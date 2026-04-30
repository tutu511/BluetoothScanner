package com.example.bluetoothscanner.data.repository

import com.example.bluetoothscanner.data.model.ScanDevice
import kotlinx.coroutines.flow.Flow

// 作為 ViewModel 存取藍牙掃描資料的統一入口
interface BluetoothScanRepository {

    // 開始 Classic Bluetooth 掃描，並持續回傳掃描到的裝置
    fun startClassicScan(): Flow<ScanDevice>

    // 停止 Classic Bluetooth 掃描
    fun stopClassicScan()

    // 開始 BLE 掃描
    fun startBleScan(): Flow<ScanDevice>

    // 停止 BLE 掃描
    fun stopBleScan()
}