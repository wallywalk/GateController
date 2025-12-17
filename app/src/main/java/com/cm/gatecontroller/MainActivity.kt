package com.cm.gatecontroller

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import com.cm.gatecontroller.auth.LoginActivity
import com.cm.gatecontroller.configuration.ConfigSideEffect
import com.cm.gatecontroller.configuration.ConfigViewModel
import com.cm.gatecontroller.ui.theme.GateControllerTheme
import com.cm.gatecontroller.user.UserSideEffect
import com.cm.gatecontroller.user.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val userViewModel: UserViewModel by viewModels()

    private lateinit var openDocumentLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var createDocumentLauncher: ActivityResultLauncher<String>

    override fun onStart() {
        super.onStart()
        userViewModel.checkUserSession()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        openDocumentLauncher =
            registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
                uri?.let {
//                    configurationViewModel.handleIntent(
//                        ConfigurationIntent.FileSelectedForLoad(
//                            it
//                        )
//                    )
                }
            }

        createDocumentLauncher =
            registerForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
                uri?.let {
//                    configurationViewModel.handleIntent(
//                        ConfigurationIntent.FileSelectedForSave(
//                            it
//                        )
//                    )
                }
            }

        setContent {
            GateControllerTheme {
                LaunchedEffect(userViewModel.sideEffect) {
                    userViewModel.sideEffect.collect { effect ->
                        when (effect) {
                            UserSideEffect.NavigateToLogin -> {
                                startActivity(
                                    Intent(
                                        this@MainActivity,
                                        LoginActivity::class.java
                                    ).apply {
                                        flags =
                                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    }
                                )
                            }
                        }
                    }
                }

                MainScreen() // TODO: NavHost + Route/Screen 구조로 Compose 안에서 분리
            }
        }
    }
}
