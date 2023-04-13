package com.example.project3activity.contracts

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.example.firebaseauth.SignUpActivity

class HomeContracts : ActivityResultContract<String?, String?>() {
    override fun createIntent(context: Context, input: String?): Intent {
        return Intent(context, SignUpActivity::class.java)
    }

    override fun parseResult(resultCode: Int, username: Intent?): String?  = when {
        resultCode != Activity.RESULT_OK -> null
        else -> username?.getStringExtra("username")


    }
}