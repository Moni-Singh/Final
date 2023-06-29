package com.example.afinal


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.afinal.Model.Messagechat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class sendImage : AppCompatActivity() {


    private lateinit var imageUri: Uri
    private lateinit var database: DatabaseReference
    var receiverRoom:String? = null
    var senderRoom:String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_image)

        val receiveruid = intent.getStringExtra("uid")
        val senderuid = FirebaseAuth.getInstance().currentUser?.uid
        senderRoom = receiveruid + senderuid
        receiverRoom = senderuid + receiveruid
        database = FirebaseDatabase.getInstance().reference



        val imageUriString = intent.getStringExtra("imageUri")
        imageUri = Uri.parse(imageUriString)

        val ivSelectedImage = findViewById<ImageView>(R.id.ivselectedImage)
        ivSelectedImage.setImageURI(imageUri)

        val ivSendMessage = findViewById<ImageView>(R.id.ivsendImage)
        ivSendMessage.setOnClickListener {
            saveImageToDatabase()
            val i = Intent(this@sendImage, ChatActivity::class.java)
            startActivity(i)
        }
    }


private fun saveImageToDatabase() {
    val message = Messagechat(null, null, null, imageUri.toString())

    val senderKey = database.child("Chats").child(senderRoom!!).child("messages").push().key
    val receiverKey = database.child("Chats").child(receiverRoom!!).child("messages").push().key

    if (senderKey != null && receiverKey != null) {
        message.key = senderKey
        message.images = imageUri.toString()

        database.child("Chats").child(senderRoom!!).child("messages").child(senderKey).setValue(message)
        database.child("Chats").child(receiverRoom!!).child("messages").child(receiverKey).setValue(message)

        println("imsgrf:$imageUri")
    }
}

}