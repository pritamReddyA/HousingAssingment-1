package com.example.housingassingment1

import java.util.regex.Pattern

class PhoneNumberState: TextFieldState(
    validator = {phoneNumber -> isPhoneNumber(phoneNumber) },
    errorMessage = {phoneNumber-> passwordErrorMessage(phoneNumber) }
)


private const val NUMBER_REGEX = "^[6789]\\d{9}\$"
private fun isPhoneNumber(phoneNumber: String): Boolean{
    return Pattern.matches(NUMBER_REGEX, phoneNumber)
}

private fun passwordErrorMessage(email: String) = "Please Enter 10 digits/Valid Number"