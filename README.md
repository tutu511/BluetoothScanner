
# 📱 BluetoothScanner

## 🧾 專案介紹

**BluetoothScanner** 是一款使用現代 Android 技術打造的藍牙掃描應用程式，支援 **Classic Bluetooth** 與 **Bluetooth Low Energy (BLE)** 兩種模式，讓使用者可以即時搜尋並查看附近的藍牙裝置資訊。

本專案採用 **Jetpack Compose + MVVM + Hilt** 架構設計，展示 Android 現代開發最佳實踐。

---

## 🚀 功能特色

* 🔍 支援 **Classic Bluetooth 掃描**
* 📡 支援 **BLE（低功耗藍牙）掃描**
* 📋 顯示裝置清單（名稱、MAC Address、RSSI）
* 🔄 即時更新掃描結果
* 📄 裝置詳細資訊頁面
* 🎯 去重處理（避免重複裝置）
* ⚡ 掃描開始 / 停止控制

---

## 🧱 技術架構

本專案採用現代 Android 開發架構：

* **Language**：Kotlin
* **UI**：Jetpack Compose
* **Architecture**：MVVM（Model-View-ViewModel）
* **Dependency Injection**：Hilt
* **Navigation**：Navigation Compose
* **State Management**：StateFlow + ViewModel

---

## 📂 專案結構

```text
ui/           → 畫面層（Compose Screen）
data/         → 資料層（Repository / Scanner）
di/           → Hilt 依賴注入
utils/        → 工具類
```

---

## 🎯 學習重點

此專案主要用於練習與展示：

* Android 藍牙開發（Classic / BLE）
* Compose UI 設計
* MVVM 架構拆分
* Hilt 依賴注入
* Navigation Compose
* 狀態管理（StateFlow）

---

## 📌 使用場景

* 藍牙設備偵測工具
* IoT 裝置掃描
* BLE 裝置開發測試
* Android 架構學習範例

---

## 💡 未來擴充

* 🔗 裝置連線功能（GATT / Socket）
* 📊 BLE 資料解析（Service / Characteristic）
* 📍 RSSI 距離估算
* 🔍 搜尋與過濾功能
* 🧪 單元測試與 UI 測試
