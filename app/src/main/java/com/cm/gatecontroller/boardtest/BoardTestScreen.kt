package com.cm.gatecontroller.boardtest

import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.cm.gatecontroller.R
import com.cm.gatecontroller.model.GateStatus
import com.cm.gatecontroller.model.LedStatus
import com.cm.gatecontroller.model.SwitchStatus
import com.cm.gatecontroller.model.color
import com.cm.gatecontroller.ui.theme.Blue600
import com.cm.gatecontroller.ui.theme.Gray400
import com.cm.gatecontroller.ui.theme.Red500
import com.cm.gatecontroller.ui.component.ControlButton
import com.cm.gatecontroller.ui.component.LabelAndButton
import com.cm.gatecontroller.ui.component.LabelAndValue
import com.cm.gatecontroller.ui.component.StatusBadge
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
            gateStatus = uiState.gateStatus,
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
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            LabelAndValue(stringResource(R.string.common_version), uiState.version)
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ControlButton(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.common_lamp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = uiState.lamp.color,
                        contentColor = Color.Black
                    ),
                    onClick = { onIntent(BoardTestIntent.ToggleLamp) }
                )
                ControlButton(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.common_relay1),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = uiState.relay1.color,
                        contentColor = Color.Black
                    ),
                    onClick = { onIntent(BoardTestIntent.ToggleRelay1) }
                )
                ControlButton(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.common_relay2),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = uiState.relay2.color,
                        contentColor = Color.Black
                    ),
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
            LabelAndButton(
                label = stringResource(R.string.common_position),
                value = uiState.position.name,
                onClick = { onIntent(BoardTestIntent.RequestPosition) }
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
                    stringResource(R.string.common_open) to uiState.openSwitch
                )
            )
        }
        item {
            InputBadgeRow(
                items = listOf(
                    stringResource(R.string.common_close1) to uiState.close1,
                    stringResource(R.string.common_close2) to uiState.close2,
                    stringResource(R.string.common_close3) to uiState.close3,
                    stringResource(R.string.common_close) to uiState.closeSwitch
                )
            )
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun LedSelectRow(selectedLed: LedStatus, onSelect: (LedStatus) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LedStatus.entries.forEach { led ->
            val isSelected = selectedLed == led

            ControlButton(
                modifier = Modifier
                    .weight(1f)
                    .border(
                        width = if (isSelected) 2.dp else 0.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.outline else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    ),
                text = when (led) {
                    LedStatus.OFF -> stringResource(R.string.common_off)
                    LedStatus.BLUE -> stringResource(R.string.common_blue)
                    LedStatus.GREEN -> stringResource(R.string.common_green)
                    LedStatus.RED -> stringResource(R.string.common_red)
                    LedStatus.WHITE -> stringResource(R.string.common_white)
                },
                fontSize = 12.sp,
                onClick = { onSelect(led) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) led.color else Gray400,
                    contentColor = if (led == LedStatus.WHITE && isSelected) Color.Black else Color.White
                )
            )
        }
    }
}

@Composable
private fun InputBadgeRow(items: List<Pair<String, SwitchStatus>>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
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
    gateStatus: GateStatus,
    onIntent: (BoardTestIntent) -> Unit
) {
    var isOpen by remember(gateStatus) { mutableStateOf(gateStatus == GateStatus.OPENING || gateStatus == GateStatus.OPENED) }
    var isClose by remember(gateStatus) {
        mutableStateOf(gateStatus == GateStatus.CLOSING || gateStatus == GateStatus.CLOSED)
    }

    val openText =
        if (isOpen) stringResource(R.string.board_test_open_stop_button) else stringResource(R.string.board_test_gate_open_button)
    val closeText =
        if (isClose) stringResource(R.string.board_test_close_stop_button) else stringResource(R.string.board_test_gate_close_button)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ControlButton(
            modifier = Modifier.weight(1f),
            text = openText,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isOpen) Red500 else Blue600,
                contentColor = Color.White
            ),
            onClick = {
                isOpen = !isOpen
                onIntent(BoardTestIntent.ToggleGateOpen)
            }
        )
        ControlButton(
            modifier = Modifier.weight(1f),
            text = closeText,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isClose) Red500 else Blue600,
                contentColor = Color.White
            ),
            onClick = {
                isClose = !isClose
                onIntent(BoardTestIntent.ToggleGateClose)
            }
        )
    }
}