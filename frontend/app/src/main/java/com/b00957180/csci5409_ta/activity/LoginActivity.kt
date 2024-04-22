package com.b00957180.csci5409_ta.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.b00957180.csci5409_ta.R
import com.b00957180.csci5409_ta.constants.AppConstants

class LoginActivity : AppCompatActivity() {
    private val usernameString = "aditya"
    private val passwordString = "aditya123"

    private lateinit var usernameTxt: EditText
    private lateinit var passwordTxt: EditText
    private lateinit var loginBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sharedPreferences = getSharedPreferences(AppConstants.APP_SHARED_PREFERENCES, MODE_PRIVATE)

        val isLoggedIn = sharedPreferences.getBoolean(AppConstants.SP_IS_SIGNED_IN_KEY, false)

        if (isLoggedIn) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        usernameTxt = findViewById(R.id.username_et)
        passwordTxt = findViewById(R.id.password_et)
        loginBtn = findViewById(R.id.login_btn)

        loginBtn.setOnClickListener {
            Log.e("UName", (usernameTxt.text.toString() == usernameString).toString())
            Log.e("Pass", (passwordTxt.text.toString() == passwordString).toString())
            if ((usernameTxt.text.toString() == usernameString) && (passwordTxt.text.toString() == passwordString)) {
                sharedPreferences.edit {
                    putBoolean(AppConstants.SP_IS_SIGNED_IN_KEY, true)
                }
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Wrong credentials entered", Toast.LENGTH_SHORT).show()
                passwordTxt.text.clear()
            }
        }
    }
}