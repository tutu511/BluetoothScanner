package com.example.bluetoothscanner

// 匯入 Android 的 Application 類別，因為要自訂整個 App 的 Application
import android.app.Application
// 匯入 Hilt 提供的註解，讓 Hilt 知道這是 App 層級的入口
import dagger.hilt.android.HiltAndroidApp

/**
 * Hilt 需要一個「全 App 的根節點」來建立依賴圖。
 * 這個根節點就是 Application 類別
 * 標記這個 Application 類別為 Hilt 的根入口，讓 Hilt 產生整個 App 的依賴注入容器
 */
@HiltAndroidApp
// 建立自訂的 Application 類別，並繼承 Android 的 Application
class BluetoothScannerApp : Application()

