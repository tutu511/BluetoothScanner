package com.example.bluetoothscanner.data.scanner

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
// 用來接收 BLE 掃描結果
import android.bluetooth.le.ScanCallback
// 用來取得 BLE 裝置資訊
import android.bluetooth.le.ScanResult
// 用來設定 BLE 掃描模式
import android.bluetooth.le.ScanSettings
import androidx.annotation.RequiresPermission
// 讓 Hilt 注入 App 層級 Context
import dagger.hilt.android.qualifiers.ApplicationContext
// 作為 App 內部統一資料模型
import com.example.bluetoothscanner.data.model.ScanDevice
// 讓 Flow 結束時可以清理掃描
import kotlinx.coroutines.channels.awaitClose
// 將 BLE callback API 轉成 Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 掃描結束時機：不會自動結束，要自己叫停
 * 結果回傳方式：ScanCallback
 * 掃描到同一裝置：傳會一直重複回傳
 *
 * startScan() 呼叫
 *         ↓
 * 檢查 bluetoothAdapter、bleScanner 是否存在
 *         ↓
 * 建立 ScanSettings（設定掃描模式）
 *         ↓
 * 建立 ScanCallback（三種情況）
 *     ├── onScanResult   → 掃到一筆裝置
 *     ├── onBatchScanResults → 批次結果回來
 *     └── onScanFailed   → 掃描失敗
 *         ↓
 * currentCallback = scanCallback  ← 記住這個 callback
 *         ↓
 * bleScanner.startScan() 開始掃描
 *         ↓
 * awaitClose 等待...（Flow 不會自動結束）
 *         ↓
 * stopScan() 被呼叫 或 協程被取消
 *         ↓
 * awaitClose 裡的 bleScanner.stopScan() 執行
 *         ↓
 * Flow 結束
 */
