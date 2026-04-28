package com.example.bluetoothscanner.di

import com.example.bluetoothscanner.data.repository.BluetoothScanRepository
import com.example.bluetoothscanner.data.repository.BluetoothScanRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt 知識
 * 核心思想：不要自己 new 物件，你告訴 Hilt 你需要什麼，Hilt 來幫你準備好（DI 依賴注入）
 * 核心概念：
 *   Component（容器）— 管理依赖的生命周期
 *     建立物件
 *     決定物件活多久（生命週期）
 *     提供物件給需要的地方
 *     @InstallIn 的所有可選值（ex）：SingletonComponent - 应用 - App 活着就活着、ActivityRetainedComponent - 视图模型 - 旋轉螢幕不死、ActivityComponent - 活动 - Activity 活着就活着
 *   Module — 告诉Hilt 「怎么建立物件」（一份食谱）
 *     告诉Hilt：這個東西怎麼做出來、它的材料（依賴）是什麼
 *     @Provides — 你自己寫建立邏輯
 *     @Binds— 告诉Hilt 介面对应哪个实作
 *   Scope — 决定同一个Component 内要不要共用同一个实例
 *     各 Component 对应的 Scope（ex）：
 *        SingletonComponent - @Singleton
 *           App 啟動 → 建立物件
 *           App 活著 → 永遠同一個
 *           App 關閉 → 物件消滅
 *        ActivityRetainedComponent - @ActivityRetainedScoped
 *           Activity 啟動 → 建立物件
 *           手機旋轉（Activity 重建）→ 還是同一個物件  ← 這是跟 ActivityScoped 的差別
 *           Activity 真正關閉（按返回）→ 物件消滅
 *        ActivityComponent - @ActivityScoped
 *           Activity A 啟動 → 建立物件 X
 *           Activity A 裡的所有地方注入 → 都拿到物件 X
 *           Activity A 關閉 → 物件 X 消滅
 *           Activity B 啟動 → 建立物件 Y（全新的）
 *
 * 流程圖：
 *   你寫 @Inject
 *        ↓
 * Hilt 查詢對應的 Component（看你在哪裡注入）
 *        ↓
 * Component 去找 Module 的食譜
 *        ↓
 * 看有沒有 @Scope → 有：拿已存在的實例
 *                → 沒有：建立新的
 *        ↓
 *     注入給你
 *
 * 概括：
 *   Component → 决定「活多久」
 *   Module → 决定「怎么做出来」
 *   Scope → 决定「要不要共用同一个」
 *   @Inject → 「我要用这个」
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    // 當需要 BluetoothScanRepository 時，Hilt 會提供 BluetoothScanRepositoryImpl
    abstract fun bindBluetoothScanRepository(
        // 傳入 BluetoothScanRepositoryImpl 實作類別
        impl: BluetoothScanRepositoryImpl
    ): BluetoothScanRepository
}