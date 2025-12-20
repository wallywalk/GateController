package com.cm.gatecontroller.configuration.model

import androidx.compose.ui.graphics.Color
import com.cm.gatecontroller.ui.theme.Gray400
import com.cm.gatecontroller.ui.theme.Yellow300

enum class UsageStatus(val color: Color) {
    USE(Yellow300),
    UNUSE(Gray400)
}
