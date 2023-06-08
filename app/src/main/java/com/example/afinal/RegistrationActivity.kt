package com.example.afinal

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.afinal.Model.User
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)


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
            val name = username.text.toString()
            val email = emailreg.text.toString()
            val password = etPass.text.toString()

            registerUser(name,email, password)
        }

    }

    private fun registerUser(name:String,email: String, password: String) {

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) {

            if (it.isSuccessful) {
                addUserToDatabase(name,email,auth.currentUser?.uid!!)
                startActivity(Intent(applicationContext, DashBoardActivity::class.java))
                finish()
            } else {

                Toast.makeText(this@RegistrationActivity,"Unseuccessful",Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun addUserToDatabase(name: String, email: String, uid: String) {
       mDBRef = FirebaseDatabase.getInstance().getReference()
        mDBRef.child("User").child(uid).setValue(User(name,email,uid))


    }


}