@SuppressLint("MissingPermission")
class BleBluetoothScannerImpl @Inject constructor(

    // 注入 Application Context，避免持有 Activity Context 造成記憶體洩漏
    @ApplicationContext private val context: Context

) : BleBluetoothScanner {

    // 從系統服務取得 BluetoothManager
    private val bluetoothManager: BluetoothManager? =
        context.getSystemService(BluetoothManager::class.java)

    // 從 BluetoothManager 取得 BluetoothAdapter
    private val bluetoothAdapter = bluetoothManager?.adapter

    // 取得 BLE Scanner - 自訂 getter 屬性寫法
    private val bleScanner get() = bluetoothAdapter?.bluetoothLeScanner

    // 保存目前的 ScanCallback，讓 stopScan 可以停止同一個 callback
    private var currentCallback: ScanCallback? = null

    // 開始 BLE 掃描
    override fun startScan(): Flow<ScanDevice> = callbackFlow {

        // 如果藍牙不支援或無法取得 BLE Scanner，直接關閉 Flow
        if (bluetoothAdapter == null || bleScanner == null) {
            close(Exception("請檢查設備是否支援藍牙，且藍牙是否開啟"))
            return@callbackFlow
        }

        /**
         * 建立 BLE 掃描設定
         *
         * setScanMode — 掃描模式：
         *   SCAN_MODE_LOW_LATENCY：最快，持續掃描，耗電最高，延遲最低
         *   SCAN_MODE_BALANCED：平衡模式，耗電中，延遲中
         *   SCAN_MODE_LOW_POWER：省電，間歇掃描，耗電最低，延遲最高
         *
         * setReportDelay — 批次回傳延遲
         *
         * setCallbackType — 觸發時機：
         *   ScanSettings.CALLBACK_TYPE_ALL_MATCHES：每次掃到都回傳（預設）
         *   ScanSettings.CALLBACK_TYPE_FIRST_MATCH：只在第一次發現裝置時回傳
         *   ScanSettings.CALLBACK_TYPE_MATCH_LOST：裝置消失時通知（需搭配 FIRST_MATCH）
         *
         * setMatchMode — 配對靈敏度：
         *   ScanSettings.MATCH_MODE_AGGRESSIVE：積極模式 - 訊號弱也回報
         *   ScanSettings.MATCH_MODE_STICKY：黏性模式 - 訊號要穩定才回報，減少誤報
         *
         * setNumOfMatches - 每個廣播週期回傳幾筆
         *   ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT：只回傳一筆
         *   ScanSettings.MATCH_NUM_FEW_ADVERTISEMENT：回傳少數幾筆
         *   ScanSettings.MATCH_NUM_MAX_ADVERTISEMENT：回傳最多筆（預設）
         *
         */
        val scanSettings = ScanSettings.Builder()
            // 使用低延遲模式，掃描速度快，但比較耗電
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            // 建立 ScanSettings
            .build()

        // 建立 BLE 掃描 callback - BLE 掃描結果的回調介面，系統掃到裝置時會主動呼叫這裡
        val scanCallback = object : ScanCallback() {

            // 每掃描到一筆 BLE 裝置時會呼叫
            override fun onScanResult(callbackType: Int, result: ScanResult) {

                // 從 ScanResult 取得 BluetoothDevice
                val device = result.device
                val deviceName = result.scanRecord?.deviceName ?: device.name

                // 建立 App 內部使用的 ScanDevice
                val scanDevice = ScanDevice(
                    name = deviceName,
                    address = device.address,
                    rssi = result.rssi,
                    type = "BLE",
                    bonded = device.bondState == BluetoothDevice.BOND_BONDED
                )

                // 將 BLE 掃描結果送出給 Flow 收集端
                trySend(scanDevice)
            }

            // 批次掃描結果回來時會呼叫
            override fun onBatchScanResults(results: MutableList<ScanResult>) {

                // 逐筆處理批次掃描結果
                results.forEach { result ->

                    // 從 ScanResult 取得 BluetoothDevice
                    val device = result.device
                    val deviceName = result.scanRecord?.deviceName ?: device.name

                    // 建立 App 內部使用的 ScanDevice
                    val scanDevice = ScanDevice(
                        name = deviceName,
                        address = device.address,
                        rssi = result.rssi,
                        type = "BLE",
                        bonded = device.bondState == android.bluetooth.BluetoothDevice.BOND_BONDED
                    )

                    // 將 BLE 掃描結果送出給 Flow 收集端
                    trySend(scanDevice)
                }
            }

            // BLE 掃描失敗時會呼叫
            override fun onScanFailed(errorCode: Int) {

                // 關閉 Flow 並回傳錯誤
                close(IllegalStateException("BLE 掃描失敗，錯誤代碼：$errorCode"))
            }
        }

        // 保存目前 callback，讓 stopScan 可以使用同一個 callback 停止掃描
        currentCallback = scanCallback

        /**
         * 開始 BLE 掃描
         *   第一個參數：filters
         *      用來過濾只掃你想要的裝置：
         *        ScanFilter.Builder()
         *           .setDeviceName("Sony WH-1000XM5")
         *           .build()
         *   第二個參數：scanSettings
         *      掃描模式、批次延遲
         *   第三個參數：scanCallback
         *      掃到裝置時系統要通知誰
         *
         */
        bleScanner?.startScan(null, scanSettings, scanCallback)

        // Flow 被取消或結束時，停止 BLE 掃描
        awaitClose {

            // 停止 BLE 掃描
            bleScanner?.stopScan(scanCallback)

            // 清除目前 callback
            if (currentCallback == scanCallback) {
                currentCallback = null
            }
        }
    }

    // 停止 BLE 掃描
    override fun stopScan() {

        // 取得目前 callback
        val callback = currentCallback

        // 如果 callback 不為空，就停止 BLE 掃描
        if (callback != null) {
            bleScanner?.stopScan(callback)
        }

        // 清除目前 callback
        currentCallback = null
    }
}