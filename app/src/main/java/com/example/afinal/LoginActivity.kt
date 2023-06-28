package com.example.afinal


import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable

import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.afinal.utils.HelperMethods
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

        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.title = "Login"

        supportActionBar!!.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(this, R.color.chatcolor)
            )
        )
        email = findViewById(R.id.loginEmail)
        password = findViewById(R.id.loginPassword)
        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.chatcolor)
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
            login(email, password)
        }

        auth = FirebaseAuth.getInstance()

        val btnregistpage: TextView = findViewById(R.id.rgisetruserbtn)
        btnregistpage.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegistrationActivity::class.java)
            finish()
            startActivity(intent)
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        val passwordRegex =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=])(?=\\S+\$).{8,}\$"
        return password.matches(passwordRegex.toRegex())
    }

    private fun login(email: String, password: String) {
        if (email.isBlank()) {
            Toast.makeText(this, "Email should not be blank", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isPasswordValid(password)) {
            Toast.makeText(
                this,
                "Password must contain at least one digit, one lowercase letter, one uppercase letter, one special character, and be at least 8 characters long.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val progressDialog = ProgressDialog(this@LoginActivity)
        progressDialog.setTitle("Loading")
        progressDialog.setMessage("Please wait")
        progressDialog.show()


        if (HelperMethods.isNetworkAvailable(this@LoginActivity)) {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) {
                if (it.isSuccessful) {

                    val editor = sharedPreferences.edit()
                    editor.putString("email", email)
                    editor.putString("password", password)
                    editor.putBoolean("isLogin", true)
                    editor.apply()
                    val intent = Intent(applicationContext, DashBoardActivity::class.java)
                    startActivity(intent)
                    progressDialog.dismiss()
                } else {

                    Toast.makeText(this@LoginActivity, "Failed", Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                }

            }
        }
    }
}