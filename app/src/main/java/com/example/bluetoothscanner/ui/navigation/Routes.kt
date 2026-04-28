package com.example.bluetoothscanner.ui.navigation

// 定義所有頁面路由常數，避免硬寫字串造成錯誤。
object Routes {

    // 首頁
    const val HOME = "home"

    // Classic 掃描頁
    const val CLASSIC_SCAN = "classic_scan"

    // BLE 掃描頁
    const val BLE_SCAN = "ble_scan"

    // 裝置詳情頁
    const val DEVICE_DETAIL = "device_detail"

    // SavedStateHandle 使用的 key
    const val SELECTED_DEVICE = "selected_device"

    // 建立裝置詳細頁實際導航路徑的方法，方便外層直接呼叫
    fun deviceDetailRoute(address: String): String {
        return "$DEVICE_DETAIL/$address"
    }
}