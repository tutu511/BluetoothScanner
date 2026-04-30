package com.example.bluetoothscanner.ui.ble

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bluetoothscanner.data.model.ScanDevice
import com.example.bluetoothscanner.ui.components.DeviceItem
import com.example.bluetoothscanner.ui.navigation.Routes
import com.example.bluetoothscanner.utils.PermissionUtils

/**
 * 宣告 BleScanScreen BLE 掃描頁面
 * API：BluetoothLeScanner.startScan()
 * 用途：智慧手錶、心率帶、IoT 感測器、Beacon、血糖機
 * 傳輸速度：較慢，適合小量資料
 * 耗電量：極低（核心）
 * 掃描時間：可自訂，持續掃描或設定 filter
 * 掃描結果：透過 ScanCallback 回調
 * 連線方式：不一定需要配對，可直接連線
 */
@Composable
fun BleScanScreen(navController: NavController, viewModel: BleScanViewModel = hiltViewModel()) {

    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current

    // 取得 BLE 掃描需要的權限清單
    val permissions = PermissionUtils.bleBluetoothPermissions()

    // 建立多權限請求 launcher，權限請求需要跟 Android 系統互動，彈出對話框給使用者看，這是 UI 層的職責
    val permissionLauncher = rememberLauncherForActivityResult(
        // 使用 RequestMultiplePermissions 一次請求多個權限
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        // 權限請求完成後會回傳每個權限的授權結果
        onResult = { result ->

            // 檢查是否所有必要權限都已授權
            val allGranted = result.values.all { granted ->
                granted
            }

            // 如果全部授權，開始掃描
            if (allGranted) {
                viewModel.startScan()
            }
        }
    )

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {

            // 標題
            Text(
                text = "BLE 掃描",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 按鈕：返回、開始掃描、停止掃描
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // 返回首頁按鈕
                Button(
                    onClick = {
                        // 點擊後返回上一頁
                        navController.popBackStack()
                    },
                    // 讓按鈕平均分配寬度
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "返回")
                }

                // 開始掃描按鈕
                Button(
                    onClick = {
                        // 判斷所有必要權限是否都已授權
                        val allGranted = permissions.all { permission ->
                            ContextCompat.checkSelfPermission(
                                context,
                                permission
                            ) == PackageManager.PERMISSION_GRANTED
                        }

                        // 如果已經全部授權，直接開始掃描
                        if (allGranted) {
                            viewModel.startScan()
                        } else {
                            // 如果尚未授權，向使用者請求權限。
                            permissionLauncher.launch(permissions)
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "開始掃描")
                }

                // 停止掃描按鈕
                Button(
                    onClick = {
                        viewModel.stopScan()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "停止掃描")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 目前掃描狀態
            Text(
                text = uiState.statusMessage,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 正在載入中
            if (uiState.isLoading) {
                CircularProgressIndicator()
            }

            // 顯示錯誤內容
            uiState.errorMessage?.let { errorMessage ->

                Spacer(modifier = Modifier.height(16.dp))

                // 顯示錯誤訊息
                Text(
                    text = "錯誤：$errorMessage",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 掃描結果區塊標題
            Text(
                text = "掃描結果",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 多筆裝置清單
            if (uiState.deviceList.isEmpty()) {

                // 空清單提示
                Text(
                    text = "目前尚無掃描結果",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {

                // 使用 LazyColumn 顯示 BLE 裝置清單
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    // 裝置資料
                    items(uiState.deviceList) { device ->

                        DeviceItem(
                            device = device,
                            // 點擊：傳 address 到詳細頁
                            onClick = {
                                navController.navigate(
                                    Routes.deviceDetailRoute(device.address)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}