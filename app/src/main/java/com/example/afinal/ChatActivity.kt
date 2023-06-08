package com.example.afinal

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.afinal.Model.Messagechat
import com.example.afinal.adapter.MessageAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class ChatActivity : AppCompatActivity() {

    private lateinit var messageRecyclerView: RecyclerView
    private lateinit var messagebox: EditText
    private lateinit var sendButton:ImageView
    private lateinit var messageList:ArrayList<Messagechat>
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var mDbRef:DatabaseReference

    var receiverRoom:String? = null
    var senderRoom:String? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val name = intent.getStringExtra("name")
        val receiveruid = intent.getStringExtra("uid")
        val senderuid = FirebaseAuth.getInstance().currentUser?.uid

        senderRoom = receiveruid + senderuid
        receiverRoom = senderuid + receiveruid
        supportActionBar?.title = name
        messageRecyclerView = findViewById<RecyclerView>(R.id.rvChathistory)
        messagebox = findViewById<EditText>(R.id.edtmessagebox)
        sendButton = findViewById<ImageView>(R.id.ivsendmessage)

        messageList = ArrayList()
        messageAdapter = MessageAdapter(this,messageList)


        messageRecyclerView.layoutManager = LinearLayoutManager(this)
        messageRecyclerView.adapter = messageAdapter


        //adding data to recycleview
        mDbRef = FirebaseDatabase.getInstance().reference
        mDbRef.child("Chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                        messageList.clear()
                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Messagechat::class.java)
                        messageList.add(message!!)
                    }
                    messageAdapter.notifyDataSetChanged()
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })


        //Adding the message to database
        sendButton.setOnClickListener{

            val message = messagebox.text.toString()

            val msg = Messagechat(message,senderuid)


            mDbRef.child("Chats").child(senderRoom!!).child("messages").push()
                .setValue(msg).addOnSuccessListener {
                    mDbRef.child("Chats").child(receiverRoom!!).child("messages").push()
                        .setValue(msg)
                }

            messagebox.setText("")
        }


    }


}