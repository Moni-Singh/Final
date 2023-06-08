package com.example.afinal



import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.afinal.Model.User
import com.example.afinal.adapter.UserAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class DashBoardActivity : AppCompatActivity() {
     private lateinit var userRecyclerview:RecyclerView
     private lateinit var userlist: ArrayList<User>
     private lateinit var  adapter:UserAdapter
     private lateinit var  mAuth:FirebaseAuth
    lateinit var receiver :AirPlaneModeChangeReceiver
    private lateinit var mDbRef:DatabaseReference



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_board)
       receiver = AirPlaneModeChangeReceiver()
       IntentFilter (Intent.ACTION_AIRPLANE_MODE_CHANGED).also {
           registerReceiver(receiver, it)


       }


        mAuth = FirebaseAuth.getInstance()
        mDbRef =FirebaseDatabase.getInstance().getReference()

        userlist = ArrayList()
        adapter = UserAdapter(this,userlist)

        userRecyclerview = findViewById(R.id.userRecyclerview)
        userRecyclerview.layoutManager = LinearLayoutManager(this)
        userRecyclerview.adapter = adapter


        mDbRef.child("User").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
            //for clearing user list
                 userlist.clear()

                for(postSnapshot in snapshot.children){

                    val currentUser = postSnapshot.getValue(User::class.java)
                    //for hiding the current logign user
                    if(mAuth.currentUser?.uid != currentUser?.uid){
                        userlist.add(currentUser!!)
                    }

                }
                adapter.notifyDataSetChanged()
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
        if (item.itemId == R.id.logout){
            //logout
            mAuth.signOut()

            val i = Intent(this@DashBoardActivity,LoginActivity::class.java)
            startActivity(i)
            finish()
            return true
        }
        return false

    }


}




