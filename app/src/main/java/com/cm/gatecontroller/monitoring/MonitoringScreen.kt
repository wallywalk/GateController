package com.cm.gatecontroller.monitoring

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.cm.gatecontroller.model.LedStatus
import com.cm.gatecontroller.model.SwitchStatus
import com.cm.gatecontroller.monitoring.model.AccessStatus
import com.cm.gatecontroller.ui.theme.Blue600
import com.cm.gatecontroller.ui.theme.Red500
import com.cm.gatecontroller.ui.theme.component.ActiveBadge
import com.cm.gatecontroller.ui.theme.component.ControlButton
import com.cm.gatecontroller.ui.theme.component.LabelAndValue
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
                title = { Text("Monitoring") },
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
                    Text("Gate", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                    TwoActiveBadgesRow(
                        label1 = "CLOSE",
                        isActive1 = uiState.gateState == AccessStatus.CLOSE,
                        label2 = "OPEN",
                        isActive2 = uiState.gateState == AccessStatus.OPEN,
                        modifier = Modifier.weight(2f)
                    )
                }
            }
            item {
                TwoActiveBadgesRow(
                    label1 = "LAMP",
                    isActive1 = uiState.lampState == SwitchStatus.ON,
                    label2 = "LED", // TODO: 색상 표시
                    isActive2 = uiState.ledState != LedStatus.OFF
                )
            }
            item {
                TwoActiveBadgesRow(
                    label1 = "RELAY1",
                    isActive1 = uiState.relay1State == SwitchStatus.ON,
                    label2 = "RELAY2",
                    isActive2 = uiState.relay2State == SwitchStatus.ON
                )
            }
            item {
                TwoActiveBadgesRow(
                    label1 = "PHOTO1",
                    isActive1 = uiState.photo1State == SwitchStatus.ON,
                    label2 = "PHOTO2",
                    isActive2 = uiState.photo2State == SwitchStatus.ON
                )
            }
            item {
                TwoActiveBadgesRow(
                    label1 = "OPEN1",
                    isActive1 = uiState.open1State == SwitchStatus.ON,
                    label2 = "CLOSE1",
                    isActive2 = uiState.close1State == SwitchStatus.ON
                )
            }
            item {
                TwoActiveBadgesRow(
                    label1 = "OPEN2",
                    isActive1 = uiState.open2State == SwitchStatus.ON,
                    label2 = "CLOSE2",
                    isActive2 = uiState.close2State == SwitchStatus.ON
                )
            }
            item {
                TwoActiveBadgesRow(
                    label1 = "OPEN3",
                    isActive1 = uiState.open3State == SwitchStatus.ON,
                    label2 = "CLOSE3",
                    isActive2 = uiState.close3State == SwitchStatus.ON
                )
            }
            item {
                TwoActiveBadgesRow(
                    label1 = "LOOP A",
                    isActive1 = uiState.loopAState == SwitchStatus.ON,
                    label2 = "LOOP B",
                    isActive2 = uiState.loopBState == SwitchStatus.ON
                )
            }
            item {
                TwoValueDisplayRow(
                    label1 = uiState.mainPower,
                    label2 = uiState.testCount.toString()
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
                    ToggleActionButton(
                        text = "Test Start",
                        isToggled = uiState.isTestRunning,
                        onToggle = { viewModel.handleIntent(MonitoringIntent.ToggleTest) },
                        modifier = Modifier.weight(1f)
                    )
                    ControlButton(
                        text = "Config",
                        modifier = Modifier.weight(1f)
                    ) { navController.navigate("configuration") }
                    ControlButton(
                        text = "Board Test",
                        modifier = Modifier.weight(1f)
                    ) { navController.navigate("boardtest") }
                }
            }
        }
    }
}

@Composable
private fun TwoActiveBadgesRow(
    label1: String,
    isActive1: Boolean,
    label2: String,
    isActive2: Boolean,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ActiveBadge(text = label1, isActive = isActive1, modifier = Modifier.weight(1f))
        ActiveBadge(text = label2, isActive = isActive2, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun TwoValueDisplayRow(label1: String, label2: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(label1, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(label2, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
private fun ToggleActionButton(
    text: String,
    isToggled: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onToggle,
        modifier = modifier.height(64.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isToggled) Red500 else Blue600,
            contentColor = Color.White
        )
    ) {
        Text(text, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}
