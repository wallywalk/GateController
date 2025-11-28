package com.cm.gatecontroller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.cm.gatecontroller.ui.theme.GateControllerTheme
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
                MainScreen(userViewModel = userViewModel)
            }
        }
    }
}