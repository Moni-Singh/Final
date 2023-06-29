package com.example.afinal

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Patterns
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.afinal.Model.User
import com.example.afinal.utils.HelperMethods
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase


class RegistrationActivity : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth
    private lateinit var username: EditText
    private lateinit var etPass: EditText
    private lateinit var emailreg: EditText
    private lateinit var btnReg: Button
    private lateinit var rgisetrbtn: Button
    private lateinit var mDBRef:DatabaseReference

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        supportActionBar?.apply {
            setDisplayShowTitleEnabled(true)
            title = "Registration"
            val titleColor = android.graphics.Color.WHITE
            val text = SpannableString(title)
            text.setSpan(ForegroundColorSpan(titleColor), 0, text.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            setTitle(text)
        }




        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.cardcolor)
        supportActionBar!!.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(this, R.color.cardcolor))
        )

        auth = Firebase.auth

        username = findViewById(R.id.regusername)
        emailreg = findViewById(R.id.regEmail)
        etPass = findViewById(R.id.loginPassword)

        btnReg = findViewById(R.id.btnRegister) as Button
        rgisetrbtn = findViewById(R.id.rgisetrbtn) as Button
        rgisetrbtn.setOnClickListener {
            startActivity(Intent(applicationContext, LoginActivity::class.java))
        }
        FirebaseApp.initializeApp(this)



        btnReg.setOnClickListener {
            if (HelperMethods.isNetworkAvailable(this@RegistrationActivity)) {
                val name = username.text.toString()
                val email = emailreg.text.toString()
                val password = etPass.text.toString()

                registerUser(name, email, password, image = "")
            }
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        val passwordRegex =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=])(?=\\S+\$).{8,}\$"
        return password.matches(passwordRegex.toRegex())
    }

    private fun registerUser(name:String,email: String, password: String,image: String) {


        if (email.isBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email should not be blank", Toast.LENGTH_SHORT).show()
            return
        }

        if (name.isBlank()) {
            Toast.makeText(this, "Name  should not be blank", Toast.LENGTH_SHORT).show()
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

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) {

                if (it.isSuccessful) {
                    addUserToDatabase(name, email, image, auth.currentUser?.uid!!)
                    startActivity(Intent(applicationContext, LoginActivity::class.java))
                    finish()
                } else {

                    Toast.makeText(this@RegistrationActivity, "Unsuccessful", Toast.LENGTH_SHORT)
                        .show()
                }
            }

        }

        private fun addUserToDatabase(name: String, email: String, image: String, uid: String) {
            mDBRef = FirebaseDatabase.getInstance().getReference()
            mDBRef.child("User").child(uid).setValue(User(name, email, uid, image))


        }

    }
