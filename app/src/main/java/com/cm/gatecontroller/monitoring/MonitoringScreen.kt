package com.cm.gatecontroller.monitoring

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.cm.gatecontroller.core.serial.model.GateState
import com.cm.gatecontroller.core.serial.model.LedColor
import com.cm.gatecontroller.core.serial.model.OnOff

@Composable
fun MonitoringScreen(
    navController: NavController,
    viewModel: MonitoringViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val status = uiState.deviceStatus

    val orangeColor = Color(0xFFFFA500)
    val redColor = Color.Red
    val blueColor = MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("MONITORING", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            item { StatusButton("GATE", "CLOSE", status.gateState == GateState.CLOSE, orangeColor) }
            item { StatusButton("GATE", "OPEN", status.gateState == GateState.OPEN, orangeColor) }

            item {
                StatusButton(
                    "LAMP",
                    status.lampState?.name,
                    status.lampState == OnOff.ON,
                    orangeColor
                )
            }
            item {
                StatusButton(
                    "LED",
                    status.ledColor?.name,
                    status.ledColor != LedColor.OFF,
                    orangeColor
                )
            }

            item { StatusDisplay("RELAY1", status.relay1) }
            item { StatusDisplay("RELAY2", status.relay2) }

            item { StatusDisplay("PHOTO1", status.photo1) }
            item { StatusDisplay("PHOTO2", status.photo2) }

            item { StatusDisplay("OPEN1", status.open1) }
            item { StatusDisplay("CLOSE1", status.close1) }

            item { StatusDisplay("OPEN2", status.open2) }
            item { StatusDisplay("CLOSE2", status.close2) }

            item { StatusDisplay("OPEN3", status.open3) }
            item { StatusDisplay("CLOSE3", status.close3) }

            item { StatusDisplay("LOOP A", status.loopA_mon) }
            item { StatusDisplay("LOOP B", status.loopB_mon) }

            item { StatusText(status.mainPower) }
            item { StatusText(status.testCount) }

            item { StatusButton("DELAY", status.delayTime_mon, true, orangeColor, fullSpan = true) }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { viewModel.handleIntent(MonitoringIntent.ToggleTest) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (status.isTestRunning) redColor else blueColor
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text(if (status.isTestRunning) "TEST STOP" else "TEST START")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { navController.navigate("configuration") },
                modifier = Modifier.weight(1f)
            ) {
                Text("CONFIGURATION")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { navController.navigate("boardtest") },
                modifier = Modifier.weight(1f)
            ) {
                Text("BOARD TEST")
            }
        }
    }
}

@Composable
fun StatusButton(
    label: String,
    value: String?,
    isActive: Boolean,
    activeColor: Color,
    fullSpan: Boolean = false
) {
    Button(
        onClick = { /* No-op for display only */ },
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isActive) activeColor else Color.Gray
        ),
        modifier = if (fullSpan) Modifier.fillMaxWidth() else Modifier
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label)
            if (value != null) {
                Text(value)
            }
        }
    }
}

@Composable
fun StatusDisplay(label: String, value: OnOff?) {
    val isActive = value == OnOff.ON
    Card(
        colors = CardDefaults.cardColors(containerColor = if (isActive) Color(0xFFFFA500) else Color.Gray),
        modifier = Modifier.height(50.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(label, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun StatusText(value: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.DarkGray),
        modifier = Modifier.height(50.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(value, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}