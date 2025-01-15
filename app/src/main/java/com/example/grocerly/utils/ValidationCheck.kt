package com.example.grocerly.utils

import android.util.Patterns

fun validateEmail(email:String):RegisterValidation{
    if (email.isEmpty()) return RegisterValidation.Failed("Email cannot be empty")

    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) return RegisterValidation.Failed("Email format incorrect")

    return RegisterValidation.Success
}

fun validatePassword(password:String):RegisterValidation {

    if (password.isEmpty()) return RegisterValidation.Failed("Password cannot be empty")

    if (password.length<6) return RegisterValidation.Failed("Password should be atleast 6 characters")

    return RegisterValidation.Success
}


fun validateName(name:String):RegisterValidation{

    if (name.isEmpty()) return RegisterValidation.Failed("Required Field")

    if (name.length<6) return RegisterValidation.Failed("Field Should Contain 5 Characters")

    return RegisterValidation.Success
}