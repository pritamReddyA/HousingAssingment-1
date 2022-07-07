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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.example.housingassingment1.ui.theme.HousingAssingment1Theme
import kotlinx.coroutines.launch
import java.util.regex.Pattern


class MainActivity : ComponentActivity() {


    val DATA_STORE_FILE_NAME = "user_store.pb"
    val Context.userDataStore: DataStore<User> by dataStore(
        fileName = DATA_STORE_FILE_NAME,
        serializer = UserProtoSerializer
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel  = UserDataViewModel(repo = ProtoUserRepoImpl(this.applicationContext.userDataStore))
        var userRepo : ProtoUserRepo? = null
        setContent {
            userRepo = ProtoUserRepoImpl(userDataStore)
            HousingAssingment1Theme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background) {


                    val userState by viewModel.userState.collectAsState()


                    val (name, setName) = remember {
                        mutableStateOf( userState?.name?:"")
                    }
                    val (phNumber, setPhNumber) = remember {
                        mutableStateOf(userState?.number?:"")
                    }
                    val (email, setEmail) = remember {
                        mutableStateOf(userState?.email?:"")
                    }
                    ProfileScreen( name, setName, phNumber, setPhNumber, email, setEmail, this.applicationContext.userDataStore)
                }
            }




        }
    }
}

// Profile Screen Component
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ProfileScreen(
    name:String,
    setName: (String) -> Unit,
    phNumber: String,
    setPhNumber:(String) ->Unit,
    email:String,
    setEmail: (String) ->Unit,
    userStore: DataStore<User>
                  ){
    // Text Field States
//    val emailState = remember{ EmailState()}
//    val phoneNumberState = remember{PhoneNumberState()}
//    val nameState = remember {NameState()}


    val viewModel  = UserDataViewModel(repo = ProtoUserRepoImpl(userStore))


    val coroutineScope = rememberCoroutineScope()

    val notification = rememberSaveable {
        mutableStateOf("")
    }


    val (isNameError, setIsNameError) = remember {
        mutableStateOf(false)
    }
    val (isNumberError, setIsNumberError) = remember {
        mutableStateOf(false)
    }
    val (isEmailError, setIsEmailError) = remember {
        mutableStateOf(false)
    }

    val (isButtonErrorState, setButtonState) = remember {
        mutableStateOf(true)
    }
    if(!(name == "" || email == "" || phNumber == "")) {
        if(isNameError || isEmailError || isNumberError){
            setButtonState(true)
        }else if(!isNameError && !isEmailError && !isNumberError){
            setButtonState(false)
        }
    }

    // Toast notifications
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

                            Column {
                                NameTextField(name, setName, Modifier, isNameError, setIsNameError, setButtonState)

                            }

                            Column() {
                                NumberTextField(phNumber, setPhNumber, Modifier, isNumberError, setIsNumberError, setButtonState)
                            }

                            Column() {
                                EmailTextField(email,setEmail, Modifier, isEmailError, setIsEmailError, setButtonState)
                            }

                        }
                        Column() {
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(10.dp),
                                onClick = { coroutineScope.launch {

                                    viewModel.addUser(name, phNumber, email)

//                                    userRepo?.updateValue(nameState.text, phoneNumberState.text, emailState.text)
                                }
                                    notification.value = "Data Saved Successfully!"
                                },
                                enabled = !isButtonErrorState
                                ) {
                                Text(text = stringResource(id = R.string.save_details))
                            }
                        }



                    }
                }
                Box(modifier = Modifier
                    .offset(135.dp, (100).dp)
                     ) {
                    Image(painter = painterResource(R.drawable.download), contentDescription = stringResource(
                        id = R.string.avatar), contentScale = ContentScale.Crop, modifier = Modifier
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
fun NameTextField(
    name:String,
    onValueChanged:(String) -> Unit,
    modifier: Modifier,
    isNameError:Boolean,
    setIsNameError: (Boolean) ->Unit,
    setButtonState: (Boolean) -> Unit
){
    val NAME_REGEX = "^[\\p{L} .'-]{4,30}+$"
    fun isError(name: String){
         setIsNameError(!Pattern.matches(NAME_REGEX, name))
    }

    TextField(value = name,
        onValueChange = { onValueChanged(it); isError(it)},
        label = { Text(text = stringResource(id = R.string.name_string)) },
        modifier = modifier
            .fillMaxWidth()
            .padding(0.dp),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.White,
        ),
        isError = isNameError
    )
    if(isNameError){
        ErrorField(error = stringResource(R.string.name_error_text))
    }
}


@Composable
fun NumberTextField(
    number:String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier,
    isNumberError: Boolean,
    setIsNumberError: (Boolean) -> Unit,
    setButtonState:(Boolean)-> Unit
){
    val NUMBER_REGEX = stringResource(R.string.number_regex)
    fun isError(number: String){
        setIsNumberError(!Pattern.matches(NUMBER_REGEX, number))
    }

    TextField(value = number,
        onValueChange = {
            onValueChanged(it); isError(it)
        },
        label = { Text(text = stringResource(R.string.number_string)) },
        modifier = modifier
            .fillMaxWidth()
            .padding(0.dp),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.White
        ),
        leadingIcon = {
            Text(text = stringResource(id = R.string.country_code))
        },
        isError = isNumberError
    )
    if(isNumberError){
        ErrorField(error = stringResource(R.string.number_error))
    }
}

@Composable
fun EmailTextField(
    email:String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier,
    isEmailError: Boolean,
    setIsEmailError:(Boolean)->Unit,
    setButtonState:(Boolean)-> Unit
){
    fun isError(email: String){
        setIsEmailError(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
    }

    TextField(value = email,
        onValueChange = {
            onValueChanged(it); isError(it)
        },
        label = { Text(text = stringResource(id = R.string.email)) },
        modifier = modifier
            .fillMaxWidth()
            .padding(0.dp), colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.White,
        ),
        isError = isEmailError
    )
    if(isEmailError){
        ErrorField(error = stringResource(id = R.string.email_error))
    }
}








//Error text display
@Composable
fun ErrorField(error : String){
    Text(text = error, modifier = Modifier.fillMaxWidth(), style = TextStyle(color= MaterialTheme.colors.error))
}


