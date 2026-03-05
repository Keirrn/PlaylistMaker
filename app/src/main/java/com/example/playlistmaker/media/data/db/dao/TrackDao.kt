package com.example.playlistmaker.media.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.media.data.db.TrackEntity

@Dao
interface TrackDao {
    @Insert(entity = TrackEntity::class ,onConflict =  OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)
    @Delete(entity = TrackEntity::class)
    suspend fun deleteTrack(track: TrackEntity)
    @Query("SELECT * FROM track_table")
    suspend fun getTracks(): List<TrackEntity>
    @Query("SELECT trackId FROM track_table")
    suspend fun getTracksId(): List<Int>
}