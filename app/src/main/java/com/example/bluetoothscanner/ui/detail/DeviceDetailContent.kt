package com.example.bluetoothscanner.ui.detail

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bluetoothscanner.data.model.ScanDevice

/**
 * DeviceDetailContent：專門根據 UiState 切換不同的內容畫面
 * uiState：傳入目前畫面狀態
 */
@Composable
fun DeviceDetailContent(
    uiState: DeviceDetailUiState
) {

    // 根據不同的 UiState 顯示對應 UI
    when (uiState) {

        // Loading 狀態：載入中畫面
        DeviceDetailUiState.Loading -> {
            LoadingContent()
        }

        // Success 狀態：裝置資訊畫面
        is DeviceDetailUiState.Success -> {
            DeviceInfoContent(device = uiState.device)
        }

        // Error 狀態：錯誤畫面
        is DeviceDetailUiState.Error -> {
            ErrorContent(message = uiState.message)
        }
    }
}

// 載入中
@Composable
fun LoadingContent() {

    // 圓形載入指示器
    CircularProgressIndicator()

    Spacer(modifier = Modifier.height(16.dp))

    // 文本
    Text(
        text = "資料載入中...",
        style = MaterialTheme.typography.bodyLarge
    )
}

// 裝置詳細資訊
@Composable
fun DeviceInfoContent(
    device: ScanDevice
) {

    // 裝置名稱
    Text(
        text = "裝置名稱：${device.name ?: "Unknown Device"}",
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
        text = "MAC Address：${device.address}",
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
        text = "RSSI：${device.rssi ?: 0} dBm",
        style = MaterialTheme.typography.bodyLarge
    )

    Spacer(modifier = Modifier.height(12.dp))

    // 裝置類型
    Text(
        text = "裝置類型：${device.type}",
        style = MaterialTheme.typography.bodyLarge
    )

    Spacer(modifier = Modifier.height(12.dp))

    // 是否已配對
    Text(
        text = "是否已配對：${if (device.bonded) "是" else "否"}",
        style = MaterialTheme.typography.bodyLarge
    )
}

// 錯誤訊息
@Composable
fun ErrorContent(
    message: String
) {

    // 錯誤訊息
    Text(
        text = "錯誤：$message",
        style = MaterialTheme.typography.bodyLarge
    )
}