package com.example.playlistmaker.media.domain

import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun addPlaylist(playlist: Playlist)

    suspend fun updatePlaylist(playlist: Playlist)

    fun getAllPlaylists(): Flow<List<Playlist>>
}