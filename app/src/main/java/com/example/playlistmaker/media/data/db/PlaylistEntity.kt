package com.example.playlistmaker.media.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val playlistId: Int,
    val playlistName: String,
    val playlistDescription: String?,
    val coverPath: String?,
    val trackIds: String,
    val tracksCount: Int
)