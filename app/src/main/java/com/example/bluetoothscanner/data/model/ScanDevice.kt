package com.example.bluetoothscanner.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// 宣告 ScanDevice 資料類別，用來表示一個藍牙裝置的資訊
@Parcelize
data class ScanDevice(

    // 裝置名稱
    val name: String?,

    // 裝置的 MAC Address（唯一識別）
    val address: String,

    // 訊號強度（RSSI），單位為 dBm
    val rssi: Int?,

    // 裝置類型（例如：Classic、BLE）
    val type: String,

    // 是否已經配對（bonded）
    val bonded: Boolean

): Parcelable