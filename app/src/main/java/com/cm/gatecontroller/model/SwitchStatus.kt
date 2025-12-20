package com.cm.gatecontroller.model

import androidx.compose.ui.graphics.Color
import com.cm.gatecontroller.ui.theme.Gray400
import com.cm.gatecontroller.ui.theme.Yellow300

enum class SwitchStatus(val color: Color) {
    ON(Yellow300),
    OFF(Gray400)
}