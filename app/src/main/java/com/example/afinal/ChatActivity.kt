package com.example.afinal

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.afinal.Model.Messagechat
import com.example.afinal.adapter.MessageAdapter
import com.example.afinal.utils.HelperMethods
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_camera.*
import java.util.*


class ChatActivity : AppCompatActivity(),MessageAdapter.OnClickSelectMessage {

    private lateinit var messageRecyclerView: RecyclerView
    private lateinit var messagebox: EditText
    private lateinit var sendButton:ImageView
    private lateinit var messageList:ArrayList<Messagechat>
    private lateinit var messageAdapter: MessageAdapter
    private val selectedImages: MutableList<Uri> = mutableListOf()
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
        val backarrow = actionBarLayout.findViewById<ImageView>(R.id.backArrowIv)
        messageRecyclerView = findViewById<RecyclerView>(R.id.rvChathistory)
        messagebox = findViewById<EditText>(R.id.edtmessagebox)
        val messagebox = findViewById<EditText>(R.id.edtmessagebox)
        val ivCamera = findViewById<ImageView>(R.id.ivCamera)
        sendButton = findViewById<ImageView>(R.id.ivsendmessage)
        val ivAttachFile = findViewById<ImageView>(R.id.ivAttachFile)
        messageList = ArrayList()
        messageAdapter = MessageAdapter(this,messageList,this)
        messageRecyclerView.layoutManager = LinearLayoutManager(this)
        messageRecyclerView.adapter = messageAdapter

        val llm = LinearLayoutManager(this)
        llm.stackFromEnd = true
        llm.reverseLayout = false
        messageRecyclerView!!.layoutManager = llm
        scrollToLastItem()
        // Scroll to the last item
        llm.scrollToPosition(messageList.size - 1)

        if (!image.isNullOrEmpty()) {
            Picasso.get().load(image).into(profileImage) }
        titleTextView.text = name

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
                    scrollToLastItem()
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        backarrow.setOnClickListener {
            val i = Intent(this@ChatActivity,DashBoardActivity::class.java)
            startActivity(i)
        }
        //message camera
        ivCamera.setOnClickListener {

            val i = Intent(this@ChatActivity,Camera::class.java)
            startActivity(i)
//            HelperMethods.showOptions(this)
        }


        //Adding the message to database
        sendButton.setOnClickListener {
            val message = messagebox.text.toString()
            var currentTime = getCurrentTime()
            val msg = Messagechat(message, senderuid, currentTime,image)

            if (message.isNotEmpty()) {
                mDbRef.child("Chats").child(senderRoom!!).child("messages").push()
                    .setValue(msg).addOnSuccessListener {
                        mDbRef.child("Chats").child(receiverRoom!!).child("messages").push()
                            .setValue(msg)
                    }
                messagebox.setText("")
            } else {

            }

            scrollToLastItem()
        }


        messagebox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (s.isNullOrEmpty()) {
                    ivCamera.visibility = View.VISIBLE
                    ivAttachFile.visibility = View.VISIBLE
                } else {
                    ivCamera.visibility = View.GONE
                    ivAttachFile.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }
//for message current time
    private fun getCurrentTime(): String? {
      HelperMethods.currentTime(this)
        return getCurrentTime()

    }

    private fun scrollToLastItem() {
        val lastItemPosition = messageList.size - 1
        messageRecyclerView.scrollToPosition(lastItemPosition)
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                0 -> {
                    // Handle image capture from camera (if required)
                }
                1 -> {
                    // Handle image selection from gallery
                    val imageUri: Uri? = data?.data
                    if (imageUri != null) {
                        selectedImages.add(imageUri)

                     messageRecyclerView.adapter?.notifyDataSetChanged()
                    }
                }
            }
        }
    }

}

