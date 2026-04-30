package com.example.bluetoothscanner.ui.classic

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bluetoothscanner.ui.components.DeviceItem
import com.example.bluetoothscanner.ui.navigation.Routes
import com.example.bluetoothscanner.utils.PermissionUtils


/**
 * 宣告 ClassicScanScreen 掃描頁面
 * API：BluetoothAdapter.startDiscovery()
 * 用途：耳機、喇叭、鍵盤、滑鼠、手機對傳檔案
 * 傳輸速度：快，適合大量資料（音訊串流）
 * 耗電量：較高
 * 掃描時間：約 12 秒，系統控制，無法自訂
 * 掃描結果：透過廣播 ACTION_FOUND 接收
 * 連線方式：需要配對
 */
@Composable
fun ClassicScanScreen(
    navController: NavController,
    // 由 Hilt 自動提供 ClassicScanViewModel
    viewModel: ClassicScanViewModel = hiltViewModel()
) {
    // 觀察 ViewModel 的 uiState，讓畫面隨狀態變化自動更新
    val uiState by viewModel.uiState.collectAsState()

    // 取得目前 Context，用來檢查權限
    val context = LocalContext.current

    // 取得 Classic 掃描需要的權限清單
    val permissions = PermissionUtils.classicBluetoothPermissions()

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
                text = "Classic 掃描",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 使用 Row 放置返回、開始掃描、停止掃描三個按鈕
            Row(
                modifier = Modifier.fillMaxWidth(),
                // 設定按鈕之間平均分散排列
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

            // 顯示目前狀態文字
            Text(
                text = uiState.statusMessage,
                style = MaterialTheme.typography.bodyLarge
            )

            // 如果目前正在載入中，顯示 loading 元件
            if (uiState.isLoading) {

                Spacer(modifier = Modifier.height(16.dp))

                // 圓形載入指示器
                CircularProgressIndicator()
            }

            // 如果有錯誤訊息，顯示錯誤內容
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

            // 如果沒有任何裝置資料，顯示空狀態文字。
            if (uiState.deviceList.isEmpty()) {
                // 顯示空清單提示
                Text(
                    text = "目前尚無掃描結果",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                // 使用 LazyColumn 顯示多筆裝置清單
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    // 每一筆 item 之間的間距
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 裝置資料
                    items(uiState.deviceList) { device ->
                        // 單筆裝置資訊
                        DeviceItem(
                            device = device,
                            onClick = {
                                // 把使用者點擊的 device 存到目前頁面的 SavedStateHandle。
                                navController.currentBackStackEntry
                                    ?.savedStateHandle
                                    ?.set(Routes.SELECTED_DEVICE, device)

                                // 跳轉到 Detail 頁
                                navController.navigate(Routes.DEVICE_DETAIL)
                            }
                        )
                    }
                }
            }
        }
    }
}