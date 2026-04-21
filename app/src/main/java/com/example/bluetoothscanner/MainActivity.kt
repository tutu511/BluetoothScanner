package com.example.bluetoothscanner

// 匯入 Bundle，因為 onCreate 會用到它接收啟動狀態資料
import android.os.Bundle
// 匯入 Compose 專案應該繼承的 ComponentActivity
import androidx.activity.ComponentActivity
// 匯入 setContent，讓 Activity 可以載入 Compose 畫面
import androidx.activity.compose.setContent
// 匯入 Hilt 的 AndroidEntryPoint 註解，讓這個 Activity 可以使用依賴注入
import dagger.hilt.android.AndroidEntryPoint
// 匯入 App 的導航主入口。
import com.example.bluetoothscanner.ui.navigation.AppNavHost
// 匯入 Compose 主題。
import com.example.bluetoothscanner.ui.theme.BluetoothScannerTheme

// 標記這個 Activity 為 Hilt 注入入口
@AndroidEntryPoint
// 宣告 MainActivity 並繼承 ComponentActivity（Compose 專案標準寫法）
class MainActivity : ComponentActivity() {

    // 覆寫 Activity 的 onCreate，作為畫面建立的入口
    override fun onCreate(savedInstanceState: Bundle?) {
        // 呼叫父類別 onCreate，確保系統初始化正常
        super.onCreate(savedInstanceState)
        // 設定 Compose UI 畫面內容
        setContent {

            // 套用 App 主題（顏色、字體等）
            BluetoothScannerTheme {

                // 載入整個 App 的導航入口
                AppNavHost()

            }
        }
    }
}