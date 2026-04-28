package com.example.bluetoothscanner.data.scanner

// 用來壓制 MissingPermission 警告，權限會在 UI 層或工具類處理
import android.annotation.SuppressLint
// 用來執行 Classic Bluetooth 掃描
import android.bluetooth.BluetoothAdapter
// 用來取得掃描到的藍牙裝置資訊
import android.bluetooth.BluetoothDevice
// 用來取得 BluetoothAdapter
import android.bluetooth.BluetoothManager
// 用來接收 Classic Bluetooth 掃描結果廣播
import android.content.BroadcastReceiver
// 用來取得系統服務與註冊 BroadcastReceiver
import android.content.Context
//用來接收藍牙廣播事件資料
import android.content.Intent
// 用來指定 BroadcastReceiver 要接收哪些事件
import android.content.IntentFilter
import android.location.LocationManager
// 用來判斷 Android 版本
import android.os.Build
import android.util.Log
// 用來相容註冊 BroadcastReceiver
import androidx.core.content.ContextCompat
// 讓 Hilt 注入 App 層級 Context
import dagger.hilt.android.qualifiers.ApplicationContext
import com.example.bluetoothscanner.data.model.ScanDevice
import com.example.bluetoothscanner.utils.PermissionUtils
// 讓 callbackFlow 結束時可以清理資源
import kotlinx.coroutines.channels.awaitClose
// 將 BroadcastReceiver callback 轉成 Flow
import kotlinx.coroutines.flow.callbackFlow
// 作為 startScan 的回傳型別
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * startScan() 被呼叫
 *         ↓
 * callbackFlow 開始
 *         ↓
 * 建立 BroadcastReceiver
 *         ↓
 * 註冊 BroadcastReceiver
 *         ↓
 * bluetoothAdapter.startDiscovery()
 *         ↓
 * ┌───────────────────────────────┐
 * │  等待藍牙系統廣播              │
 * │                               │
 * │  掃到裝置 → onReceive()       │
 * │           → trySend(device)  │──→ ViewModel 的 collect 收到資料
 * │                               │
 * │  掃描完成 → onReceive()       │
 * │           → close()          │──→ Flow 結束
 * └───────────────────────────────┘
 *         ↓
 * awaitClose 執行
 *         ↓
 * cancelDiscovery()
 * unregisterReceiver()
 * 清理完畢
 */
