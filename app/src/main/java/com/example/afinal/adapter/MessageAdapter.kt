package com.example.afinal.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.afinal.ChatActivity
import com.example.afinal.Model.Messagechat
import com.example.afinal.R
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList

class MessageAdapter(val context: Context, val messageList: ArrayList<Messagechat>, private val listener: MessageAdapter.OnClickSelectMessage) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val Item_RECEIVE = 1
    val Item_SEND = 2


    class SentViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val message = itemview.findViewById<TextView>(R.id.tvsendmessage)
        val sendMessageTime = itemview.findViewById<TextView>(R.id.tvSendChatTime)
        val selectedSendImage = itemview.findViewById<ImageView>(R.id.ivSelectedSendImage)

    }

    class ReceiveViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val message = itemview.findViewById<TextView>(R.id.tvreceivemessage)
        val receiveMessageTime = itemview.findViewById<TextView>(R.id.tvReceiveChatTime)
        val selectedReceiveImage = itemview.findViewById<ImageView>(R.id.ivSelectedReciveImage)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 2) {
            val view: View =
                LayoutInflater.from(context).inflate(R.layout.send_layout, parent, false)
            return SentViewHolder(view)
        } else {
            val view: View =
                LayoutInflater.from(context).inflate(R.layout.receive_layout, parent, false)
            return ReceiveViewHolder(view)
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]

        if (holder.javaClass == SentViewHolder::class.java) {
            var viewHolder = holder as SentViewHolder
            viewHolder.message.text = currentMessage.message
            viewHolder.sendMessageTime.text = currentMessage.currentTime
           if(!currentMessage.image.isNullOrEmpty()){
               Picasso.get().load(currentMessage.image).into(holder.selectedSendImage)
           }else {
               Log.d("UserAdapter", "Image URL: ${currentMessage.image}")
           }
            holder.message.setOnClickListener {

                listener.selectSendmessage(currentMessage)

            }

        } else {
            var viewHolder = holder as ReceiveViewHolder
            viewHolder.message.text = currentMessage.message
            viewHolder.receiveMessageTime.text = currentMessage.currentTime
            viewHolder.receiveMessageTime.text = currentMessage.currentTime
             if (!currentMessage.image.isNullOrEmpty()) {
                Picasso.get().load(currentMessage.image).into(holder.selectedReceiveImage)
            } else {
                Log.d("UserAdapter", "Image URL: ${currentMessage.image}")
            }

            holder.message.setOnClickListener {
                listener.selectSendmessage(currentMessage)
            }

        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentmesage = messageList[position]
        return if (FirebaseAuth.getInstance().currentUser?.uid.equals(currentmesage.senderId)) Item_SEND else Item_RECEIVE

    }


    override fun getItemCount(): Int {
        return messageList.size
    }
interface OnClickSelectMessage{
    fun selectSendmessage(messagelist:Messagechat)
}

}