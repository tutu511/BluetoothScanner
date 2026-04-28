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
 * SavedStateHandle：以存取導航參數的容器(如果只傳 address 可用)
 *   【在系統殺掉 App 後也能恢復資料】
 *   【一个可以在 ViewModel 里存取，且能在 App 被系统杀掉后还能恢复的键值储存空间】
 * MutableStateFlow：可以修改的資料流，可被觀察的資料盒子
 *   【裡面的資料一旦改變，所有觀察它的人都會自動收到通知】
 * StateFlow + asStateFlow()：對外唯讀的資料流，UI 只能觀察不能修改
 *   【asStateFlow() 把 MutableStateFlow 轉成 StateFlow】
 * init：ViewModel 建立時自動執行的區塊
 *   【對應 java 建構子裡直接呼叫】
 * viewModelScope.launch：在 ViewModel 的生命週期內啟動一個 Coroutine
 *   【非同步等待，不會卡住 UI，對應 java 開一個 線程】
 */
class DeviceDetailViewModel @Inject constructor() : ViewModel() {

    // 建立可變的內部 UiState，初始值為 Loading
    private val _uiState = MutableStateFlow<DeviceDetailUiState>(DeviceDetailUiState.Loading)

    // 對外提供唯讀的 UiState，讓畫面只能觀察不能修改
    val uiState: StateFlow<DeviceDetailUiState> = _uiState.asStateFlow()

    fun setDevice(device: ScanDevice) {
        _uiState.value = DeviceDetailUiState.Success(device)
    }

    fun setError(message: String) {
        _uiState.value = DeviceDetailUiState.Error(message)
    }

    // 在 ViewModel 建立時立即執行初始化流程
    init {

    }
}