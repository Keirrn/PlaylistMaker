package com.example.playlistmaker.media.domain

import android.net.Uri

class ImageInteractor(
    private val repository: ImageRepository
) {
    suspend fun saveImage(uri: Uri): String {
        return repository.saveImage(uri)
    }
}