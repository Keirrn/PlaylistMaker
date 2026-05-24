package com.example.playlistmaker.player.data

import android.content.Context
import android.util.TypedValue
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.player.domain.ImageLoadRepository

class ImageLoadRepositoryImpl : ImageLoadRepository {
    override fun loadImage(url: String, target: Any, cornerRadiusDp: Float) {
        val imageView = target as ImageView
        Glide.with(imageView.context)
            .load(url)
            .placeholder(R.drawable.placeholder)
            .transform(
                CenterCrop(),
                RoundedCorners(dpToPx(cornerRadiusDp, imageView.context))
            )
            .into(imageView)
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }
}