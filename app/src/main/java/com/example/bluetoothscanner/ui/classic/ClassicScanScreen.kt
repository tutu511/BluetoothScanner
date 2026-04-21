package com.example.bluetoothscanner.ui.classic

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


/**
 * 宣告 ClassicScanScreen，這是 Classic Bluetooth 掃描頁面
 * API：BluetoothAdapter.startDiscovery()
 * 用途：耳機、喇叭、鍵盤、滑鼠、手機對傳檔案
 * 傳輸速度：快，適合大量資料（音訊串流）
 * 耗電量：較高
 * 掃描時間：約 12 秒，系統控制，無法自訂
 * 掃描結果：透過廣播 ACTION_FOUND 接收
 * 連線方式：需要配對
 */
@Composable
fun ClassicScanScreen(navController: NavController) {

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
                text = "目前狀態：尚未開始掃描",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 掃描結果區塊標題
            Text(
                text = "掃描結果",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "目前尚無掃描結果",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}