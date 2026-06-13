package com.example.playlistmaker.media.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.media.data.db.PlaylistTrackEntity
@Dao
interface PlaylistTrackDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: PlaylistTrackEntity)
    @Query("SELECT * FROM playlist_track_table")
    suspend fun getTracks(): List<PlaylistTrackEntity>
    @Query("DELETE FROM playlist_track_table WHERE trackId = :trackId")
    suspend fun deleteTrack(trackId: Long)
}