package com.cm.gatecontroller.connection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cm.gatecontroller.core.serial.RealSerialClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConnectionViewModel @Inject constructor(
    private val serialClient: RealSerialClient
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConnectionUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            connectToFirstDevice()
        }
    }

    fun connectToFirstDevice() {
        if (_uiState.value.status == ConnectionStatus.CONNECTING || _uiState.value.status == ConnectionStatus.CONNECTED) {
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(status = ConnectionStatus.CONNECTING) }

            val devices = serialClient.getAvailableDevices()
            if (devices.isEmpty()) {
                _uiState.update { it.copy(status = ConnectionStatus.ERROR) }
                return@launch
            }

            val firstDevice = devices.first()
            val result = serialClient.connect(firstDevice)

            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        status = ConnectionStatus.CONNECTED,
                        connectedDeviceName = firstDevice.toString()
                    )
                }
            } else {
                _uiState.update { it.copy(status = ConnectionStatus.ERROR) }
            }
        }
    }

    fun disconnect() {
        viewModelScope.launch {
            serialClient.disconnect()
            _uiState.update {
                it.copy(
                    status = ConnectionStatus.DISCONNECTED,
                    connectedDeviceName = null
                )
            }
        }
    }
}