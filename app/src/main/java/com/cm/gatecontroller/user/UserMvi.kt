package com.cm.gatecontroller.user

data class UserUiState(
    val userEmail: String = ""
)

sealed interface UserSideEffect {
    data object NavigateToLogin : UserSideEffect
}

