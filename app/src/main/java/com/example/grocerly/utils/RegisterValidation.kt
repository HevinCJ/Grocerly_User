package com.example.grocerly.utils

sealed class RegisterValidation {
     object Success : RegisterValidation()
    class Failed(val message:String):RegisterValidation()
}

data class RegisterFieldState(
    val name:RegisterValidation,
    val email:RegisterValidation,
    val password:RegisterValidation
)

data class LoginRegisterFieldState(

    val email:RegisterValidation,
    val password:RegisterValidation
)