package com.example.housingassingment1

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class ProtoUserRepoImpl(
    private val protoDataStore: DataStore<User>
) : ProtoUserRepo{

    //Not using this function
    override suspend fun saveUserInState(user: User) {
        protoDataStore.updateData { store->
            store.toBuilder()
                .setEmail(user.email)
                .setName(user.name)
                .setNumber(user.number)
                .build()
        }
    }

    //Getting user details from Proto
    override suspend fun getUserInState(): Flow<User> {
        return protoDataStore.data
            .catch { exp ->
                if (exp is IOException){
                    emit(User.getDefaultInstance())
                }else{
                    throw exp
                }
            }
    }

    //Setting user details in Proto
    override suspend fun updateValue(name: String, phoneNumber: String, email: String){
        protoDataStore.updateData { store->
            store.toBuilder()
                .setName(name)
                .setNumber(phoneNumber)
                .setEmail(email)
                .build()
        }
    }

}