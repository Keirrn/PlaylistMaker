package com.example.playlistmaker.media.domain

import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun addPlaylist(playlist: Playlist)

    suspend fun updatePlaylist(playlist: Playlist)

    fun getAllPlaylists(): Flow<List<Playlist>>
    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist): Boolean
    suspend fun getPlaylistById(id: Long): Playlist

    suspend fun getTracksByIds(ids: List<Long>): List<Track>
    suspend fun deleteTrackFromPlaylist(trackId: Long, playlistId: Long)
    suspend fun deletePlaylist(playlist: Playlist)
}