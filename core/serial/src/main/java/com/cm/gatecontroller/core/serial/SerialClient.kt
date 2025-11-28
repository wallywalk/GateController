package com.cm.gatecontroller.core.serial

import kotlinx.coroutines.flow.Flow

interface SerialClient {
    val responses: Flow<String>
    suspend fun sendCommand(command: String)
    fun connect()
    fun disconnect()
}
