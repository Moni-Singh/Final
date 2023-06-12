package com.example.afinal

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.afinal.Model.User
import com.example.afinal.adapter.UserAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.util.*


class DashBoardActivity : AppCompatActivity() {
    private lateinit var userRecyclerview:RecyclerView
    private lateinit var userlist: ArrayList<User>
    private lateinit var  userAdapter:UserAdapter
    private lateinit var  mAuth:FirebaseAuth
    private lateinit var  userImage:ImageView
    private lateinit var  submitButton:Button
    private lateinit var  cancelButton:Button
    private  var  progressBar:ProgressBar? = null
    private lateinit var storageRef: StorageReference


    lateinit var receiver :AirPlaneModeChangeReceiver
    private lateinit var mDbRef:DatabaseReference
    private var imageUrl: String = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_board)
        receiver = AirPlaneModeChangeReceiver()

        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.title = "Chat Application"

        supportActionBar!!.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(this, R.color.chatcolor)))


        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.chatcolor)
        IntentFilter (Intent.ACTION_AIRPLANE_MODE_CHANGED).also {
            registerReceiver(receiver, it)



        }

// In onCreate or initialization
        storageRef = FirebaseStorage.getInstance().reference
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


            val dialog = Dialog(this@DashBoardActivity)
            dialog.setContentView(R.layout.dialogbox_uploadimage)
            dialog.window?.setBackgroundDrawableResource(R.drawable.chatborder)


            userImage = dialog.findViewById(R.id.userImage)

            val cardviewDailog: CardView = dialog.findViewById(R.id.cardviewDailog)
            submitButton = dialog.findViewById(R.id.submitButton)
            cancelButton = dialog.findViewById(R.id.cancelButton)
            val progressBar = dialog.findViewById<ProgressBar>(R.id.progress)
            cardviewDailog.setOnClickListener{


                val options =
                    arrayOf<CharSequence>("Take Photo", "Choose photo from Gallery", "Cancel")
                val builder = AlertDialog.Builder(this@DashBoardActivity)
                builder.setTitle("Choose an option")
                builder.setItems(options) { dialog, item ->
                    when (item) {
                        0 -> {
                            val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            startActivityForResult(takePicture, 0)
                        }
                        1 -> {
                            val pickPhoto = Intent(
                                Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            )
                            startActivityForResult(pickPhoto, 1)
                        }
                        2 -> dialog.dismiss()
                    }
                }
                builder.show()
            }

            cancelButton!!.setOnClickListener {
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Do you want to Cancel?")
                builder.setTitle("Alert!")
                builder.setCancelable(false)
                builder.setPositiveButton("Yes") { dialog, which -> finish() }
                builder.setNegativeButton("No") { dialog, which -> dialog.cancel() }
                val alertDialog = builder.create()
                alertDialog.show()
            }


            submitButton.setOnClickListener {
                val progressBar = findViewById<ProgressBar>(R.id.progress)


                progressBar?.visibility = View.VISIBLE // Show the progressBar

                val drawable = userImage.drawable as BitmapDrawable
                val imageBitmap = drawable.bitmap
                val filename = "image_${System.currentTimeMillis()}.jpg"
                val imageRef = storageRef.child(filename)

                val baos = ByteArrayOutputStream()
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val imageData = baos.toByteArray()
                val uploadTask = imageRef.putBytes(imageData)
                uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let { throw it }
                    }

                    imageRef.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Get the download URL of the uploaded image
                        val downloadUri = task.result

                        val currentUser = mAuth.currentUser
                        if (currentUser != null) {
                            val uid = currentUser.uid
                            mDbRef.child("User").child(uid).child("image")
                                .setValue(downloadUri.toString())
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(
                                            this@DashBoardActivity,
                                            "Image uploaded successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        dialog.dismiss()
                                    } else {
                                        Toast.makeText(
                                            this@DashBoardActivity,
                                            "Failed to upload image",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                    progressBar?.visibility = View.GONE
                                }
                        }

                    } else {
                        Toast.makeText(
                            this@DashBoardActivity,
                            "Failed to upload image",
                            Toast.LENGTH_SHORT
                        ).show()

                        progressBar?.visibility = View.GONE
                    }
                }
            }

            dialog.show()
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



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0) {
                val image = data?.extras?.get("data") as Bitmap
                userImage.setImageBitmap(image)
            } else if (requestCode == 1) {
                val imageUri = data?.data
                userImage.setImageURI(imageUri)
                println("imaehmdih:$userImage")
            }
        }
    }




}




