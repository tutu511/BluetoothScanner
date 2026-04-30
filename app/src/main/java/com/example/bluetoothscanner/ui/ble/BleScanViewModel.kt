package com.example.bluetoothscanner.ui.ble

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bluetoothscanner.data.model.ScanDevice
import com.example.bluetoothscanner.data.repository.BluetoothScanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BleScanViewModel @Inject constructor(
    // 注入 BluetoothScanRepository，讓 ViewModel 可以呼叫 BLE 掃描
    private val repository: BluetoothScanRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BleScanUiState())

    // 只能觀察不能修改
    val uiState: StateFlow<BleScanUiState> = _uiState.asStateFlow()

    // 保存目前 BLE 掃描任務，避免重複啟動多個掃描
    private var scanJob: Job? = null

    private var isStoppedByUser = false

    // 開始 BLE 掃描的方法
    fun startScan() {

        // 如果目前已經正在掃描，就不重複啟動
        if (_uiState.value.isScanning) {
            return
        }

        isStoppedByUser = false

        // 啟動 BLE 掃描 Coroutine，並保存 Job
        scanJob = viewModelScope.launch {
            // 掃描開始前，先更新畫面狀態
            _uiState.value = _uiState.value.copy(
                isScanning = true,
                isLoading = true,
                statusMessage = "目前狀態：BLE 掃描中...",
                errorMessage = null
            )

            // 開始正式掃描：呼叫 Repository 開始 BLE 掃描
            repository.startBleScan()
                .onCompletion { cause ->
                    scanJob = null
                    _uiState.value = _uiState.value.copy(
                        isScanning = false,
                        isLoading = false,
                        statusMessage = if (cause != null && !isStoppedByUser) "目前狀態：BLE 掃描失敗"
                        else "目前狀態：BLE 掃描完成，共找到 ${_uiState.value.deviceList.size} 筆裝置",
                        errorMessage = cause?.message
                    )
                }
                .catch {  }
                .collect { device ->
                    val currentList = _uiState.value.deviceList
                    val updatedList = if (currentList.any { it.address == device.address }) {
                        currentList.map {
                            if (it.address == device.address) device else it
                        }
                    } else {
                        currentList + device
                    }

                    // 將掃描結果更新到畫面狀態
                    _uiState.value = _uiState.value.copy(
                        isScanning = true,
                        isLoading = false,
                        deviceList = updatedList,
                        statusMessage = "目前狀態：BLE 掃描中... (${updatedList.size})"
                    )
                }

        }

    }

    // 停止 BLE 掃描的方法
    fun stopScan() {
        // 如果目前沒有在掃描，就不做事。、
        if (!_uiState.value.isScanning) {
            return
        }

        // 通知底層 Scanner 停止 BLE 掃描
        repository.stopBleScan()
        isStoppedByUser = true

        /**
         * 取消目前正在 collect 的 Coroutine
         *
         * scanJob?.cancel()
         *         ↓
         * 協程被取消
         *         ↓
         * collect 中斷
         *         ↓
         * callbackFlow 結束
         *         ↓
         * awaitClose 執行 → bleScanner.stopScan()、清理資源
         *         ↓
         * onCompletion 觸發
         */
        scanJob?.cancel()
        scanJob = null
    }

    // 清除錯誤訊息
    fun clearError() {

        _uiState.value = _uiState.value.copy(
            errorMessage = null
        )
    }
}