package com.example.bluetoothscanner.ui.ble

import com.example.bluetoothscanner.data.model.ScanDevice

// BLE 掃描頁所有畫面狀態
data class BleScanUiState (
    // 是否正在掃描
    val isScanning: Boolean = false,

    // 狀態文字
    val statusMessage: String = "目前狀態：尚未開始掃描",

    // 結果清單
    val deviceList: List<ScanDevice> = emptyList(),

    // 是否正在加載
    val isLoading: Boolean = false,

    // 錯誤信息
    val errorMessage: String? = null
)