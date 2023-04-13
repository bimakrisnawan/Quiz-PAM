package com.example.firebaseauth

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.example.firebaseauth.R
import com.example.firebaseauth.ui.theme.FirebaseAuthTheme
import com.example.project3activity.contracts.SignUpContracts
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val username = intent.getStringExtra("username")
        setContent {
            FirebaseAuthTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
//                    Cari firestore dengan parameter
                    MainScreen(onSubmitActionEvent = ::uploadImage, username = username!!)
                }
            }
        }
    }

    private fun uploadImage(img: ImageBitmap, caption: String){
        val fStorage = Firebase.storage
        val storageRef = fStorage.reference

        val fileName = SimpleDateFormat("yyyMMddHHmm'.png'").format(Date())
        val ref = storageRef.child("images/$fileName")

        val stream = ByteArrayOutputStream()
        img.asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream)
        val image = stream.toByteArray()

        var uploadTask = ref.putBytes(image)

        val urlTask = uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            ref.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                addData(downloadUri.toString(), caption)
            } else {
                Toast.makeText(applicationContext, "Failed Upload Image", Toast.LENGTH_LONG).show()
                // Handle failures
                // ...
            }
        }
    }

    private fun addData(imgUrl: String, caption: String){
        val fFireStore = Firebase.firestore

        val data = hashMapOf(
            "caption" to caption,
            "imgUrl" to imgUrl
        )

        fFireStore.collection("images")
            .add(data)
            .addOnSuccessListener { documentRefrence ->
                Toast.makeText(applicationContext, "Added data successfuly",Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener{e ->
                Toast.makeText(applicationContext, "Failed add data", Toast.LENGTH_LONG).show()

            }
    }
}

@SuppressLint("RestrictedApi")
@Composable
fun MainScreen (
    onSubmitActionEvent: (img: ImageBitmap, caption: String) -> Unit,
    username: String
){

    val lContext = LocalContext.current


// firestore dengan username sebagai parameter

//    nama collection
    val collectionRef = FirebaseFirestore.getInstance().collection("users")

//    select where
    val query = collectionRef.whereEqualTo("username", username)

//    kenalin var nama & username buat diisi sama dipanggill
    var nama by remember { mutableStateOf("") }
    var usernamefromdatastore by remember { mutableStateOf("") }

//    buat nanti dipanggil ke text
    LaunchedEffect(username) {
        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result) {
                    nama = document.getString("nama") ?: ""
                    usernamefromdatastore = document.getString("username") ?: ""
                }
            } else {
//                kalo gagal / ga ketemu
            }
        }
    }



    var captionText by remember { mutableStateOf("") }
    var takenImage by remember {
        mutableStateOf(
            BitmapFactory.decodeResource(lContext.resources, R.drawable.img_import).asImageBitmap()
        )
    }

    val takePictureContract = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { _takenImageBitmap ->
            takenImage = _takenImageBitmap.asImageBitmap()
        }
    )

    Column(modifier = Modifier.padding(4.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(bitmap = takenImage, contentDescription = "",
                modifier = Modifier
                    .size(120.dp)
                    .padding(end = 4.dp)
                    .clickable {
                        takePictureContract.launch()
                    })
            Column(modifier = Modifier.fillMaxWidth()) {
                TextField(value = captionText, onValueChange = {captionText = it})
                Button(onClick = {
                    onSubmitActionEvent(takenImage, captionText)
                    captionText = ""
                    takenImage = BitmapFactory.decodeResource(lContext.resources, R.drawable.img_import).asImageBitmap()
                }) {
                    Text(text = "Submit")
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(text = "Halo, ")
            Text(text = nama)
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(text = "Username Kamu Adalah, ")
            Text(text = usernamefromdatastore)
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            val activity = (LocalContext.current as? Activity)
            Button(onClick = {
                activity?.finish()
            }) {
                Text("Kembali ke Halaman Login")
            }
        }
    }

}


//@Preview(showBackground = true)
//@Composable
//fun MainScreenPreview(){
//    MainScreen()
//}
