package com.example.bluetoothscanner.ui.ble

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bluetoothscanner.data.model.ScanDevice
import com.example.bluetoothscanner.ui.components.DeviceItem
import com.example.bluetoothscanner.ui.navigation.Routes

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
fun BleScanScreen(navController: NavController) {

    // 建立假資料清單：模擬 BLE 掃描結果
    val deviceList = remember {
        listOf(
            ScanDevice(
                name = "Mi Band 8",
                address = "10:20:30:40:50:60",
                rssi = -38,
                type = "BLE",
                bonded = true
            ),
            ScanDevice(
                name = "Temp Sensor",
                address = "AA:10:BB:20:CC:30",
                rssi = -67,
                type = "BLE",
                bonded = false
            ),
            ScanDevice(
                name = null,
                address = "DE:AD:BE:EF:11:22",
                rssi = -80,
                type = "BLE",
                bonded = false
            )
        )
    }

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
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "開始掃描")
                }

                // 停止掃描按鈕
                Button(
                    onClick = {
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "停止掃描")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 目前掃描狀態
            Text(
                text = "目前狀態：顯示假資料中",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 掃描結果區塊標題
            Text(
                text = "掃描結果",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 多筆裝置清單
            LazyColumn(
                // 讓清單填滿整個寬度。
                modifier = Modifier.fillMaxWidth(),
                // 設定每一筆 item 之間的間距。
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 逐筆顯示裝置資料
                items(deviceList) { device ->
                    // 單筆裝置資訊。
                    DeviceItem(
                        device = device,
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