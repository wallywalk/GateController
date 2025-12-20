package com.cm.gatecontroller.model

import androidx.compose.ui.graphics.Color
import com.cm.gatecontroller.ui.theme.Blue600
import com.cm.gatecontroller.ui.theme.Gray400
import com.cm.gatecontroller.ui.theme.Green500
import com.cm.gatecontroller.ui.theme.Red500
import com.cm.gatecontroller.ui.theme.White100
import com.cm.gatecontroller.ui.theme.Yellow300

enum class LedStatus(val color: Color) {
    RED(Red500),
    GREEN(Green500),
    BLUE(Blue600),
    YELLOW(Yellow300),
    WHITE(White100),
    OFF(Gray400),
}