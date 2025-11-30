package com.cm.gatecontroller.monitoring

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.cm.gatecontroller.core.serial.model.GateControllerState
import com.cm.gatecontroller.core.serial.model.GateState
import com.cm.gatecontroller.core.serial.model.LedColor
import com.cm.gatecontroller.core.serial.model.OnOff
import com.cm.gatecontroller.ui.theme.GateControllerTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun MonitoringScreen(
    navController: NavController,
    viewModel: MonitoringViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val status = uiState.deviceStatus

    val orangeColor = Color(0xFFFFA500) // Status ON/USE
    val grayColor = Color.Gray // Status OFF/UNUSE
    val redColor = Color.Red // TEST STOP
    val blueColor = Color(0xFF0070C0) // LED BLUE, TEST START

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("MONITORING", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))

        // Gate
        MonitoringRow(
            label = "GATE",
            content = {
                MonitoringButton(
                    text = "CLOSE",
                    isActive = status.gateState == GateState.CLOSE,
                    activeColor = orangeColor,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                MonitoringButton(
                    text = "OPEN",
                    isActive = status.gateState == GateState.OPEN,
                    activeColor = orangeColor,
                    modifier = Modifier.weight(1f)
                )
            }
        )

        // LAMP, LED
        MonitoringRow(
            content = {
                MonitoringButton(
                    text = "LAMP",
                    value = status.lampState?.name,
                    isActive = status.lampState == OnOff.ON,
                    activeColor = orangeColor,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                MonitoringButton(
                    text = "LED",
                    value = status.ledColor?.name,
                    isActive = status.ledColor != LedColor.OFF,
                    activeColor = when (status.ledColor) {
                        LedColor.BLUE -> Color(0xFF0070C0)
                        LedColor.GREEN -> Color(0xFF00B050)
                        LedColor.RED -> Color(0xFFFF0000)
                        LedColor.WHITE -> Color(0xFFF2F2F2)
                        else -> grayColor
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        )

        // RELAY1, RELAY2
        MonitoringRow(
            content = {
                MonitoringButton(
                    text = "RELAY1",
                    isActive = status.relay1 == OnOff.ON,
                    activeColor = orangeColor,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                MonitoringButton(
                    text = "RELAY2",
                    isActive = status.relay2 == OnOff.ON,
                    activeColor = orangeColor,
                    modifier = Modifier.weight(1f)
                )
            }
        )

        // PHOTO1, PHOTO2
        MonitoringRow(
            content = {
                MonitoringButton(
                    text = "PHOTO1",
                    isActive = status.photo1 == OnOff.ON,
                    activeColor = orangeColor,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                MonitoringButton(
                    text = "PHOTO2",
                    isActive = status.photo2 == OnOff.ON,
                    activeColor = orangeColor,
                    modifier = Modifier.weight(1f)
                )
            }
        )

        // OPEN1, CLOSE1
        MonitoringRow(
            content = {
                MonitoringButton(
                    text = "OPEN1",
                    isActive = status.open1 == OnOff.ON,
                    activeColor = orangeColor,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                MonitoringButton(
                    text = "CLOSE1",
                    isActive = status.close1 == OnOff.ON,
                    activeColor = orangeColor,
                    modifier = Modifier.weight(1f)
                )
            }
        )

        // OPEN2, CLOSE2
        MonitoringRow(
            content = {
                MonitoringButton(
                    text = "OPEN2",
                    isActive = status.open2 == OnOff.ON,
                    activeColor = orangeColor,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                MonitoringButton(
                    text = "CLOSE2",
                    isActive = status.close2 == OnOff.ON,
                    activeColor = orangeColor,
                    modifier = Modifier.weight(1f)
                )
            }
        )

        // OPEN3, CLOSE3
        MonitoringRow(
            content = {
                MonitoringButton(
                    text = "OPEN3",
                    isActive = status.open3 == OnOff.ON,
                    activeColor = orangeColor,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                MonitoringButton(
                    text = "CLOSE3",
                    isActive = status.close3 == OnOff.ON,
                    activeColor = orangeColor,
                    modifier = Modifier.weight(1f)
                )
            }
        )

        // LOOP A, LOOP B
        MonitoringRow(
            content = {
                MonitoringButton(
                    text = "LOOP A",
                    isActive = status.loopA_mon == OnOff.ON,
                    activeColor = orangeColor,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                MonitoringButton(
                    text = "LOOP B",
                    isActive = status.loopB_mon == OnOff.ON,
                    activeColor = orangeColor,
                    modifier = Modifier.weight(1f)
                )
            }
        )

        // 23.68V, 1234
        MonitoringRow(
            content = {
                MonitoringButton(
                    text = status.mainPower,
                    isActive = true, // Always active for display
                    activeColor = MaterialTheme.colorScheme.surfaceVariant, // Neutral color
                    modifier = Modifier.weight(1f),
                    textColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                MonitoringButton(
                    text = status.testCount,
                    isActive = true, // Always active for display
                    activeColor = MaterialTheme.colorScheme.surfaceVariant, // Neutral color
                    modifier = Modifier.weight(1f),
                    textColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        )

        // DELAY 30sec
        MonitoringRow(
            label = "DELAY",
            content = {
                MonitoringButton(
                    text = status.delayTime_mon,
                    isActive = true, // Always active for display
                    activeColor = MaterialTheme.colorScheme.surfaceVariant, // Neutral color
                    modifier = Modifier.fillMaxWidth(),
                    textColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Bottom Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MonitoringButton(
                text = if (status.isTestRunning) "TEST STOP" else "TEST START",
                isActive = status.isTestRunning,
                activeColor = if (status.isTestRunning) redColor else blueColor,
                onClick = { viewModel.handleIntent(MonitoringIntent.ToggleTest) },
                modifier = Modifier
                    .weight(1f)
                    .height(60.dp) // Taller button
            )
            Spacer(modifier = Modifier.width(8.dp))
            MonitoringButton(
                text = "CONFIGURATION",
                onClick = { navController.navigate("configuration") },
                isActive = true,
                activeColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .weight(1f)
                    .height(60.dp) // Taller button
            )
            Spacer(modifier = Modifier.width(8.dp))
            MonitoringButton(
                text = "BOARD TEST",
                onClick = { navController.navigate("boardtest") },
                isActive = true,
                activeColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .weight(1f)
                    .height(60.dp) // Taller button
            )
        }
    }
}

@Composable
fun MonitoringRow(
    label: String? = null,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(80.dp) // Fixed width for label
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}

@Composable
fun MonitoringButton(
    text: String,
    value: String? = null,
    isActive: Boolean,
    activeColor: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    textColor: Color = Color.White
) {
    val buttonColors = ButtonDefaults.buttonColors(
        containerColor = if (isActive) activeColor else Color.Gray,
        contentColor = textColor
    )
    Button(
        onClick = onClick ?: {},
        enabled = onClick != null, // Enable button only if onClick is provided
        shape = RoundedCornerShape(8.dp), // Radius
        colors = buttonColors,
        modifier = modifier.height(50.dp) // Default height
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text, fontSize = 14.sp, textAlign = TextAlign.Center)
            if (value != null) {
                Text(value, fontSize = 12.sp, textAlign = TextAlign.Center)
            }
        }
    }
}

// Mock ViewModel for Preview
class MockMonitoringViewModel : MonitoringViewModel(
    object : com.cm.gatecontroller.core.serial.SerialRepository {
        override val deviceStatus: StateFlow<GateControllerState> = MutableStateFlow(
            GateControllerState(
                version = "1.00JAN24",
                gateState = GateState.CLOSE,
                lampState = OnOff.ON,
                ledColor = LedColor.BLUE,
                relay1 = OnOff.ON,
                relay2 = OnOff.OFF,
                photo1 = OnOff.ON,
                photo2 = OnOff.OFF,
                open1 = OnOff.ON,
                close1 = OnOff.OFF,
                open2 = OnOff.ON,
                close2 = OnOff.OFF,
                open3 = OnOff.ON,
                close3 = OnOff.OFF,
                loopA_mon = OnOff.ON,
                loopB_mon = OnOff.OFF,
                mainPower = "23.68V",
                testCount = "1234",
                delayTime_mon = "30sec",
                isTestRunning = false
            )
        )

        override suspend fun refreshStatus() {}
        override suspend fun openGate() {}
        override suspend fun closeGate() {}
        override suspend fun stopGate() {}
        override suspend fun toggleLamp() {}
        override suspend fun setLedColor(color: String) {}
        override suspend fun startTest() {}
        override suspend fun stopTest() {}
        override suspend fun refreshConfiguration() {}
        override suspend fun setOpenLevel(level: Int) {}
        override suspend fun setCloseLevel(level: Int) {}
        override suspend fun setLampUsage(use: Boolean) {}
        override suspend fun setBuzzerUsage(use: Boolean) {}
        override suspend fun setLampOnPosition(position: String) {}
        override suspend fun setLampOffPosition(position: String) {}
        override suspend fun setLedOpenColor(color: String) {}
        override suspend fun setLedOpenPosition(position: String) {}
        override suspend fun setLedCloseColor(color: String) {}
        override suspend fun setLedClosePosition(position: String) {}
        override suspend fun setLoopAUsage(use: Boolean) {}
        override suspend fun setLoopBUsage(use: Boolean) {}
        override suspend fun setDelayTime(time: Int) {}
        override suspend fun setRelay1Mode(mode: Int) {}
        override suspend fun setRelay2Mode(mode: Int) {}
        override suspend fun factoryReset(): Result<Unit> { return Result.success(Unit) }
        override suspend fun setControlLamp(on: Boolean) {}
        override suspend fun setControlRelay1(on: Boolean) {}
        override suspend fun setControlRelay2(on: Boolean) {}
        override suspend fun setControlLed(color: String) {}
        override suspend fun setControlPosition(position: String) {}
        override suspend fun openGateTest() {}
        override suspend fun closeGateTest() {}
        override suspend fun stopGateTest() {}
    }
)

@Preview(showBackground = true)
@Composable
fun MonitoringScreenPreview() {
    GateControllerTheme {
        MonitoringScreen(navController = rememberNavController(), viewModel = MockMonitoringViewModel())
    }
}