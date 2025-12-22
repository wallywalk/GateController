package com.cm.gatecontroller

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cm.gatecontroller.boardtest.BoardTestScreen
import com.cm.gatecontroller.configuration.ConfigurationScreen
import com.cm.gatecontroller.connection.ConnectionStatus
import com.cm.gatecontroller.connection.ConnectionViewModel
import com.cm.gatecontroller.monitoring.MonitoringScreen
import com.cm.gatecontroller.debug.DebugView
import com.cm.gatecontroller.user.UserViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    userViewModel: UserViewModel = hiltViewModel(),
    connectionViewModel: ConnectionViewModel = hiltViewModel(),
) {
    val connectionUiState by connectionViewModel.uiState.collectAsState()
    var debugViewVisible by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val title = when (currentRoute) {
        MainTab.Monitoring.route -> stringResource(R.string.monitoring_title)
        MainTab.Configuration.route -> stringResource(R.string.config_title)
        MainTab.BoardTest.route -> stringResource(R.string.board_test_title)
        else -> stringResource(R.string.main_app_title)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        modifier = Modifier.combinedClickable(
                            onClick = { /* No-op */ },
                            onLongClick = {
                                debugViewVisible = !debugViewVisible
                            }
                        ),
                        text = title
                    )
                },
                navigationIcon = {
                    if (navController.previousBackStackEntry != null) {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.common_back)
                            )
                        }
                    }
                },
                actions = {
                    TextButton(
                        onClick = { userViewModel.logout() }, // TODO: handleIntent
                        content = {
                            Text(stringResource(R.string.common_logout))
                        }
                    )
                    ConnectionStatus(
                        status = connectionUiState.status,
                        deviceName = connectionUiState.connectedDeviceName,
                        onConnectClick = { connectionViewModel.connectToFirstDevice() }
                    )
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            NavHost(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                navController = navController,
                startDestination = MainTab.Monitoring.route
            ) {
                composable(MainTab.Monitoring.route) {
                    MonitoringScreen(
                        navController = navController,
                        showSnackbar = { message ->
                            scope.launch {
                                snackbarHostState.showSnackbar(message)
                            }
                        }
                    )
                }
                composable(MainTab.Configuration.route) {
                    ConfigurationScreen(
                        navController = navController,
                        showSnackbar = { message ->
                            scope.launch {
                                snackbarHostState.showSnackbar(message)
                            }
                        }
                    )
                }
                composable(MainTab.BoardTest.route) {
                    BoardTestScreen(
                        navController = navController,
                        showSnackbar = { message ->
                            scope.launch {
                                snackbarHostState.showSnackbar(message)
                            }
                        }
                    )
                }
            }

            if (debugViewVisible) {
                DebugView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.4f)
                )
            }
        }
    }
}

@Composable
private fun ConnectionStatus(
    status: ConnectionStatus,
    deviceName: String?,
    onConnectClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(status.label, color = status.color)
        Spacer(modifier = Modifier.width(10.dp))

        if (status == ConnectionStatus.DISCONNECTED || status == ConnectionStatus.ERROR) {
            Button(onClick = onConnectClick) {
                Text(stringResource(R.string.common_connect))
            }
        }
    }
}