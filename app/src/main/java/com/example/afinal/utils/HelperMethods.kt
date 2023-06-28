package com.example.afinal.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.ContextCompat.startActivity
import com.example.afinal.R
import java.text.SimpleDateFormat
import java.util.*

object HelperMethods {
    fun showOptions(context: Context) {
        val options = arrayOf<CharSequence>("Take Photo", "Choose photo from Gallery", "Cancel")
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Choose an option")
        builder.setItems(options) { dialog, item ->
            when (item) {
                0 -> {
                    val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    (context as Activity).startActivityForResult(takePicture, 0)
                }
                1 -> {
                    val pickPhoto = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                    (context as Activity).startActivityForResult(pickPhoto, 1)
                }
                2 -> dialog.dismiss()
            }
        }
        builder.show()
    }


    fun currentTime(context: Context): String {
        val currentTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return  dateFormat.format(currentTime)
    }

//For Internet Connecttion
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val networkCapabilities =
                connectivityManager.getNetworkCapabilities(network) ?: return false
            return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }
    }



}
