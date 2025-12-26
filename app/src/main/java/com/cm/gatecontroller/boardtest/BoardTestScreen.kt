package com.cm.gatecontroller.boardtest

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.cm.gatecontroller.R
import com.cm.gatecontroller.model.LedStatus
import com.cm.gatecontroller.model.SwitchStatus
import com.cm.gatecontroller.model.color
import com.cm.gatecontroller.ui.component.ControlButton
import com.cm.gatecontroller.ui.component.LabelAndBadge
import com.cm.gatecontroller.ui.component.StatusBadge
import com.cm.gatecontroller.ui.theme.GateControllerTheme
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoardTestScreen(
    navController: NavController,
    showSnackbar: (String) -> Unit,
    viewModel: BoardTestViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collectLatest { effect ->
            when (effect) {
                is BoardTestSideEffect.ShowSnackbar -> {
                    showSnackbar(effect.message)
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            BoardTestContent(
                modifier = Modifier.weight(1f),
                uiState = uiState,
                onIntent = viewModel::handleIntent
            )
        }
        ControlButtons(
            isGateOpen = uiState.isGateOpen,
            onIntent = viewModel::handleIntent
        )
    }
}

@Composable
private fun BoardTestContent(
    modifier: Modifier = Modifier,
    uiState: BoardTestUiState,
    onIntent: (BoardTestIntent) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            LabelAndBadge(
                label = stringResource(R.string.common_version),
                badgeText = uiState.version
            )
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatusBadge(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.common_lamp),
                    backgroundColor = uiState.lamp.color,
                    onClick = { onIntent(BoardTestIntent.ToggleLamp) }
                )
                StatusBadge(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.common_relay1),
                    backgroundColor = uiState.relay1.color,
                    onClick = { onIntent(BoardTestIntent.ToggleRelay1) }
                )
                StatusBadge(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.common_relay2),
                    backgroundColor = uiState.relay2.color,
                    onClick = { onIntent(BoardTestIntent.ToggleRelay2) }
                )
            }
        }
        item {
            LedSelectRow(
                selectedLed = uiState.led,
                onSelect = { onIntent(BoardTestIntent.SelectLed(it)) }
            )
        }
        item {
            LabelAndBadge(
                label = stringResource(R.string.common_position),
                badgeText = uiState.position.name,
                onClickBadge = { onIntent(BoardTestIntent.RequestPosition) }
            )
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item {
            InputBadgeRow(
                items = listOf(
                    stringResource(R.string.common_photo1) to uiState.photo1,
                    stringResource(R.string.common_photo2) to uiState.photo2,
                    stringResource(R.string.common_loop_a) to uiState.loopA,
                    stringResource(R.string.common_loop_b) to uiState.loopB
                )
            )
        }
        item {
            InputBadgeRow(
                items = listOf(
                    stringResource(R.string.common_open1) to uiState.open1,
                    stringResource(R.string.common_open2) to uiState.open2,
                    stringResource(R.string.common_open3) to uiState.open3,
                    stringResource(R.string.common_open) to uiState.swOpen
                )
            )
        }
        item {
            InputBadgeRow(
                items = listOf(
                    stringResource(R.string.common_close1) to uiState.close1,
                    stringResource(R.string.common_close2) to uiState.close2,
                    stringResource(R.string.common_close3) to uiState.close3,
                    stringResource(R.string.common_close) to uiState.swClose
                )
            )
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun LedSelectRow(selectedLed: LedStatus, onSelect: (LedStatus) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LedStatus.entries.forEach { led ->
            val isSelected = selectedLed == led
            val currentTextColor =
                if (led == LedStatus.WHITE && isSelected) Color.Black else MaterialTheme.colorScheme.onPrimary

            StatusBadge(
                modifier = Modifier.weight(1f),
                text = when (led) {
                    LedStatus.OFF -> stringResource(R.string.common_off)
                    LedStatus.BLUE -> stringResource(R.string.common_blue)
                    LedStatus.GREEN -> stringResource(R.string.common_green)
                    LedStatus.RED -> stringResource(R.string.common_red)
                    LedStatus.WHITE -> stringResource(R.string.common_white)
                },
                textColor = currentTextColor,
                onClick = { onSelect(led) },
                backgroundColor = if (isSelected) led.color else MaterialTheme.colorScheme.inversePrimary
            )
        }
    }
}

@Composable
private fun InputBadgeRow(items: List<Pair<String, SwitchStatus>>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items.forEach { (label, status) ->
            StatusBadge(
                modifier = Modifier.weight(1f),
                text = label,
                backgroundColor = status.color
            )
        }
    }
}

@Composable
private fun ControlButtons(
    isGateOpen: Boolean,
    onIntent: (BoardTestIntent) -> Unit
) {
    val buttonText = if (isGateOpen) {
        stringResource(R.string.board_test_gate_close_button)
    } else {
        stringResource(R.string.board_test_gate_open_button)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ControlButton(
            modifier = Modifier.weight(1f),
            text = buttonText,
            onClick = { onIntent(BoardTestIntent.ToggleGate) }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BoardTestScreenPreview() {
    GateControllerTheme {
        BoardTestScreen(
            navController = androidx.navigation.compose.rememberNavController(),
            showSnackbar = {}
        )
    }
}
