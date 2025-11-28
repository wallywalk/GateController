package com.cm.gatecontroller

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import com.cm.gatecontroller.auth.LoginActivity
import com.cm.gatecontroller.ui.theme.GateControllerTheme
import com.cm.gatecontroller.user.UserSideEffect
import com.cm.gatecontroller.user.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val userViewModel: UserViewModel by viewModels()

    override fun onStart() {
        super.onStart()
        userViewModel.checkUserSession()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

                MainScreen(userViewModel = userViewModel)
            }
        }
    }
}