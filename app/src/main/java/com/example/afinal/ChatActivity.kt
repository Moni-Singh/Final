package com.example.afinal

import android.annotation.SuppressLint
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build

import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.afinal.Model.Messagechat
import com.example.afinal.adapter.MessageAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso



class ChatActivity : AppCompatActivity(),MessageAdapter.OnClickSelectMessage {

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

        val actionBar = supportActionBar
        actionBar?.setDisplayShowCustomEnabled(true)
        actionBar?.setDisplayShowTitleEnabled(false)
        val inflater = LayoutInflater.from(this)
        val actionBarLayout = inflater.inflate(R.layout.actionbar_image, null)
        actionBar?.customView = actionBarLayout
        val profileImage = actionBarLayout.findViewById<ImageView>(R.id.profileImage)
        val titleTextView = actionBarLayout.findViewById<TextView>(R.id.UsernameTV)
        val backarrow =actionBarLayout.findViewById<ImageView>(R.id.backArrowIv)
        val deleteIv =actionBarLayout.findViewById<ImageView>(R.id.deleteIv)

        if (!image.isNullOrEmpty()) {
            Picasso.get().load(image).into(profileImage) }
        titleTextView.text = name
        backarrow.setOnClickListener {
            val i = Intent(this@ChatActivity,DashBoardActivity::class.java)
            startActivity(i)
        }

        messageRecyclerView = findViewById<RecyclerView>(R.id.rvChathistory)
        messagebox = findViewById<EditText>(R.id.edtmessagebox)
        sendButton = findViewById<ImageView>(R.id.ivsendmessage)

        messageList = ArrayList()
        messageAdapter = MessageAdapter(this,messageList,this)
        messageRecyclerView.layoutManager = LinearLayoutManager(this)
        messageRecyclerView.adapter = messageAdapter

        val llm = LinearLayoutManager(this)
        llm.stackFromEnd = true
        llm.reverseLayout = false
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
                        message?.key = postSnapshot.key ?: ""
                        messageList.add(message!!)
                    }
                    messageAdapter.notifyDataSetChanged()
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })


        //Adding the message to database
        sendButton.setOnClickListener {
            val message = messagebox.text.toString()
            val msg = Messagechat(message, senderuid)

            if (message.isNotEmpty()) {
                mDbRef.child("Chats").child(senderRoom!!).child("messages").push()
                    .setValue(msg).addOnSuccessListener {
                        mDbRef.child("Chats").child(receiverRoom!!).child("messages").push()
                            .setValue(msg)
                    }
                messagebox.setText("")
            } else {

            }
        }

    }


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun selectSendmessage(messagelist: Messagechat) {
        val selectedMessage = messagelist.message
        println("message $selectedMessage")

        val popupMenu = PopupMenu(this, findViewById<TextView>(R.id.tvsendmessage))
        popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)
        popupMenu.setForceShowIcon(true)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.Reply -> {
                    true
                }
                R.id.Forward -> {

                    true
                }
                R.id.Copy -> {
                    val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clipData = ClipData.newPlainText("Message", selectedMessage)
                    clipboardManager.setPrimaryClip(clipData)

                    true
                }
                R.id.Delete -> {
                    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
                    val messageRef: DatabaseReference = database.getReference("Chats")
                        .child(senderRoom!!)
                        .child("messages")
                        .child(messagelist.key)

                    messageRef.removeValue()
                        .addOnSuccessListener {
                            println("Message deleted successfully")
                            //Rrmoving message from local list
                            messageList.remove(messagelist)
                            messageAdapter.notifyDataSetChanged()
                        }
                        .addOnFailureListener { exception ->
                            println("Failed to delete message: ${exception.message}")
                        }

                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun selectReceivemessage(messagelist: Messagechat) {
        val selectedMessage = messagelist.message
        println("message $selectedMessage")

        val popupMenu = PopupMenu(this, findViewById<TextView>(R.id.tvreceivemessage))
        popupMenu.setForceShowIcon(true)

        popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.Reply -> {
                    true
                }
                R.id.Forward -> {

                    true
                }
                R.id.Copy -> {
                    val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clipData = ClipData.newPlainText("Message", selectedMessage)
                    clipboardManager.setPrimaryClip(clipData)

                    true
                }
                R.id.Delete -> {
                    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
                    val messageRef: DatabaseReference = database.getReference("Chats")
                        .child(receiverRoom!!)
                        .child("messages")
                        .child(messagelist.key)

                    messageRef.removeValue()
                        .addOnSuccessListener {
                            println("Message deleted successfully")
                            //Removing message from local list
                            messageList.remove(messagelist)
                            messageAdapter.notifyDataSetChanged()
                        }
                        .addOnFailureListener { exception ->
                            println("Failed to delete message: ${exception.message}")
                        }

                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }


}

