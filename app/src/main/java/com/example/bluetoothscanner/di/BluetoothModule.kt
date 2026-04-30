package com.example.bluetoothscanner.di

import com.example.bluetoothscanner.data.scanner.BleBluetoothScanner
import com.example.bluetoothscanner.data.scanner.BleBluetoothScannerImpl
import com.example.bluetoothscanner.data.scanner.ClassicBluetoothScanner
import com.example.bluetoothscanner.data.scanner.ClassicBluetoothScannerImpl
// 用來告訴 Hilt 介面要綁定哪個實作
import dagger.Binds
// 表示這是一個 Hilt 模組
import dagger.Module
// 用來指定這個模組安裝到哪個 Hilt Component
import dagger.hilt.InstallIn
// 表示這個綁定作用於整個 App 生命週期
import dagger.hilt.components.SingletonComponent
// 表示這個實例在 App 內共用同一份
import javax.inject.Singleton

// 宣告這是一個 Hilt Module，用來提供藍牙相關依賴
@Module
// 指定這個 Module 安裝到 SingletonComponent，也就是 App 等級的依賴容器
@InstallIn(SingletonComponent::class)
// 宣告 BluetoothModule，專門負責藍牙 Scanner 的依賴綁定
abstract class BluetoothModule {

    /**
     * 当有人需要 ClassicBluetoothScanner，请给他 ClassicBluetoothScannerImpl
     * 【回传型别 : ClassicBluetoothScanner 不是继承，是在告诉Hilt「这个绑定是为了哪个介面服务的」】
     *
     * @Binds 是 Hilt 的特殊规定：
     *   这个函式不需要你写实作内容（Hilt 会在编译时自动产生）
     *   沒有實作內容的函式 → 必須宣告為 abstract
     *   abstract fun 所在的 class 也必须是 abstract class
     *
     * 所以整个Module 也要是abstract class BluetoothModule
     *
     * @Inject val scanner: ClassicBluetoothScanner
     *                                     ↓
     *         Hilt 查詢 @Binds 的對應表
     *                                     ↓
     *         找到：ClassicBluetoothScanner → ClassicBluetoothScannerImpl
     *                                     ↓
     *         實際注入：ClassicBluetoothScannerImpl 的實例
     */
    // 綁定 ClassicBluetoothScanner 介面到 ClassicBluetoothScannerImpl 實作
    @Binds
    // 指定這個 Scanner 在 App 內共用同一份實例
    @Singleton
    // 當需要 ClassicBluetoothScanner 時，Hilt 會提供 ClassicBluetoothScannerImpl
    abstract fun bindClassicBluetoothScanner(
        // 傳入 ClassicBluetoothScannerImpl 實作類別
        impl: ClassicBluetoothScannerImpl
    ): ClassicBluetoothScanner


    @Binds
    @Singleton
    abstract fun bindBleBluetoothScanner(
        impl: BleBluetoothScannerImpl
    ): BleBluetoothScanner
}