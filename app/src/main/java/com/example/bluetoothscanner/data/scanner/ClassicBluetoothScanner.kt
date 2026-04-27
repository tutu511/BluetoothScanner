package com.example.bluetoothscanner.data.scanner

// 讓掃描結果可以用資料流方式持續回傳
import kotlinx.coroutines.flow.Flow
import com.example.bluetoothscanner.data.model.ScanDevice

// 定義 Classic Bluetooth 掃描器應該提供的功能
interface ClassicBluetoothScanner {

    // 開始掃描 Classic Bluetooth 裝置，並用 Flow 持續送出掃描結果
    fun startScan(): Flow<ScanDevice>

    // 停止 Classic Bluetooth 掃描
    fun stopScan()
}