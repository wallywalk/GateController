package com.cm.gatecontroller.monitoring

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.cm.gatecontroller.MainTab
import com.cm.gatecontroller.R
import com.cm.gatecontroller.model.color
import com.cm.gatecontroller.monitoring.model.color
import com.cm.gatecontroller.ui.theme.Blue600
import com.cm.gatecontroller.ui.theme.Red500
import com.cm.gatecontroller.ui.component.ControlButton
import com.cm.gatecontroller.ui.component.LabelAndValue
import com.cm.gatecontroller.ui.component.StatusBadge
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonitoringScreen(
    navController: NavHostController,
    viewModel: MonitoringViewModel = hiltViewModel(),
    showSnackbar: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collectLatest { effect ->
            when (effect) {
                is MonitoringSideEffect.ShowSnackbar -> {
                    showSnackbar(effect.message)
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.monitoring_gate),
                        fontWeight = FontWeight.Bold
                    )
                    TwoStatusBadgesRow(
                        modifier = Modifier.weight(2f),
                        label1 = stringResource(R.string.common_close),
                        backgroundColor1 = uiState.channelMode.color,
                        label2 = stringResource(R.string.common_open),
                        backgroundColor2 = uiState.channelMode.color
                    )
                }
            }
            item {
                TwoStatusBadgesRow(
                    label1 = stringResource(R.string.common_lamp),
                    backgroundColor1 = uiState.lamp.color,
                    label2 = stringResource(R.string.common_led),
                    backgroundColor2 = uiState.led.color
                )
            }
            item {
                TwoStatusBadgesRow(
                    label1 = stringResource(R.string.common_relay1),
                    backgroundColor1 = uiState.relay1.color,
                    label2 = stringResource(R.string.common_relay2),
                    backgroundColor2 = uiState.relay2.color
                )
            }
            item {
                TwoStatusBadgesRow(
                    label1 = stringResource(R.string.common_photo1),
                    backgroundColor1 = uiState.photo1.color,
                    label2 = stringResource(R.string.common_photo2),
                    backgroundColor2 = uiState.photo2.color
                )
            }
            item {
                TwoStatusBadgesRow(
                    label1 = stringResource(R.string.common_open1),
                    backgroundColor1 = uiState.open1.color,
                    label2 = stringResource(R.string.common_close1),
                    backgroundColor2 = uiState.close1.color
                )
            }
            item {
                TwoStatusBadgesRow(
                    label1 = stringResource(R.string.common_open2),
                    backgroundColor1 = uiState.open2.color,
                    label2 = stringResource(R.string.common_close2),
                    backgroundColor2 = uiState.close2.color
                )
            }
            item {
                TwoStatusBadgesRow(
                    label1 = stringResource(R.string.common_open3),
                    backgroundColor1 = uiState.open3.color,
                    label2 = stringResource(R.string.common_close3),
                    backgroundColor2 = uiState.close3.color
                )
            }
            item {
                TwoStatusBadgesRow(
                    label1 = stringResource(R.string.common_loop_a),
                    backgroundColor1 = uiState.loopA.color,
                    label2 = stringResource(R.string.common_loop_b),
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
                    label = stringResource(R.string.common_delay_time),
                    value = "${uiState.delayTime}sec"
                )
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ControlButton(
                modifier = Modifier.weight(1f),
                text = if (uiState.isTestRunning) stringResource(R.string.monitoring_test_stop_button) else stringResource(
                    R.string.monitoring_test_start_button
                ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (uiState.isTestRunning) Red500 else Blue600,
                    contentColor = Color.White
                ),
                onClick = { viewModel.handleIntent(MonitoringIntent.ToggleTest) }
            )
            ControlButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.monitoring_config_button),
                onClick = { navController.navigate(MainTab.Configuration.route) }
            )
            ControlButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.monitoring_board_test_button),
                onClick = { navController.navigate(MainTab.BoardTest.route) }
            )
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
            modifier = Modifier.weight(1f),
            text = label1,
            backgroundColor = backgroundColor1
        )
        StatusBadge(
            modifier = Modifier.weight(1f),
            text = label2,
            backgroundColor = backgroundColor2
        )
    }
}