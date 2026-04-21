package com.example.bluetoothscanner.ui.home

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
// 匯入 NavController，讓畫面可以做頁面跳轉
import androidx.navigation.NavController
// 匯入路由常數
import com.example.bluetoothscanner.ui.navigation.Routes

// 宣告 HomeScreen，這是 App 的首頁畫面。
@Composable
fun HomeScreen(navController: NavController) {

    // 使用 Surface 作為整個首頁畫面的最外層容器
    Surface(
        // 讓 Surface 填滿整個畫面
        modifier = Modifier.fillMaxSize()
    ) {

        // 使用 Column 讓畫面元件垂直排列
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {

            // 標題
            Text(
                text = "藍牙掃描",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 說明
            Text(
                text = "請選擇掃描模式",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 顯示 Classic 掃描按鈕。
            Button(
                onClick = {
                    // 點擊後跳轉到 Classic 掃描頁
                    navController.navigate(Routes.CLASSIC_SCAN)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Classic 掃描")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 顯示 BLE 掃描按鈕
            Button(
                onClick = {
                    // 點擊後跳轉到 BLE 掃描頁
                    navController.navigate(Routes.BLE_SCAN)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "BLE 掃描")
            }
        }
    }
}