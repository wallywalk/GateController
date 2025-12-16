package com.cm.gatecontroller

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.cm.gatecontroller.auth.LoginActivity
import com.cm.gatecontroller.configuration.ConfigurationIntent
import com.cm.gatecontroller.configuration.ConfigurationSideEffect
import com.cm.gatecontroller.configuration.ConfigurationViewModel
import com.cm.gatecontroller.ui.theme.GateControllerTheme
import com.cm.gatecontroller.user.UserSideEffect
import com.cm.gatecontroller.user.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val userViewModel: UserViewModel by viewModels()
    private val configurationViewModel: ConfigurationViewModel by viewModels()

    private lateinit var openDocumentLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var createDocumentLauncher: ActivityResultLauncher<String>

    override fun onStart() {
        super.onStart()
        userViewModel.checkUserSession()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        openDocumentLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            uri?.let { configurationViewModel.handleIntent(ConfigurationIntent.FileSelectedForLoad(it)) }
        }

        createDocumentLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
            uri?.let { configurationViewModel.handleIntent(ConfigurationIntent.FileSelectedForSave(it)) }
        }

        setContent {
            GateControllerTheme {
                // UserViewModel SideEffect handling
                LaunchedEffect(userViewModel.sideEffect) {
                    userViewModel.sideEffect.collect { effect ->
                        when (effect) {
                            UserSideEffect.NavigateToLogin -> {
                                startActivity(Intent(this@MainActivity, LoginActivity::class.java).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                })
                            }
                        }
                    }
                }

                // ConfigurationViewModel SideEffect handling
                LaunchedEffect(configurationViewModel.sideEffect) {
                    configurationViewModel.sideEffect.collect { effect ->
                        when (effect) {
                            is ConfigurationSideEffect.ShowSnackbar -> {
                                // Snackbar is handled by Scaffold in MainScreen, pass it down
                                // For now, we'll just log or handle it here if needed
                            }
                            is ConfigurationSideEffect.ShowConfirmDialog -> {
                                // Handle confirmation dialog here if needed
                            }
                            ConfigurationSideEffect.LaunchFilePicker -> {
                                openDocumentLauncher.launch(arrayOf("application/json"))
                            }
                            ConfigurationSideEffect.LaunchFileSaver -> {
                                createDocumentLauncher.launch("config.json")
                            }
                            ConfigurationSideEffect.ShowRelayMapDialog -> {
                                // Show Relay Map Dialog
                                // For now, a simple text dialog. If an image is provided, it can be displayed here.
                                // You might want to use a MutableState in the ViewModel to control dialog visibility
                                // and pass the image resource ID if available.
                                // For this example, we'll just show a snackbar or a simple dialog.
                                // Since we don't have a direct way to show a dialog from here without a Composable context,
                                // we'll use a temporary snackbar to indicate the action.
                                // A better approach would be to have a dialog state in ConfigurationUiState.
                                // For now, let's just show a simple log or a placeholder.
                                // For a real dialog, you'd typically manage a `showDialog` state in the ViewModel.
                                // As a placeholder, we'll just log it.
                                android.util.Log.d("MainActivity", "Showing Relay Map Dialog (placeholder)")
                            }
                        }
                    }
                }

                MainScreen( // TODO: NavHost + Route/Screen 구조로 Compose 안에서 분리
                    userViewModel = userViewModel,
                    configurationViewModel = configurationViewModel
                )
            }
        }
    }
}
