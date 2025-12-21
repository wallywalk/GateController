package com.cm.gatecontroller.boardtest

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.cm.gatecontroller.model.GateStatus
import com.cm.gatecontroller.model.LedStatus
import com.cm.gatecontroller.model.SwitchStatus
import com.cm.gatecontroller.model.color
import com.cm.gatecontroller.ui.theme.Blue600
import com.cm.gatecontroller.ui.theme.Gray400
import com.cm.gatecontroller.ui.theme.Red500
import com.cm.gatecontroller.ui.theme.component.ControlButton
import com.cm.gatecontroller.ui.theme.component.LabelAndButton
import com.cm.gatecontroller.ui.theme.component.LabelAndValue
import com.cm.gatecontroller.ui.theme.component.StatusBadge
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoardTestScreen(
    navController: NavController,
    viewModel: BoardTestViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collectLatest { effect ->
            when (effect) {
                is BoardTestSideEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("BOARD TEST") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            BoardTestContent(
                paddingValues = paddingValues,
                uiState = uiState,
                onIntent = viewModel::handleIntent
            )
        }
    }
}

@Composable
private fun BoardTestContent(
    paddingValues: PaddingValues,
    uiState: BoardTestUiState,
    onIntent: (BoardTestIntent) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            LabelAndValue("VERSION", uiState.version)
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
                    text = "LAMP",
                    colors = ButtonDefaults.buttonColors(
                        containerColor = uiState.lamp.color,
                        contentColor = Color.Black
                    ),
                    onClick = { onIntent(BoardTestIntent.ToggleLamp) }
                )
                ControlButton(
                    modifier = Modifier.weight(1f),
                    text = "RELAY1",
                    colors = ButtonDefaults.buttonColors(
                        containerColor = uiState.relay1.color,
                        contentColor = Color.Black
                    ),
                    onClick = { onIntent(BoardTestIntent.ToggleRelay1) }
                )
                ControlButton(
                    modifier = Modifier.weight(1f),
                    text = "RELAY2",
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
                label = "POSITION",
                value = uiState.position.name,
                onClick = { onIntent(BoardTestIntent.RequestPosition) }
            )
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            InputBadgeRow(
                items = listOf(
                    "PHOTO1" to uiState.photo1,
                    "PHOTO2" to uiState.photo2,
                    "LOOP A" to uiState.loopA,
                    "LOOP B" to uiState.loopB
                )
            )
        }
        item {
            InputBadgeRow(
                items = listOf(
                    "OPEN1" to uiState.open1,
                    "OPEN2" to uiState.open2,
                    "OPEN3" to uiState.open3,
                    "OPEN" to uiState.openSwitch
                )
            )
        }
        item {
            InputBadgeRow(
                items = listOf(
                    "CLOSE1" to uiState.close1,
                    "CLOSE2" to uiState.close2,
                    "CLOSE3" to uiState.close3,
                    "CLOSE" to uiState.closeSwitch
                )
            )
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                var isOpen by remember(uiState.gateStatus) {
                    mutableStateOf(
                        uiState.gateStatus == GateStatus.OPENING || uiState.gateStatus == GateStatus.OPENED
                    )
                }
                var isClose by remember(uiState.gateStatus) {
                    mutableStateOf(
                        uiState.gateStatus == GateStatus.CLOSING || uiState.gateStatus == GateStatus.CLOSED
                    )
                }

                val openText = if (isOpen) "OPEN STOP" else "GATE OPEN"
                val closeText = if (isClose) "CLOSE STOP" else "GATE CLOSE"

                ControlButton(
                    text = openText,
                    modifier = Modifier.weight(1f),
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
                    text = closeText,
                    modifier = Modifier.weight(1f),
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
                text = led.name,
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
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items.forEach { (label, status) ->
            StatusBadge(
                text = label,
                backgroundColor = status.color,
                modifier = Modifier.weight(1f)
            )
        }
    }
}