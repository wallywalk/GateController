package com.cm.gatecontroller.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.cm.gatecontroller.MainActivity
import com.cm.gatecontroller.ui.theme.GateControllerTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {

    private val viewModel: AuthViewModel by viewModels()

    override fun onStart() {
        super.onStart()
        viewModel.handleIntent(AuthIntent.CheckCurrentUser)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            GateControllerTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope() // TODO: scope 필요 여부

                LaunchedEffect(viewModel.sideEffect) {
                    viewModel.sideEffect.collect { effect ->
                        when (effect) {
                            is AuthSideEffect.ShowSnackbar -> {
                                scope.launch {
                                    snackbarHostState.showSnackbar(effect.message)
                                }
                            }

                            is AuthSideEffect.NavigateToMain -> {
                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                finish()
                            }
                        }
                    }
                }

                Scaffold(
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
                ) { padding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        AuthScreen(
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}