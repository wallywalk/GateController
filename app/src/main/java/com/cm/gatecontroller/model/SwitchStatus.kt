package com.cm.gatecontroller.model

import androidx.compose.ui.graphics.Color
import com.cm.gatecontroller.ui.theme.Gray400
import com.cm.gatecontroller.ui.theme.Yellow300

enum class SwitchStatus {
    ON,
    OFF
}

val SwitchStatus.color: Color
    get() = when (this) {
        SwitchStatus.ON -> Yellow300
        SwitchStatus.OFF -> Gray400
    }