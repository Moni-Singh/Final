package com.example.afinal.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.afinal.ChatActivity
import com.example.afinal.Model.User
import com.example.afinal.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class UserAdapter(val context: Context, val userlist:ArrayList<User>):RecyclerView.Adapter<UserAdapter.UserViewHolder> (){

    class UserViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val textName = itemView.findViewById<TextView>(R.id.text_name)


    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
     val view: View = LayoutInflater.from(context).inflate(R.layout.user_layout,parent,false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser  = userlist[position]

        holder.textName.text = currentUser.name

        holder.itemView.setOnClickListener{
            val i = Intent(context,ChatActivity::class.java)
            //send some infrormation from one activity to another
            i.putExtra("name",currentUser.name)
            i.putExtra("uid",currentUser.uid)

            context.startActivity(i)
        }

    }

    override fun getItemCount(): Int {
      return userlist.size
    }
}