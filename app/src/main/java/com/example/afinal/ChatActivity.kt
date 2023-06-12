package com.example.afinal

import android.annotation.SuppressLint
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.afinal.Model.Messagechat
import com.example.afinal.adapter.MessageAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso


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

        supportActionBar!!.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(this, R.color.chatcolor))
        )
        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.chatcolor)
        val name = intent.getStringExtra("name")
        val image = intent.getStringExtra("image")
        val receiveruid = intent.getStringExtra("uid")
        val senderuid = FirebaseAuth.getInstance().currentUser?.uid

        senderRoom = receiveruid + senderuid
        receiverRoom = senderuid + receiveruid
        supportActionBar?.title = name


        // Set custom layout for ActionBar
        val actionBar = supportActionBar
        actionBar?.setDisplayShowCustomEnabled(true)
        actionBar?.setDisplayShowTitleEnabled(false)

        val inflater = LayoutInflater.from(this)
        val actionBarLayout = inflater.inflate(R.layout.actionbar_image, null)
        actionBar?.customView = actionBarLayout

        val profileImage = actionBarLayout.findViewById<ImageView>(R.id.profileImage)
        val titleTextView = actionBarLayout.findViewById<TextView>(R.id.UsernameTV)



        titleTextView.text = name







        messageRecyclerView = findViewById<RecyclerView>(R.id.rvChathistory)
        messagebox = findViewById<EditText>(R.id.edtmessagebox)
        sendButton = findViewById<ImageView>(R.id.ivsendmessage)

        messageList = ArrayList()
        messageAdapter = MessageAdapter(this,messageList)


        messageRecyclerView.layoutManager = LinearLayoutManager(this)
        messageRecyclerView.adapter = messageAdapter

        val llm = LinearLayoutManager(this)
        llm.stackFromEnd = true     // items gravity sticks to bottom
        llm.reverseLayout = false   // item list sorting (new messages start from the bottom)
        messageRecyclerView!!.layoutManager = llm
        messageRecyclerView!!.scrollToPosition(messageList.size -1)
//        messageAdapter!!.notifyDataSetChanged()
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

            if(!!messagebox.text.isEmpty()){
                Toast.makeText(this,"please write something",Toast.LENGTH_SHORT).show()

            }else{

            }

            mDbRef.child("Chats").child(senderRoom!!).child("messages").push()
                .setValue(msg).addOnSuccessListener {
                    mDbRef.child("Chats").child(receiverRoom!!).child("messages").push()
                        .setValue(msg)
                }

            messagebox.setText("")
        }


    }


}

