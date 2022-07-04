package com.example.housingassingment1

import java.util.regex.Pattern

class NameState: TextFieldState(
    validator = {name -> isName(name) },
    errorMessage = {name -> nameErrorMessage(name) }
)

private const val NAME_REGEX = "^[A-Za-z][A-Za-z_]{4,30}$"
private fun isName(name : String): Boolean{
    return Pattern.matches(NAME_REGEX, name)
}

private fun nameErrorMessage(name: String) = "Please Enter a Valid Name"