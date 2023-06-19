package com.example.afinal.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.afinal.ChatActivity
import com.example.afinal.Model.User
import com.example.afinal.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(val context: Context):RecyclerView.Adapter<UserAdapter.UserViewHolder> (){
    val userlist:ArrayList<User> =  ArrayList<User>()

    class UserViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val textName = itemView.findViewById<TextView>(R.id.text_name)
        val emailAddress = itemView.findViewById<TextView>(R.id.textView3)
        val UserImage = itemView.findViewById<ImageView>(R.id.UserImage)


    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
     val view: View = LayoutInflater.from(context).inflate(R.layout.user_layout,parent,false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser  = userlist[position]
        holder.textName.text = currentUser.name
        holder.emailAddress.text = currentUser.email
        if (currentUser.image!!.isNotEmpty()) {
            Picasso.get().load(currentUser.image).into(holder.UserImage)
        } else {
            Log.d("UserAdapter", "Image URL: ${currentUser.image}")
        }
        holder.itemView.setOnClickListener{
            val i = Intent(context,ChatActivity::class.java)
            //send some information from one activity to another
            i.putExtra("name",currentUser.name)
            i.putExtra("uid",currentUser.uid)
            i.putExtra("image",currentUser.image)


            context.startActivity(i)

        }
    }

    fun setUserList(data:List<User>){
        userlist.clear()
        userlist.addAll(data)
        notifyDataSetChanged()
    }

    fun addUser(user:User){
        userlist.add(user)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
      return userlist.size
    }
}