package com.example.afinal

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class sendImage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_image)

        val imageUriString = intent.getStringExtra("imageUri")
        val imageUri = Uri.parse(imageUriString)

        val ivSelectedImage = findViewById<ImageView>(R.id.ivselectedImage)
        ivSelectedImage.setImageURI(imageUri)

        val ivsendmessage = findViewById<ImageView>(R.id.ivsendmessage)
       ivSelectedImage.setOnClickListener {

       }

    }
}