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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
// hiltViewModel：Compose 直接取得 Hilt 管理的 ViewModel
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

/**
 * DeviceDetailScreen：裝置詳細資訊頁面
 * val：唯讀變數，值不能重新賦值
 * collectAsState：把 StateFlow 轉成 Compose 能認識的 State<T>
 */
@Composable
fun DeviceDetailScreen(
    navController: NavController,
    // 由 Hilt 自動提供 DeviceDetailViewModel
    viewModel: DeviceDetailViewModel = hiltViewModel()
) {
    // 觀察 ViewModel 的 uiState，讓畫面能隨狀態改變自動更新
    val uiState by viewModel.uiState.collectAsState()

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

            // 根據 uiState 切換不同的內容區塊
            DeviceDetailContent(uiState = uiState)
        }
    }
}