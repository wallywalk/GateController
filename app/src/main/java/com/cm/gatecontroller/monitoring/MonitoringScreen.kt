package com.cm.gatecontroller.monitoring

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ButtonDefaults
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.cm.gatecontroller.MainTab
import com.cm.gatecontroller.model.color
import com.cm.gatecontroller.monitoring.model.color
import com.cm.gatecontroller.ui.theme.Blue600
import com.cm.gatecontroller.ui.theme.Red500
import com.cm.gatecontroller.ui.theme.component.ControlButton
import com.cm.gatecontroller.ui.theme.component.LabelAndValue
import com.cm.gatecontroller.ui.theme.component.StatusBadge
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
                title = { Text("MONITORING") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("GATE", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                    TwoStatusBadgesRow(
                        label1 = "CLOSE",
                        backgroundColor1 = uiState.channelMode.color,
                        label2 = "OPEN",
                        backgroundColor2 = uiState.channelMode.color,
                        modifier = Modifier.weight(2f)
                    )
                }
            }
            item {
                TwoStatusBadgesRow(
                    label1 = "LAMP",
                    backgroundColor1 = uiState.lamp.color,
                    label2 = "LED",
                    backgroundColor2 = uiState.led.color
                )
            }
            item {
                TwoStatusBadgesRow(
                    label1 = "RELAY1",
                    backgroundColor1 = uiState.relay1.color,
                    label2 = "RELAY2",
                    backgroundColor2 = uiState.relay2.color
                )
            }
            item {
                TwoStatusBadgesRow(
                    label1 = "PHOTO1",
                    backgroundColor1 = uiState.photo1.color,
                    label2 = "PHOTO2",
                    backgroundColor2 = uiState.photo2.color
                )
            }
            item {
                TwoStatusBadgesRow(
                    label1 = "OPEN1",
                    backgroundColor1 = uiState.open1.color,
                    label2 = "CLOSE1",
                    backgroundColor2 = uiState.close1.color
                )
            }
            item {
                TwoStatusBadgesRow(
                    label1 = "OPEN2",
                    backgroundColor1 = uiState.open2.color,
                    label2 = "CLOSE2",
                    backgroundColor2 = uiState.close2.color
                )
            }
            item {
                TwoStatusBadgesRow(
                    label1 = "OPEN3",
                    backgroundColor1 = uiState.open3.color,
                    label2 = "CLOSE3",
                    backgroundColor2 = uiState.close3.color
                )
            }
            item {
                TwoStatusBadgesRow(
                    label1 = "LOOP A",
                    backgroundColor1 = uiState.loopA.color,
                    label2 = "LOOP B",
                    backgroundColor2 = uiState.loopB.color
                )
            }
            item {
                TwoStatusBadgesRow(
                    label1 = uiState.mainPower,
                    backgroundColor1 = MaterialTheme.colorScheme.surfaceVariant,
                    label2 = uiState.testCount.toString(),
                    backgroundColor2 = MaterialTheme.colorScheme.surfaceVariant
                )
            }
            item {
                LabelAndValue(
                    label = "DELAY TIME",
                    value = "${uiState.delayTime}sec"
                )
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ControlButton(
                        text = if (uiState.isTestRunning) "TEST STOP" else "TEST START",
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (uiState.isTestRunning) Red500 else Blue600,
                            contentColor = Color.White
                        ),
                        onClick = { viewModel.handleIntent(MonitoringIntent.ToggleTest) }
                    )
                    ControlButton(
                        modifier = Modifier.weight(1f),
                        text = "CONFIG",
                        onClick = { navController.navigate(MainTab.Configuration.route) }
                    )
                    ControlButton(
                        modifier = Modifier.weight(1f),
                        text = "BOARD TEST",
                        onClick = { navController.navigate(MainTab.BoardTest.route) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TwoStatusBadgesRow(
    label1: String,
    backgroundColor1: Color,
    label2: String,
    backgroundColor2: Color,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        StatusBadge(
            text = label1,
            backgroundColor = backgroundColor1,
            modifier = Modifier.weight(1f)
        )
        StatusBadge(
            text = label2,
            backgroundColor = backgroundColor2,
            modifier = Modifier.weight(1f)
        )
    }
}