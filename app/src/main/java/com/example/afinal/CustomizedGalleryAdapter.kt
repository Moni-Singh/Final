package com.example.afinal

import android.content.Context
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Gallery
import android.widget.ImageView

class CustomizedGalleryAdapter(private val context: Context,  private val imageUris: List<Uri>) : BaseAdapter() {


    override fun getCount(): Int {
        return imageUris.size
    }


    override fun getItem(position: Int): Any {
        return position
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val imageView = ImageView(context)
        imageView.setImageURI(Uri.parse(imageUris[position].toString()))
        imageView.layoutParams = Gallery.LayoutParams(200, 200)
        return imageView
    }
}
