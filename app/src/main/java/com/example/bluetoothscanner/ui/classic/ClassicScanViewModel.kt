package com.example.bluetoothscanner.ui.classic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bluetoothscanner.data.model.ScanDevice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.delay

// 標記類別是由 Hilt 管理的 ViewModel
@HiltViewModel
/**
 * StateFlow 的通知条件是：value 指向的地址有沒有換
 *
 * .copy() 方法：
 *   ClassicScanUiState 裡面的變量都是唯讀狀態（val），所以不能直接修改（除非用 var）
 *   但是如果用 var 的話，StateFlow 不會知道被修改了，只有 .value 被替換成新物件時，才會通知觀察者
 *   copy() 是 data class 自動產生的方法：
 *      指定的欄位 → 用新值
 *      沒有指定的欄位 → 保留原本的值
 *      回傳一個全新的物件
 */
class ClassicScanViewModel @Inject constructor(): ViewModel() {

    // 建立內部可變的 uiState，初始值使用 ClassicScanUiState 預設值
    private val _uiState = MutableStateFlow(ClassicScanUiState())

    // 對外提供 uiState 的唯讀版本
    val uiState: StateFlow<ClassicScanUiState> = _uiState.asStateFlow()

    // 開始掃描
    fun startScan() {
        viewModelScope.launch {
            // 掃描狀態更新為：正在掃描
            _uiState.value = _uiState.value.copy(
                isScanning = true,
                isLoading = true,
                statusMessage = "目前狀態：掃描中...",
                errorMessage = null
            )

            // 模擬掃描延遲。
            delay(800)

            // 建立假掃描結果資料。
            val fakeDevices = listOf(
                ScanDevice(
                    name = "Sony WH-1000XM5",
                    address = "001122334455",
                    rssi = -45,
                    type = "Classic",
                    bonded = true
                ),
                ScanDevice(
                    name = "JBL Speaker",
                    address = "AABBCCDDEEFF",
                    rssi = -60,
                    type = "Classic",
                    bonded = false
                ),
                ScanDevice(
                    name = null,
                    address = "1234567890AB",
                    rssi = -72,
                    type = "Classic",
                    bonded = false
                )
            )

            // 將掃描結果更新到畫面狀態
            _uiState.value = _uiState.value.copy(
                isScanning = true,
                isLoading = false,
                statusMessage = "目前狀態：掃描完成，共找到 ${fakeDevices.size} 筆裝置",
                deviceList = fakeDevices,
                errorMessage = null
            )
        }
    }

    // 停止掃描
    fun stopScan() {

        // 掃描狀態：停止掃描
        _uiState.value = _uiState.value.copy(
            isScanning = false,
            isLoading = false,
            statusMessage = "目前狀態：已停止掃描"
        )
    }

    // 清除錯誤訊息
    fun clearError() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null
        )
    }

}