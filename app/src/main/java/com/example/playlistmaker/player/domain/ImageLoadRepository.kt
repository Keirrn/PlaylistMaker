package com.example.playlistmaker.player.domain

interface ImageLoadRepository {
    fun loadImage(url: String, into: Any, cornerRadiusDp: Float = 2f)
}