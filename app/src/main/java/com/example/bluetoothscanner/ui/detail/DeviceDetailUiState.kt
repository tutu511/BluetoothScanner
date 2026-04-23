package com.example.bluetoothscanner.ui.detail

import com.example.bluetoothscanner.data.model.ScanDevice

/**
 * 使用 sealed interface 表示畫面可能出現的所有狀態
 * sealed interface 密封介面，限制「這個介面只能被固定幾種類別實作」
 *
 * data class：資料類別，自動幫你產生
 *   equals() → 比較內容
 *   hashCode() → 雜湊值
 *   toString() → 印出內容
 *   copy() → 複製並修改部分欄位
 *
 * data object：資料單例，就是 data class + object 的結合，代表唯一一個實例、沒有任何欄位 = enum 單一值 / Singleton
 *   自動產生：
 *     toString() → 印出名稱
 *     equals() / hashCode() → 單例永遠相等
 *
 * DeviceDetailUiState
 * ├── Loading              → 什麼資料都不需要，就是「載入中」這個狀態
 * ├── Success(device)      → 需要帶著裝置資料
 * └── Error(message)       → 需要帶著錯誤訊息
 *
 *
 * sealed interface 讓狀態永遠只有一種，不會衝突
 * data object 用在不需要資料的狀態
 * data class 用在需要攜帶資料的狀態
 * : DeviceDetailUiState 告訴編譯器我是這個家族的成員【implements】
 */
sealed interface DeviceDetailUiState {

    // 正在載入資料中
    data object Loading : DeviceDetailUiState

    // 已成功取得裝置資料
    data class Success(
        // 傳入成功取得的裝置資料
        val device: ScanDevice
    ) : DeviceDetailUiState

    // 發生錯誤
    data class Error(
        // 傳入錯誤訊息。
        val message: String
    ) : DeviceDetailUiState
}