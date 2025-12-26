package com.cm.gatecontroller.monitoring.model

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.cm.gatecontroller.ui.theme.Purple700

enum class ChannelMode {
    OPEN,
    CLOSE
}

val ChannelMode.color: Color
    @Composable
    get() = when (this) {
        ChannelMode.OPEN -> Purple700
        ChannelMode.CLOSE -> MaterialTheme.colorScheme.inversePrimary
    }