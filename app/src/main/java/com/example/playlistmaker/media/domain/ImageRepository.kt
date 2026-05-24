package com.example.playlistmaker.media.domain

import android.net.Uri

interface ImageRepository {
    suspend fun saveImage(uri: Uri): String
}