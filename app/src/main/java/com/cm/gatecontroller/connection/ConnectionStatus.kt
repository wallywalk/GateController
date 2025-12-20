package com.cm.gatecontroller.connection

import androidx.compose.ui.graphics.Color
import com.cm.gatecontroller.ui.theme.Gray400
import com.cm.gatecontroller.ui.theme.Green500
import com.cm.gatecontroller.ui.theme.Red500
import com.cm.gatecontroller.ui.theme.Yellow300

enum class ConnectionStatus(
    val label: String,
    val color: Color
) {
    DISCONNECTED(
        label = "Disconnected",
        color = Gray400
    ),
    CONNECTING(
        label = "Connecting...",
        color = Yellow300
    ),
    CONNECTED(
        label = "Connected",
        color = Green500
    ),
    ERROR(
        label = "Error",
        color = Red500
    )
}
