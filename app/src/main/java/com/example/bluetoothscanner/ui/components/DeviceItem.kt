package com.example.bluetoothscanner.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bluetoothscanner.data.model.ScanDevice

// 宣告 DeviceItem，顯示單一藍牙裝置資訊的共用元件
@Composable
fun DeviceItem(
    // 傳入一筆藍牙裝置資料
    device: ScanDevice,

    // 傳入點擊事件
    onClick: () -> Unit
) {

    // 使用 Card 呈現單筆裝置資料
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        colors = CardDefaults.cardColors()
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            // 裝置名稱
            Text(
                text = "裝置名稱：${device.name ?: "Unknown Device"}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // MAC Address
            Text(
                text = "MAC Address：${device.address}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(6.dp))

            // RSSI，如果為空就顯示 --。
            Text(
                text = "RSSI：${device.rssi?.toString() ?: "--"} dBm",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(6.dp))

            // 裝置類型
            Text(
                text = "裝置類型：${device.type}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(6.dp))

            // 是否已配對
            Text(
                text = "是否已配對：${if (device.bonded) "是" else "否"}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}