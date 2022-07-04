package com.example.housingassingment1

import java.util.regex.Pattern

class EmailState : TextFieldState(
    validator = {email -> isEmailValid(email)},
    errorMessage = {email-> emailErrorMessage(email)}
)



private fun isEmailValid(email: String): Boolean{
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

private fun emailErrorMessage(email: String) = "Invalid Email"