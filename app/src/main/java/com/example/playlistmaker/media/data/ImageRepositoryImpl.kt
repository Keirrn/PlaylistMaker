package com.example.playlistmaker.media.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import com.example.playlistmaker.media.domain.ImageRepository
import java.io.File
import java.io.FileOutputStream

class ImageRepositoryImpl(
    private val context: Context
) : ImageRepository {

    override suspend fun saveImage(uri: Uri): String {

        val filePath = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "playlist_covers"
        )

        if (!filePath.exists()) {
            filePath.mkdirs()
        }

        val fileName = "cover_${System.currentTimeMillis()}.jpg"
        val file = File(filePath, fileName)

        val inputStream = context.contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)

        inputStream.use { input ->
            outputStream.use { output ->
                BitmapFactory
                    .decodeStream(input)
                    .compress(Bitmap.CompressFormat.JPEG, 30, output)
            }
        }

        return file.absolutePath
    }
}