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
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.cm.gatecontroller.model.GateStatus
import com.cm.gatecontroller.model.LedStatus
import com.cm.gatecontroller.model.SwitchStatus
import com.cm.gatecontroller.ui.theme.Blue600
import com.cm.gatecontroller.ui.theme.Gray400
import com.cm.gatecontroller.ui.theme.Green500
import com.cm.gatecontroller.ui.theme.Red500
import com.cm.gatecontroller.ui.theme.White100
import com.cm.gatecontroller.ui.theme.Yellow300
import com.cm.gatecontroller.ui.theme.component.ControlButton
import com.cm.gatecontroller.ui.theme.component.InputBadge
import com.cm.gatecontroller.ui.theme.component.LabelAndValue
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoardTestScreen(
    navController: NavController,
    viewModel: BoardTestViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.sideEffect.collectLatest { effect ->
            when (effect) {
                is BoardTestSideEffect.ShowToast -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Board Test") },
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
            LabelAndValue("Version", uiState.version)
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutputButton(
                    label = "LAMP",
                    isOn = uiState.lamp == SwitchStatus.ON,
                    onClick = { onIntent(BoardTestIntent.ToggleLamp) },
                    modifier = Modifier.weight(1f)
                )
                OutputButton(
                    label = "RELAY1",
                    isOn = uiState.relay1 == SwitchStatus.ON,
                    onClick = { onIntent(BoardTestIntent.ToggleRelay1) },
                    modifier = Modifier.weight(1f)
                )
                OutputButton(
                    label = "RELAY2",
                    isOn = uiState.relay2 == SwitchStatus.ON,
                    onClick = { onIntent(BoardTestIntent.ToggleRelay2) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        item {
            LedSelector(
                selectedLed = uiState.led,
                onSelect = { onIntent(BoardTestIntent.SelectLed(it)) }
            )
        }
        item {
            LabelAndValue("POSITION", uiState.position.toString())
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            InputBadgeRow(
                labels = listOf("PHOTO1", "PHOTO2", "LOOP A", "LOOP B"),
                states = listOf(uiState.photo1, uiState.photo2, uiState.loopA, uiState.loopB)
            )
        }
        item {
            InputBadgeRow(
                labels = listOf("OPEN1", "OPEN2", "OPEN3", "OPEN"),
                states = listOf(uiState.open1, uiState.open2, uiState.open3, uiState.openSwitch)
            )
        }
        item {
            InputBadgeRow(
                labels = listOf("CLOSE1", "CLOSE2", "CLOSE3", "CLOSE"),
                states = listOf(uiState.close1, uiState.close2, uiState.close3, uiState.closeSwitch)
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
private fun OutputButton(
    label: String,
    isOn: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isOn) Yellow300 else Gray400,
            contentColor = Color.Black
        ),
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(label, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun LedSelector(selectedLed: LedStatus, onSelect: (LedStatus) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LedStatus.entries.forEach { led ->
            val isSelected = selectedLed == led
            val buttonColor = when (led) {
                LedStatus.OFF -> Gray400
                LedStatus.BLUE -> Blue600
                LedStatus.GREEN -> Green500
                LedStatus.RED -> Red500
                LedStatus.WHITE -> White100
            }
            Button(
                onClick = { onSelect(led) },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .border(
                        width = if (isSelected) 3.dp else 0.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.outline else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentPadding = PaddingValues(4.dp)
            ) {
                Text(
                    text = led.name,
                    color = if (led == LedStatus.WHITE) Color.Black else Color.White,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun InputBadgeRow(labels: List<String>, states: List<SwitchStatus>) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        labels.forEachIndexed { index, label ->
            InputBadge(
                label = label,
                isOn = states.getOrElse(index) { SwitchStatus.OFF } == SwitchStatus.ON,
                modifier = Modifier.weight(1f)
            )
        }
    }
}