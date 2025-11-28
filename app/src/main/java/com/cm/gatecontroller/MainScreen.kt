package com.cm.gatecontroller

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cm.gatecontroller.boardtest.BoardTestScreen
import com.cm.gatecontroller.configuration.ConfigurationScreen
import com.cm.gatecontroller.monitoring.MonitoringScreen
import com.cm.gatecontroller.user.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    userViewModel: UserViewModel
) {
    val userUiState by userViewModel.uiState.collectAsState()
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gate Controller") },
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(userUiState.userEmail, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = { userViewModel.logout() }) {
//                            Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                        }
                    }
                }
            )
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "monitoring", // TODO: Change to MainTab.MONITORING.route
            modifier = Modifier.padding(padding)
        ) {
            composable("monitoring") {
                MonitoringScreen(navController = navController)
            }
            composable("configuration") {
                ConfigurationScreen()
            }
            composable("boardtest") {
                BoardTestScreen()
            }
        }
    }
}