class ClassicBluetoothScannerImpl @Inject constructor(

    /**
     * 注入 Application Context，避免持有 Activity Context 造成記憶體洩漏
     *
     * Android 的 Context
     * ├── Application Context  → 整個 App 活著就活著
     * └── Activity Context     → Activity 活著才活著
     *
     * 若：private val context: Context - 如果傳入的是 Activity Context
     *    使用者離開頁面 Activity 應該要被銷毀，但 ClassicBluetoothScannerImpl 還活著，
     *    還持有 Activity Context，導致 Activity 無法被 GC 回收
     */
    @ApplicationContext private val context: Context

) : ClassicBluetoothScanner {

    // 從系統服務取得 BluetoothManager
    private val bluetoothManager: BluetoothManager? =
        context.getSystemService(BluetoothManager::class.java)

    // 從 BluetoothManager 取得 BluetoothAdapter
    private val bluetoothAdapter: BluetoothAdapter? =
        bluetoothManager?.adapter

    // 開始 Classic Bluetooth 掃描
    @SuppressLint("MissingPermission")
    /**
     * 傳統設備：藍牙掃描是用 BroadcastReceiver 回呼（callback） 的方式回傳資料
     * 傳統 callback 方式：
     *   藍牙系統 ──→ onReceive() 被呼叫 ──→ 你在裡面處理資料
     *
     * 但 ViewModel 想要的是 Flow：
     *   藍牙系統 ──→ Flow 發出資料 ──→ ViewModel 收集
     * callbackFlow 就是把 callback 轉成 Flow 的橋樑
     *   設定 callback（BroadcastReceiver） - 啟動掃描 - callback 觸發時用 trySend 送資料 - 結束時用 close 關閉 - awaitClose 清理資源
     *
     * BroadcastReceiver          callbackFlow           ViewModel
     * onReceive() 被呼叫  ──→  trySend(scanDevice) ──→  collect { device -> }
     */
    override fun startScan(): Flow<ScanDevice> = callbackFlow {

        // 1.裝置不支援藍牙
        if (bluetoothAdapter == null) {
            close(Exception("此裝置不支援藍牙"))
            return@callbackFlow
        }

        // 2.如果目前已經在掃描，先取消舊掃描
        if (bluetoothAdapter.isDiscovering) {
            bluetoothAdapter.cancelDiscovery()
        }

        // 3.檢查藍牙開關是否打開
        if (!bluetoothAdapter.isEnabled) {
            close(Exception("藍牙目前已關閉，請先開啟藍牙"))
            return@callbackFlow
        }

        // 4.位置服務（GPS 開關）未開啟
        // Classic BT startDiscovery() 在大多數裝置（包含 MIUI）上需要系統層級的定位服務開啟
        if (!PermissionUtils.isLocationEnabled(context)) {
            close(Exception("請先至手機「設定 → 定位」開啟位置服務後再掃描"))
            return@callbackFlow
        }

        // 建立 BroadcastReceiver，用來接收 Classic Bluetooth 掃描事件
        val receiver = object : BroadcastReceiver() {

            // 當系統發出符合條件的廣播時會呼叫此方法
            override fun onReceive(context: Context?, intent: Intent?) {

                // 如果 intent 為空，直接結束
                if (intent == null) return

                /**
                 * 根據藍牙廣播事件類型處理不同邏輯：
                 *
                 * 掃描相關
                 * BluetoothDevice.ACTION_FOUND                    // 掃到一個裝置
                 * BluetoothAdapter.ACTION_DISCOVERY_STARTED       // 掃描開始
                 * BluetoothAdapter.ACTION_DISCOVERY_FINISHED      // 掃描結束
                 *
                 * 藍牙狀態相關
                 * BluetoothAdapter.ACTION_STATE_CHANGED           // 藍牙開關狀態改變（開/關）
                 *
                 * 配對相關
                 * BluetoothDevice.ACTION_BOND_STATE_CHANGED       // 配對狀態改變
                 * BluetoothDevice.ACTION_PAIRING_REQUEST          // 收到配對請求
                 *
                 * 連線相關
                 * BluetoothDevice.ACTION_ACL_CONNECTED            // 裝置連線成功
                 * BluetoothDevice.ACTION_ACL_DISCONNECTED         // 裝置斷線
                 * BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED // 裝置請求斷線
                 */
                when (intent.action) {

                    // 掃描到 Classic Bluetooth 裝置時會收到 ACTION_FOUND
                    BluetoothDevice.ACTION_FOUND -> {
                        // 從 Intent 中取得 BluetoothDevice
                        val device = getBluetoothDevice(intent)

                        // 從 Intent 中取得 RSSI；若沒有 RSSI 則回傳 null
                        val rssi = if (intent.hasExtra(BluetoothDevice.EXTRA_RSSI)) {
                            intent.getShortExtra(
                                BluetoothDevice.EXTRA_RSSI,
                                Short.MIN_VALUE
                            ).toInt()
                        } else {
                            null
                        }

                        // 如果有取得裝置資料，就轉成 ScanDevice
                        if (device != null && device.name != null) {
                            // 建立 App 內部使用的 ScanDevice 資料
                            val scanDevice = ScanDevice(
                                name = device.name,
                                address = device.address,
                                rssi = rssi,
                                type = "Classic",
                                bonded = device.bondState == BluetoothDevice.BOND_BONDED
                            )

                            /**
                             * 將掃描結果送出給 Flow 收集端
                             * send：暫停等待，直到資料被收走（需要在 suspend 函式裡）
                             * trySend：嘗試送出，送不出去就放棄，不會暫停（callback 裡只能用這個）
                             */
                            trySend(scanDevice)
                        }
                    }

                    /**
                     * 情況二：掃描完成
                     * Classic Bluetooth 掃描完成時會收到 ACTION_DISCOVERY_FINISHED
                     *
                     * 掃描結束的情況
                     * ├── 1. 時間到（約12秒）→ 自動結束
                     * ├── 2. 手動呼叫 cancelDiscovery() → 提前結束
                     * └── 3. 藍牙被關掉 → 強制結束
                     */
                    BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                        Log.d("tutu", "ACTION_DISCOVERY_FINISHED")
                        // 關閉 Flow，表示本次掃描結束
                        close()
                    }
                }
            }
        }

        // 建立 IntentFilter，指定要接收的藍牙廣播事件（過濾器）
        val filter = IntentFilter().apply {
            // 提高 IntentFilter 優先級，防止被攔截
//            priority = IntentFilter.SYSTEM_HIGH_PRIORITY

            // 接收掃描到裝置的事件
            addAction(BluetoothDevice.ACTION_FOUND)

            // 接收掃描完成的事件
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        }

        // 註冊 BroadcastReceiver
        ContextCompat.registerReceiver(
            // 使用 Application Context 註冊
            context,

            // 傳入剛剛建立的 receiver
            receiver,

            // 傳入要監聽的事件 filter
            filter,

            // 設定 receiver 對外部 App 開放
            ContextCompat.RECEIVER_EXPORTED
        )

        // 開始 Classic Bluetooth 掃描
        val started = bluetoothAdapter.startDiscovery()
        if (started) {
            Log.d("tutu", "指令發送成功，等待 STARTED 廣播...")
        }

        // 5.掃描啟動失敗，關閉 Flow 並帶入原因（可能是定位服務未開啟或權限不足）
        if (!started) {
            close(Exception("startDiscovery() returned false，請確認已開啟定位服務與相關權限"))
        }

        /**
         * Flow 結束或被取消時，自動執行清理掃描與 receiver
         *
         * 觸發點：
         * ├── 呼叫 close() 後
         * ├── ViewModel 被清除（viewModelScope 取消）
         * ├── 收集端手動取消
         * └── 發生 Exception
         */
        awaitClose {
            // 如果仍在掃描中，取消掃描
            if (bluetoothAdapter.isDiscovering) {
                // 停止掃描
                bluetoothAdapter.cancelDiscovery()
            }

            /**
             * 取消註冊 BroadcastReceiver，避免資源洩漏
             *
             * 等同於：
             *  try {
             *     context.unregisterReceiver(receiver)
             *  } catch (e: Exception) {
             *     // 忽略錯誤
             *  }
             */
            runCatching {
                context.unregisterReceiver(receiver)
            }
        }
    }

    // 停止 Classic Bluetooth 掃描
    override fun stopScan() {
        // 如果藍牙正在掃描中，就取消掃描
        if (bluetoothAdapter?.isDiscovering == true) {
            bluetoothAdapter.cancelDiscovery()
        }
    }

    // 從 Intent 中安全取得 BluetoothDevice 物件
    private fun getBluetoothDevice(intent: Intent): BluetoothDevice? {

        /**
         * Android 13 以上使用新版 getParcelableExtra
         *
         * Parcelable：
         *   把物件「序列化」成可以傳遞的格式
         *   讓物件可以放進 Intent、Bundle 裡傳遞
         *
         * BluetoothDevice 本身就實作了 Parcelable，所以可以放進 Intent 傳遞
         */
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(
                BluetoothDevice.EXTRA_DEVICE,
                BluetoothDevice::class.java
            )
        } else {
            // Android 12 以下使用舊版 getParcelableExtra
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
        }
    }
}