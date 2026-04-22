package com.example.bluetoothscanner.ui.navigation

// 匯入 Compose 基礎函式註解
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
// 匯入 Navigation Compose 的 NavHost
import androidx.navigation.compose.NavHost
// 匯入 Navigation Controller 建立方法
import androidx.navigation.compose.rememberNavController
// 匯入 composable，讓我們可以定義畫面
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
// 匯入首頁畫面（之後會建立）
import com.example.bluetoothscanner.ui.home.HomeScreen
// 匯入 Classic 掃描頁（之後會建立）
import com.example.bluetoothscanner.ui.classic.ClassicScanScreen
// 匯入 BLE 掃描頁（之後會建立）
import com.example.bluetoothscanner.ui.ble.BleScanScreen
// 匯入裝置詳情頁（之後會建立）
import com.example.bluetoothscanner.ui.detail.DeviceDetailScreen

// 宣告 AppNavHost，這是整個 App 的導航控制中心
@Composable
fun AppNavHost() {

    // 建立 NavController，用來控制頁面跳轉
    val navController = rememberNavController()

    // 建立導航容器
    NavHost(
        // 傳入 NavController
        navController = navController,

        // 設定預設起始畫面
        startDestination = Routes.HOME
    ) {

        // 定義首頁路由
        composable(Routes.HOME) {

            // 顯示首頁畫面，並傳入 navController 讓它可以做頁面跳轉
            HomeScreen(navController)
        }

        // 定義 Classic 掃描頁
        composable(Routes.CLASSIC_SCAN) {

            // 顯示 Classic 掃描畫面
            ClassicScanScreen(navController)
        }

        // 定義 BLE 掃描頁
        composable(Routes.BLE_SCAN) {

            // 顯示 BLE 掃描畫面
            BleScanScreen(navController)
        }

        // 定義裝置詳細頁路由，並需要一個 address 參數
        composable(
            // 設定這個頁面的完整路由格式
            route = Routes.DEVICE_DETAIL_ROUTE,
            // 定義需要接收的導航參數清單
            arguments = listOf(
                // address 參數
                navArgument("address") {
                    // address 的型別為字串
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            // 從導航參數中取出 address，如果取不到就給空字串
            val address = backStackEntry.arguments?.getString("address").orEmpty()

            // 裝置詳細頁，並把 address 傳進去
            DeviceDetailScreen(
                navController = navController,
                address = address
            )
        }
    }
}