package com.example.bluetoothscanner.data.scanner

import com.example.bluetoothscanner.data.model.ScanDevice
import kotlinx.coroutines.flow.Flow

// 定義 BLE 掃描器應該提供的功能
interface BleBluetoothScanner {

    // 開始 BLE 掃描，並用 Flow 持續回傳掃描到的裝置
    fun startScan(): Flow<ScanDevice>

    // 停止 BLE 掃描
    fun stopScan()

}