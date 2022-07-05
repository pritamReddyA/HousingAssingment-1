package com.example.housingassingment1

import kotlinx.coroutines.flow.Flow





interface  ProtoUserRepo  {
    //Not using this function
    suspend fun saveUserInState(user : User)
    //Getting user details from Proto
    suspend fun getUserInState() : Flow<User>
    //Setting user details in Proto
    suspend fun updateValue(name: String, phoneNumber: String, email: String)

}