package com.example.afinal


import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var btnlogin: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        email = findViewById(R.id.loginEmail)
        password = findViewById(R.id.loginPassword)

        sharedPreferences = getSharedPreferences("shared_Preference", MODE_PRIVATE)
        val isLogin = sharedPreferences.getBoolean("isLogin", false)

        if (isLogin) {
            val intent = Intent(this, DashBoardActivity::class.java)
            startActivity(intent)
            finish()
        }


        btnlogin = findViewById(R.id.btnLogin)
        btnlogin.setOnClickListener {

            val email = email.text.toString()
            val password = password.text.toString()
            login(email,password)
        }

        auth = FirebaseAuth.getInstance()

        val btnregistpage: Button = findViewById(R.id.rgisetruserbtn)
        btnregistpage.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegistrationActivity::class.java)
            finish()
            startActivity(intent)
        }
    }

    private fun login(email: String, password: String) {

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener (this) {
            if (it.isSuccessful) {
                val intent = Intent(applicationContext, DashBoardActivity::class.java)
                startActivity(intent)
            } else {

                Toast.makeText(this@LoginActivity,"Unseuccessful",Toast.LENGTH_SHORT).show()
            }

        }
    }
}
