package com.example.bluetoothscanner.ui.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

// 宣告 DeviceDetailScreen 裝置詳細資訊頁面
@Composable
fun DeviceDetailScreen(navController: NavController, address: String) {

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                // 設定頁面四周內距。
                .padding(24.dp)
        ) {

            // 標題
            Text(
                text = "裝置詳細資訊",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 返回按鈕
            Button(
                onClick = {
                    // 點擊後返回上一頁
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "返回")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 裝置名稱
            Text(
                text = "裝置名稱：Unknown Device",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(12.dp))

            /**
             * MAC Address
             * Media Access Control Address，是每個藍牙裝置的唯一硬體識別碼
             * 格式：AA:BB:CC:DD:EE:FF（6 組 16 進位）
             * 用途：識別「這是哪台裝置」
             * 類似網路卡的 MAC，每台裝置出廠時就燒錄在晶片裡
             */
            Text(
                text = "裝置 MAC Address：$address",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(12.dp))

            /**
             * RSSI
             * Received Signal Strength Indicator，接收訊號強度指標
             * 單位：dBm（負數）
             * 數值越接近 0 → 訊號越強（距離越近）
             * 數值越負 → 訊號越弱（距離越遠）
             */
            Text(
                text = "RSSI：-- dBm",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 顯示裝置類型
            Text(
                text = "裝置類型：Unknown",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 是否已配對
            Text(
                text = "是否已配對：否",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}