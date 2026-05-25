package com.example.playlistmaker.utill

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.playlistmaker.R

object ToastUtils {

    fun showPlaylistToast(
        context: Context,
        message: String
    ) {

        val layout = LayoutInflater
            .from(context)
            .inflate(R.layout.toast_playlist_created, null)

        layout.findViewById<TextView>(R.id.toastText).text =
            message


        Toast(context).apply {

            duration = Toast.LENGTH_SHORT
            view = layout

            setGravity(
                Gravity.BOTTOM or Gravity.FILL_HORIZONTAL,
                0,
                60
            )

        }.show()
    }
}