package com.example.bluetoothscanner.utils

import android.Manifest
import android.content.Context
import android.location.LocationManager
import android.os.Build

// 集中管理 App 需要的權限
object PermissionUtils {

    // 回傳目前裝置進行 Classic 掃描需要的權限清單
    fun classicBluetoothPermissions(): Array<String> {

        // Android 12 以上需要新的藍牙 Runtime 權限
        // 仍包含 ACCESS_FINE_LOCATION，因部分 OEM（如 MIUI）的 startDiscovery() 需要定位權限或定位服務開啟
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } else {
            // Android 11 以下 Classic / BLE 掃描常需要定位權限
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }
    }

    // 檢查系統定位服務（GPS）是否已開啟
    fun isLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        val locationEnabled = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            locationManager?.isLocationEnabled == true
        } else {
            locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true ||
                    locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true
        }
        return locationEnabled
    }

    // 回傳目前裝置進行 BLE 掃描需要的權限清單
    fun bleBluetoothPermissions(): Array<String> {

        // Android 12 以上需要新的藍牙 Runtime 權限
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT
            )
        } else {
            // Android 11 以下 BLE 掃描通常需要定位權限
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }
    }
}