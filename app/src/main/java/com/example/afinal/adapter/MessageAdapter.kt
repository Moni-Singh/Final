package com.example.afinal.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.afinal.ChatActivity
import com.example.afinal.Model.Messagechat
import com.example.afinal.R
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(val context: Context, val messageList: ArrayList<Messagechat>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {



    val Item_RECEIVE = 1;
    val Item_SEND = 2;


    class SentViewHolder(itemview: View):RecyclerView.ViewHolder(itemview){
       val sentmessage = itemview.findViewById<TextView>(R.id.tvsendmessage)


    }

    class ReceiveViewHolder(itemview:View): RecyclerView.ViewHolder(itemview){


        val receivemessage = itemview.findViewById<TextView>(R.id.tvreceivemessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
      if(viewType == 1){
          val view: View = LayoutInflater.from(context).inflate(R.layout.receive_layout,parent,false)
          return ReceiveViewHolder(view)
      }else{
          val view: View = LayoutInflater.from(context).inflate(R.layout.send_layout,parent,false)
          return SentViewHolder(view)
      }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]

       if(holder.javaClass == SentViewHolder::class.java){
           //here is code for sent view holder

           val viewholder = holder as SentViewHolder
           holder.sentmessage.text = currentMessage.message

       }else{
           //here is code for receive view holder
           val viewholder =  holder as ReceiveViewHolder
           holder.receivemessage.text = currentMessage.message
       }
    }

    override fun getItemViewType(position: Int): Int {
        val currentmesage = messageList[position]

        if(FirebaseAuth.getInstance().currentUser?.uid.equals(currentmesage.senderId)){

            return Item_SEND
        }else{
            return  Item_RECEIVE

        }
    }

    override fun getItemCount(): Int {
     return  messageList.size
    }
}