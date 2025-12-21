package com.cm.gatecontroller.monitoring.model

import androidx.compose.ui.graphics.Color
import com.cm.gatecontroller.ui.theme.Gray400
import com.cm.gatecontroller.ui.theme.Yellow300

enum class ChannelMode {
    OPEN,
    CLOSE
}

val ChannelMode.color: Color
    get() = when (this) {
        ChannelMode.OPEN -> Yellow300
        ChannelMode.CLOSE -> Gray400
    }