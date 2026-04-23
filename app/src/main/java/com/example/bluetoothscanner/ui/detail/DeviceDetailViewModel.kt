package com.example.bluetoothscanner.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.example.bluetoothscanner.data.model.ScanDevice

// 標記類別是由 Hilt 管理的 ViewModel
@HiltViewModel
/**
 * 宣告 DeviceDetailViewModel，並繼承 ViewModel
 * @Inject constructor：告訴 Hilt「這個類別的建構子需要依賴注入」
 *   【不需要自己手動 new SavedStateHandle()，Hilt 自動幫你建立並傳進來】
 * SavedStateHandle：以存取導航參數的容器
 *   【在系統殺掉 App 後也能恢復資料】
 * MutableStateFlow：可以修改的資料流，可被觀察的資料盒子
 *   【裡面的資料一旦改變，所有觀察它的人都會自動收到通知】
 * StateFlow + asStateFlow()：對外唯讀的資料流，UI 只能觀察不能修改
 *   【asStateFlow() 把 MutableStateFlow 轉成 StateFlow】
 * init：ViewModel 建立時自動執行的區塊
 *   【對應 java 建構子裡直接呼叫】
 * viewModelScope.launch：在 ViewModel 的生命週期內啟動一個 Coroutine
 *   【非同步等待，不會卡住 UI，對應 java 開一個 線程】
 */
class DeviceDetailViewModel @Inject constructor(

    // 注入 SavedStateHandle，讓 ViewModel 能取得導航參數
    private val savedStateHandle: SavedStateHandle

) : ViewModel() {

    // 建立可變的內部 UiState，初始值為 Loading
    private val _uiState = MutableStateFlow<DeviceDetailUiState>(DeviceDetailUiState.Loading)

    // 對外提供唯讀的 UiState，讓畫面只能觀察不能修改
    val uiState: StateFlow<DeviceDetailUiState> = _uiState.asStateFlow()

    // 從 SavedStateHandle 讀取 address，如果沒有值就回傳空字串
    private val address: String = savedStateHandle["address"] ?: ""

    // 在 ViewModel 建立時立即執行初始化流程
    init {
        // 載入裝置詳細資料
        loadDeviceDetail()
    }

    // 宣告載入裝置詳細資料的方法
    private fun loadDeviceDetail() {

        // 啟動 coroutine 處理模擬載入流程
        viewModelScope.launch {

            // 先把畫面狀態設為 Loading
            _uiState.value = DeviceDetailUiState.Loading

            // 模擬資料載入延遲，之後接 Repository 時可移除
            delay(500)

            // 建立假資料清單，模擬資料來源
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

            // 根據 address 從假資料中查找對應裝置
            val matchedDevice = fakeDevices.find { it.address == address }

            // 如果有找到裝置資料
            if (matchedDevice != null) {
                // 將畫面狀態更新為 Success，並帶入裝置資料
                _uiState.value = DeviceDetailUiState.Success(matchedDevice)
            } else {
                // 如果找不到資料，則更新為 Error 狀態
                _uiState.value = DeviceDetailUiState.Error("找不到對應的裝置資料。")
            }
        }
    }
}