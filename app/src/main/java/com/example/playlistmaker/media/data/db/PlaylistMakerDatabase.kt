package com.example.playlistmaker.media.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.media.data.db.dao.PlaylistDao
import com.example.playlistmaker.media.data.db.dao.TrackDao

@Database(
    version = 3,
    entities = [
        TrackEntity::class,
        PlaylistEntity::class
    ]
)
abstract class PlaylistMakerDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
}