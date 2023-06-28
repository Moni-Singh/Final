package com.example.afinal

import android.app.Activity
import android.app.Dialog
import android.app.SearchManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.afinal.Model.User
import com.example.afinal.adapter.UserAdapter
import com.example.afinal.utils.HelperMethods
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
    private lateinit var storageRef: StorageReference
    lateinit var receiver :AirPlaneModeChangeReceiver
    private lateinit var mDbRef:DatabaseReference




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

        val manager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchItem = menu!!.findItem(R.id.search)

        if(searchItem?.actionView is SearchView){
        val searchView = searchItem!!.actionView as SearchView

        searchView.setSearchableInfo(manager.getSearchableInfo(componentName))

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                searchView.setQuery("", false)
                searchItem.collapseActionView()
                Toast.makeText(this@DashBoardActivity, "looking for$query", Toast.LENGTH_SHORT)
                    .show()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                performSearch(newText)
              return true
            }
        })}

        return super.onCreateOptionsMenu(menu)
    }

    private fun performSearch(query: String?) {
        val filteredList = ArrayList<User>()

        if (!query.isNullOrBlank()) {
            for (user in userlist) {
                if (user.name!!.contains(query, ignoreCase = true)) {
                    filteredList.add(user)
                }
            }
        } else {
            filteredList.addAll(userlist)
        }
        userAdapter.setUserList(filteredList!!)
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
                HelperMethods.showOptions(this)
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
                                        userAdapter.notifyDataSetChanged()
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

    private fun perfromSearch() {
        TODO("Not yet implemented")
    }


    override fun onBackPressed() {
        val alertDialog = AlertDialog.Builder(this, R.style.AlertDialogTheme)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("Exit")
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton("Yes", null)
            .setNegativeButton("No", null)
            .show()
        // Set button colors programmatically
        val positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
        positiveButton.setTextColor(resources.getColor(android.R.color.black))
        val negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        negativeButton.setTextColor(resources.getColor(android.R.color.black))

        positiveButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
            alertDialog.dismiss()
        }
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
                println("imageUrl:$userImage")
            }
        }
    }




}




