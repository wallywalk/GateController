package com.cm.gatecontroller.monitoring

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.cm.gatecontroller.ui.theme.Blue600
import com.cm.gatecontroller.ui.theme.Gray400
import com.cm.gatecontroller.ui.theme.Green500
import com.cm.gatecontroller.ui.theme.Red500
import com.cm.gatecontroller.ui.theme.White100
import com.cm.gatecontroller.ui.theme.Yellow300
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonitoringScreen(
    navController: NavHostController,
    viewModel: MonitoringViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collectLatest { effect ->
            when (effect) {
                is MonitoringSideEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gate Controller Monitoring") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val statusItems = createStatusItems(uiState)
                items(statusItems) { item ->
                    StatusCard(title = item.title, value = item.value, color = item.color)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Version: ${uiState.version}", fontWeight = FontWeight.Bold)
                Button(
                    onClick = { viewModel.handleIntent(MonitoringIntent.ToggleTest) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (uiState.isTestRunning) Red500 else Blue600
                    )
                ) {
                    Text(if (uiState.isTestRunning) "TEST STOP" else "TEST START")
                }
            }
        }
    }
}

@Composable
fun StatusCard(title: String, value: String, color: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black
            )
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
        }
    }
}

private data class StatusItem(val title: String, val value: String, val color: Color)

private fun onOffColor(status: OnOffStatus, active: Color, inactive: Color) =
    if (status == OnOffStatus.ON) active else inactive

private fun gateColor(status: GateStatus, active: Color, inactive: Color) =
    if (status == GateStatus.OPEN) active else inactive

private fun ledColor(status: LedStatus) = when (status) {
    LedStatus.OFF -> Gray400
    LedStatus.BLUE -> Blue600
    LedStatus.GREEN -> Green500
    LedStatus.RED -> Red500
    LedStatus.WHITE -> White100
}

private fun createStatusItems(uiState: MonitoringUiState): List<StatusItem> { // fixme: 하드코딩
    val activeColor = Yellow300
    val inactiveColor = Gray400

    return listOf(
        StatusItem(
            "GATE",
            uiState.gateState.name,
            gateColor(uiState.gateState, activeColor, inactiveColor)
        ),
        StatusItem(
            "LAMP",
            uiState.lampState.name,
            onOffColor(uiState.lampState, activeColor, inactiveColor)
        ),
        StatusItem("LED", uiState.ledState.name, ledColor(uiState.ledState)),
        StatusItem(
            "RELAY 1",
            uiState.relay1State.name,
            onOffColor(uiState.relay1State, activeColor, inactiveColor)
        ),
        StatusItem(
            "RELAY 2",
            uiState.relay2State.name,
            onOffColor(uiState.relay2State, activeColor, inactiveColor)
        ),
        StatusItem(
            "PHOTO 1",
            uiState.photo1State.name,
            onOffColor(uiState.photo1State, activeColor, inactiveColor)
        ),
        StatusItem(
            "PHOTO 2",
            uiState.photo2State.name,
            onOffColor(uiState.photo2State, activeColor, inactiveColor)
        ),
        StatusItem(
            "OPEN 1",
            uiState.open1State.name,
            onOffColor(uiState.open1State, activeColor, inactiveColor)
        ),
        StatusItem(
            "OPEN 2",
            uiState.open2State.name,
            onOffColor(uiState.open2State, activeColor, inactiveColor)
        ),
        StatusItem(
            "OPEN 3",
            uiState.open3State.name,
            onOffColor(uiState.open3State, activeColor, inactiveColor)
        ),
        StatusItem(
            "CLOSE 1",
            uiState.close1State.name,
            onOffColor(uiState.close1State, activeColor, inactiveColor)
        ),
        StatusItem(
            "CLOSE 2",
            uiState.close2State.name,
            onOffColor(uiState.close2State, activeColor, inactiveColor)
        ),
        StatusItem(
            "CLOSE 3",
            uiState.close3State.name,
            onOffColor(uiState.close3State, activeColor, inactiveColor)
        ),
        StatusItem(
            "LOOP A",
            uiState.loopAState.name,
            onOffColor(uiState.loopAState, activeColor, inactiveColor)
        ),
        StatusItem(
            "LOOP B",
            uiState.loopBState.name,
            onOffColor(uiState.loopBState, activeColor, inactiveColor)
        ),
        StatusItem("MAIN PWR", uiState.mainPower, activeColor),
        StatusItem("TEST CNT", uiState.testCount.toString(), activeColor),
        StatusItem("DELAY", uiState.delayTime.toString(), activeColor)
    )
}