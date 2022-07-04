package com.example.housingassingment1

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.example.housingassingment1.ui.theme.HousingAssingment1Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : ComponentActivity() {

    private val DATA_STORE_FILE_NAME = "user_store.pb"
    private val Context.userDataStore: DataStore<User> by dataStore(
        fileName = DATA_STORE_FILE_NAME,
        serializer = MySerializer
    )

    private var userRepo : ProtoUserRepo? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            userRepo = ProtoUserRepoImpl(userDataStore)
            HousingAssingment1Theme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background) {

                    ProfileScreen(userRepo)
                }
            }



        }
    }
}


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ProfileScreen(userRepo: ProtoUserRepo?){



    val emailState = remember{ EmailState()}
    val phoneNumberState = remember{PhoneNumberState()}
    val nameState = remember {NameState()}

    val coroutineScope = rememberCoroutineScope()

    val notification = rememberSaveable {
        mutableStateOf("")
    }


    coroutineScope.launch{
        userRepo?.getUserInState()?.collect(){ user ->
            withContext(Dispatchers.Main){
                nameState.text = user.name
                phoneNumberState.text = user.number
                emailState.text = user.email
            }
        }
    }



    if(notification.value.isNotEmpty()){
        Toast.makeText(LocalContext.current, notification.value, Toast.LENGTH_SHORT).show()
        notification.value = ""
    }



    Scaffold(backgroundColor = Color(0xFF6635DF)) {
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Box() {
                Card(
                    Modifier

                        .padding(top = 160.dp)
                ){
                    Column(Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .padding(top = 60.dp),
                        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.fillMaxHeight(0.5f),
                            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceAround) {

                            Column() {
                                TextField(value = nameState.text,
                                    onValueChange = { nameState.text = it; nameState.validate() },
                                    label = { Text(text = "Name") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(0.dp),
                                    colors = TextFieldDefaults.textFieldColors(
                                        backgroundColor = Color.White,
                                    ),
                                    isError = nameState.error != null
                                )
                                nameState.error?.let { ErrorField(it) }
                            }

                            Column() {


                                TextField(value = phoneNumberState.text,
                                    onValueChange = {
                                        phoneNumberState.text = it; phoneNumberState.validate()
                                    },
                                    label = { Text(text = "Phone Number") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(0.dp),
                                    colors = TextFieldDefaults.textFieldColors(
                                        backgroundColor = Color.White
                                    ),
                                    leadingIcon = {
                                        Text(text = "+91")
                                    },
                                    isError = phoneNumberState.error != null
                                )
                                phoneNumberState.error?.let { ErrorField(it) }
                            }

//                            TogiRoundedPicker(
//                                value = phoneNumber.value,
//                                onValueChange = { phoneNumber.value = it },
//                                defaultCountry = getLibCountries().single { it.countryCode == defaultLang },
//                                pickedCountry = {
//                                    phoneCode = it.countryPhoneCode
//                                    defaultLang = it.countryCode
//                                },
//                                error = isValidPhone
//                            )

                            Column() {
                                TextField(value = emailState.text,
                                    onValueChange = {
                                        emailState.text = it
                                        emailState.validate()
                                    },
                                    label = { Text(text = "Email") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(0.dp), colors = TextFieldDefaults.textFieldColors(
                                        backgroundColor = Color.White,
                                    ),
                                    isError = emailState.error != null
                                )
                                emailState.error?.let { ErrorField(it) }
                            }

                        }
                        Column() {
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(10.dp),
                                onClick = { coroutineScope.launch {

                                    userRepo?.updateValue(nameState.text, phoneNumberState.text, emailState.text)
                                }
                                    notification.value = "Data Saved Successfully!"

                                },

                                enabled = (emailState.isValid() && nameState.isValid() && phoneNumberState.isValid())
                                ) {
                                Text(text = "Save Details")
                            }
                        }



                    }
                }
                Box(modifier = Modifier

                    .offset(135.dp, (100).dp)
                     ) {
                    Image(painter = painterResource(R.drawable.download), contentDescription = "avatar", contentScale = ContentScale.Crop, modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .clickable { notification.value = "image clicked" }
                    )
                }
            }

        }

    }

}

@Composable
fun ErrorField(error : String){
    Text(text = error, modifier = Modifier.fillMaxWidth(), style = TextStyle(color= MaterialTheme.colors.error))
}






@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    HousingAssingment1Theme {
        ProfileScreen(userRepo = null)
    }
}

