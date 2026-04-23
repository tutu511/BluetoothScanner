package com.example.bluetoothscanner.ui.classic

import com.example.bluetoothscanner.data.model.ScanDevice

// Classic 掃描頁所有需要的畫面狀態
data class ClassicScanUiState(
    // 是否正在掃描中
    val isScanning: Boolean = false,

    // 是否正在載入中
    val isLoading: Boolean = false,

    // 掃描狀態文本
    val statusMessage: String = "目前狀態：尚未開始掃描",

    // 掃描結果清單
    val deviceList: List<ScanDevice> = emptyList(),

    // 錯誤信息
    val errorMessage: String? = null

)