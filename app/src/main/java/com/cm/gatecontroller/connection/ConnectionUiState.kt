package com.cm.gatecontroller.connection

data class ConnectionUiState(
    val status: ConnectionStatus = ConnectionStatus.DISCONNECTED,
    val connectedDeviceName: String? = null
)
