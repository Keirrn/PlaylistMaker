package com.example.playlistmaker.domain.repositories

interface ImageLoadRepository {
    fun loadImage(url: String, into: Any, cornerRadiusDp: Float = 2f)
}