package com.example.bluetoothscanner.data.repository

import com.example.bluetoothscanner.data.model.ScanDevice
import com.example.bluetoothscanner.data.scanner.ClassicBluetoothScanner
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BluetoothScanRepositoryImpl @Inject constructor(
    // 注入 ClassicBluetoothScanner，讓 Repository 可以呼叫 Classic 掃描功能
    private val classicBluetoothScanner: ClassicBluetoothScanner

) : BluetoothScanRepository {

    // 開始 Classic Bluetooth 掃描
    override fun startClassicScan(): Flow<ScanDevice> {

        // 轉呼叫底層 ClassicBluetoothScanner，取得掃描結果資料流
        return classicBluetoothScanner.startScan()
    }

    // 停止 Classic Bluetooth 掃描
    override fun stopClassicScan() {

        // 轉呼叫底層 ClassicBluetoothScanner，停止掃描
        classicBluetoothScanner.stopScan()
    }
}