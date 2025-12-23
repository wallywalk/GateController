package com.cm.gatecontroller.model

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.cm.gatecontroller.ui.theme.Blue600
import com.cm.gatecontroller.ui.theme.Green500
import com.cm.gatecontroller.ui.theme.Red500
import com.cm.gatecontroller.ui.theme.White100

enum class LedStatus {
    RED,
    GREEN,
    BLUE,
    WHITE,
    OFF,
}

val LedStatus.color: Color
    @Composable
    get() = when (this) {
        LedStatus.RED -> Red500
        LedStatus.GREEN -> Green500
        LedStatus.BLUE -> Blue600
        LedStatus.WHITE -> White100
        LedStatus.OFF -> MaterialTheme.colorScheme.primary
    }