package com.example.bluetoothscanner.ui.ble

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bluetoothscanner.data.model.ScanDevice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class BleScanViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(BleScanUiState())

    // 只能觀察不能修改
    val uiState: StateFlow<BleScanUiState> = _uiState.asStateFlow()

    // 開始 BLE 掃描的方法。
    fun startScan() {

        // 模擬 BLE 掃描流程
        viewModelScope.launch {

            // 狀態更新為掃描中
            _uiState.value = _uiState.value.copy(
                isScanning = true,
                isLoading = true,
                statusMessage = "目前狀態：BLE 掃描中...",
                errorMessage = null
            )

            // 模擬 BLE 掃描延遲
            delay(800)

            // 建立假 BLE 掃描結果資料
            val fakeDevices = listOf(
                ScanDevice(
                    name = "Mi Band 8",
                    address = "102030405060",
                    rssi = -38,
                    type = "BLE",
                    bonded = true
                ),
                ScanDevice(
                    name = "Temp Sensor",
                    address = "AA10BB20CC30",
                    rssi = -67,
                    type = "BLE",
                    bonded = false
                ),
                ScanDevice(
                    name = null,
                    address = "DEADBEEF1122",
                    rssi = -80,
                    type = "BLE",
                    bonded = false
                )
            )

            // 將 BLE 掃描結果更新到畫面狀態
            _uiState.value = _uiState.value.copy(
                isScanning = true,
                isLoading = false,
                statusMessage = "目前狀態：BLE 掃描完成，共找到 ${fakeDevices.size} 筆裝置",
                deviceList = fakeDevices,
                errorMessage = null
            )
        }
    }

    // 停止 BLE 掃描的方法
    fun stopScan() {

        // 狀態：停止 BLE 掃描
        _uiState.value = _uiState.value.copy(
            isScanning = false,
            isLoading = false,
            statusMessage = "目前狀態：已停止 BLE 掃描"
        )
    }

    // 清除錯誤訊息
    fun clearError() {

        _uiState.value = _uiState.value.copy(
            errorMessage = null
        )
    }
}