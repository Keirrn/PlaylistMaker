package com.example.playlistmaker.search.domain

interface ImageLoadRepository {
    fun loadImage(url: String, into: Any, cornerRadiusDp: Float = 2f)
}