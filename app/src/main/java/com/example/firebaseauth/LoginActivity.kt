package com.example.firebaseauth

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.firebaseauth.ui.theme.FirebaseAuthTheme
import com.example.project3activity.contracts.SignUpContracts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern

class LoginActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FirebaseAuthTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    LoginScreen(onSignInAction = ::doAuth)

                }
            }
        }
    }
    private fun doAuth(
        username: String,
        password: String,
    ){
        auth = Firebase.auth
        auth.signInWithEmailAndPassword(username , password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

//                    set param buat ditampilin di home
                    startActivity(
                        Intent(this, MainActivity::class.java).putExtra("username", username)
                    )
                } else {
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

}




@Composable
fun LoginScreen(
    onSignInAction: (String, String) ->Unit
) {

    var usernameInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }

    val getUsernameFromSignUp = rememberLauncherForActivityResult(
        contract = SignUpContracts(),
        onResult = { username ->
            usernameInput = username!!
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextField(
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            value = usernameInput,
            onValueChange = {usernameInput = it},
            label = { Text(text = "Username")},
            modifier = Modifier.fillMaxWidth(),

        )
        TextField(value = passwordInput, onValueChange = {passwordInput = it}, label = { Text(text = "Password")},
            visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(),
        )

        Row() {
            Button(onClick = {
                onSignInAction(usernameInput, passwordInput)
            })
            {
                Text(text = "Login")
            }
            Button(onClick = {
                getUsernameFromSignUp.launch("")
            })
            {
                Text(text = "SignUp")
            }
        }

    }


}

//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    FirebaseAuthTheme {
//        LoginScreen()
//    }
//}