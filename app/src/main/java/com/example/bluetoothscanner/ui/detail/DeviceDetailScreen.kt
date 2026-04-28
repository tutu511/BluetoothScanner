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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
// hiltViewModel：Compose 直接取得 Hilt 管理的 ViewModel
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.bluetoothscanner.data.model.ScanDevice
import com.example.bluetoothscanner.ui.navigation.Routes

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
    /**
     * 取得上一頁傳來的 ScanDevice
     *
     * BackStack：
     * [ HOME, CLASSIC_SCAN(A), DEVICE_DETAIL(B) ]
     *                 ↑                   ↑
     *     previousBackStackEntry    currentBackStackEntry
     *     （資料存在這裡）           （B 從這裡往前拿）
     */
    val device = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<ScanDevice>(Routes.SELECTED_DEVICE)

    /**
     * 在 Composable 裡安全地啟動協程，並且綁定生命週期
     * Composable 函式會不斷重組（recompose），每次狀態改變都可能重新執行
     *
     * LaunchedEffect 保證
     *   Composable 進入畫面時啟動協程
     *   Composable 離開畫面時自動取消協程
     *   根據 key 決定要不要重新執行（device 改變了就執行一次）
     *
     */
    LaunchedEffect(device) {
        if (device != null) {
            viewModel.setDevice(device)
        } else {
            viewModel.setError("找不到裝置資料。")
        }
    }

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

            // 根據 uiState 切換不同的內容區塊
            DeviceDetailContent(uiState = uiState)
        }
    }
}