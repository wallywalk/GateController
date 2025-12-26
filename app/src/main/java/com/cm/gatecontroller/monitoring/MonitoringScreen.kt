package com.cm.gatecontroller.monitoring

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.cm.gatecontroller.MainTab
import com.cm.gatecontroller.R
import com.cm.gatecontroller.model.color
import com.cm.gatecontroller.ui.component.ControlButton
import com.cm.gatecontroller.ui.component.LabelAndBadge
import com.cm.gatecontroller.ui.component.StatusBadge
import com.cm.gatecontroller.ui.theme.GateControllerTheme
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
                LabelAndBadge(
                    label = stringResource(R.string.common_version),
                    badgeModifier = Modifier.weight(2f),
                    badgeText = uiState.version
                )
            }
            item {
                LabelAndBadge(
                    label = stringResource(R.string.monitoring_gate),
                    badgeModifier = Modifier.weight(2f),
                    badgeText = stringResource(uiState.gateModeRes)
                )
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
                    label2 = uiState.testCount.toString()
                )
            }
            item {
                LabelAndBadge(
                    label = stringResource(R.string.common_delay_time),
                    badgeModifier = Modifier.weight(2f),
                    badgeText = "${uiState.delayTime}sec"
                )
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        Row( // TODO: 하단 버튼 뷰 통합
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ControlButton(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                text = if (uiState.isTestRunning) stringResource(R.string.monitoring_test_stop_button) else stringResource(
                    R.string.monitoring_test_start_button
                ),
                onClick = { viewModel.handleIntent(MonitoringIntent.ToggleTest) }
            )
            ControlButton(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                text = stringResource(R.string.monitoring_config_button),
                onClick = { navController.navigate(MainTab.Configuration.route) }
            )
            ControlButton(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                text = stringResource(R.string.monitoring_board_test_button),
                onClick = { navController.navigate(MainTab.BoardTest.route) }
            )
        }
    }
}

@Composable
private fun TwoStatusBadgesRow(
    modifier: Modifier = Modifier,
    label1: String,
    backgroundColor1: Color = MaterialTheme.colorScheme.inversePrimary,
    label2: String,
    backgroundColor2: Color = MaterialTheme.colorScheme.inversePrimary,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
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

@Preview(showBackground = true)
@Composable
fun MonitoringScreenPreview() {
    GateControllerTheme {
        MonitoringScreen(
            navController = androidx.navigation.compose.rememberNavController(),
            showSnackbar = {}
        )
    }
}
