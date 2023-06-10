package com.example.afinal



import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.afinal.Model.User
import com.example.afinal.adapter.UserAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso


class DashBoardActivity : AppCompatActivity() {
     private lateinit var userRecyclerview:RecyclerView
     private lateinit var userlist: ArrayList<User>
     private lateinit var  userAdapter:UserAdapter
     private lateinit var  mAuth:FirebaseAuth
    lateinit var receiver :AirPlaneModeChangeReceiver
    private lateinit var mDbRef:DatabaseReference
    private var imageUrl: String = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_board)
       receiver = AirPlaneModeChangeReceiver()

        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.title = "Chat Application"


        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.chatcolor)
       IntentFilter (Intent.ACTION_AIRPLANE_MODE_CHANGED).also {
           registerReceiver(receiver, it)


       }


        mAuth = FirebaseAuth.getInstance()
        mDbRef =FirebaseDatabase.getInstance().getReference()

        userlist = ArrayList()
        userAdapter = UserAdapter(this)

        userRecyclerview = findViewById(R.id.userRecyclerview)
        userRecyclerview.layoutManager = LinearLayoutManager(this)
        userRecyclerview.adapter = userAdapter


        mDbRef.child("User").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
            //for clearing user list
                for(postSnapshot in snapshot.children){
                    val currentUser = postSnapshot.getValue(User::class.java)
                    //for hiding the current logign user
                    if(mAuth.currentUser?.uid != currentUser?.uid){
                        userAdapter.addUser(currentUser!!);
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.uploadImage){
            val options = arrayOf<CharSequence>("Take Photo", "Choose photo from Gallery", "Cancel")
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Choose an option")
            builder.setItems(options) { dialog, item ->
                when (item) {
                    0 -> {
                        val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        startActivityForResult(takePicture, 0)
                    }
                    1 -> {
                        val pickPhoto =
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        startActivityForResult(pickPhoto, 1)
                    }
                    2 -> dialog.dismiss()
                }
            }
            builder.show()
        }
        if (item.itemId == R.id.logout) {
            mAuth.signOut()
            val sharedPreferences = getSharedPreferences("shared_Preference", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean("islogin", false)
            editor.clear()
            editor.apply()

            val intent = Intent(this@DashBoardActivity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
            return true
        }
        return  false
    }


    override fun onBackPressed() {
        android.app.AlertDialog.Builder(this, R.style.AlertDialogTheme)
            .setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
            .setMessage("Are you sure want to exit?")
            .setPositiveButton("yes") { _, _ ->
                val intent = Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_HOME)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }.setNegativeButton("no", null).show()
    }

}




