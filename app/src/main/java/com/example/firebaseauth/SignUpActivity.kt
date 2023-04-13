package com.example.firebaseauth

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.service.autofill.OnClickAction
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.firebaseauth.ui.theme.FirebaseAuthTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class SignUpActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        setContent {
            FirebaseAuthTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    SignUpForm(onClickAction = ::sendUsernameBackToLoginPage)
                }
            }
        }
    }

    private fun sendUsernameBackToLoginPage(nama: String?,username: String?,password: String?){
        auth.createUserWithEmailAndPassword(username!!, password!!)
            .addOnCompleteListener{
                if(it.isSuccessful){
                    val result = Intent().putExtra("username", username)
                    setResult(Activity.RESULT_OK,result)

//                    kirim ke firestore
                    addData(nama!!, username!!, password!!)
                    finish()
                }else{
                    Toast.makeText(applicationContext, "Error Create User", Toast.LENGTH_SHORT).show()
                }
            }

    }


    private fun addData(nama: String?,username: String?,password: String?){
        val fFireStore = Firebase.firestore

        val data = hashMapOf(
            "nama" to nama,
            "username" to username,
            "password" to password
        )

        fFireStore.collection("users")
            .add(data)
            .addOnSuccessListener { documentRefrence ->
                Toast.makeText(applicationContext, "Added data successfuly",Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener{e ->
                Toast.makeText(applicationContext, "Failed add data", Toast.LENGTH_LONG).show()

            }
    }


}

@Composable
fun SignUpForm(
    onClickAction: (String, String, String) -> Unit
) {
    var namaInput by remember { mutableStateOf("") }
    var usernameInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var passwordConf by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextField(
            value = namaInput,
            onValueChange = { namaInput = it },
            label = { Text(text = "Nama") },
            modifier = Modifier.fillMaxWidth(),
        )
        TextField(
            value = usernameInput,
            onValueChange = { usernameInput = it },
            label = { Text(text = "Username") },
            modifier = Modifier.fillMaxWidth(),
        )
        TextField(
            value = passwordInput,
            onValueChange = { passwordInput = it },
            label = { Text(text = "Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        TextField(
            value = passwordConf,
            onValueChange = { passwordConf = it },
            label = { Text(text = "Confirmation Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        Button(
            onClick = {
                if (passwordConf == passwordInput) {
                    onClickAction(namaInput, usernameInput, passwordInput)
                }
                else{
                    Toast.makeText(context, "Gak Sama Nih", Toast.LENGTH_SHORT).show()
                }
            }
        ) {
            Text(text = "Submit")
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview2() {
//    FirebaseAuthTheme {
//        SignUpForm()
//    }
//}