package com.example.housingassingment1

import kotlinx.coroutines.flow.Flow





interface  ProtoUserRepo  {
    suspend fun saveUserInState(user : User)
    suspend fun getUserInState() : Flow<User>
    suspend fun updateValue(name: String, phoneNumber: String, email: String)

}