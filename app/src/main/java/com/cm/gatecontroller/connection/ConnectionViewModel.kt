package com.cm.gatecontroller.connection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cm.gatecontroller.core.logger.Logger
import com.cm.gatecontroller.core.serial.SerialClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConnectionViewModel @Inject constructor(
    private val serialClient: SerialClient,
    private val debugLogger: Logger,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConnectionUiState())
    val uiState = _uiState.asStateFlow()

    init {
        debugLogger.d(TAG, "init - observing permission events.")
        viewModelScope.launch {
            serialClient.permissionEvents.collect { granted ->
                debugLogger.d(TAG, "Permission event received. Granted: $granted")
                if (granted) {
                    connectToFirstDevice()
                } else {
                    _uiState.update { it.copy(status = ConnectionStatus.ERROR) }
                    debugLogger.d(TAG, "Permission denied by user.")
                }
            }
        }

        viewModelScope.launch {
            connectToFirstDevice()
        }
    }

    override fun onCleared() {
        debugLogger.d(TAG, "onCleared")
        super.onCleared()
    }

    fun connectToFirstDevice() {
        debugLogger.d(TAG, "connectToFirstDevice() called.")
        if (_uiState.value.status == ConnectionStatus.CONNECTING || _uiState.value.status == ConnectionStatus.CONNECTED) {
            debugLogger.d(TAG, "Aborting connection attempt: Already connecting or connected.")
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(status = ConnectionStatus.CONNECTING) }
            debugLogger.d(TAG, "Status set to CONNECTING.")

            val devices = serialClient.getAvailableDevices()
            if (devices.isEmpty()) {
                _uiState.update { it.copy(status = ConnectionStatus.ERROR) }
                debugLogger.d(TAG, "Connection failed: No available devices found.")
                return@launch
            }
            debugLogger.d(
                TAG,
                "Found ${devices.size} devices. Attempting to connect to the first one."
            )

            val firstDevice = devices.first()
            val result = serialClient.connect(firstDevice)

            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        status = ConnectionStatus.CONNECTED,
                        connectedDeviceName = firstDevice.toString()
                    )
                }
                debugLogger.d(TAG, "Connection successful to ${firstDevice.device.deviceName}.")
            } else {
                if (result.exceptionOrNull() is SecurityException) {
                    _uiState.update { it.copy(status = ConnectionStatus.DISCONNECTED) }
                    debugLogger.d(
                        TAG,
                        "Connection failed: SecurityException. Requesting permission..."
                    )
                    serialClient.requestPermission(firstDevice.device)
                } else {
                    _uiState.update { it.copy(status = ConnectionStatus.ERROR) }
                    debugLogger.d(TAG, "Connection failed: ${result.exceptionOrNull()?.message}")
                }
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

    companion object {
        private const val TAG = "ConnectionViewModel"
    }
}