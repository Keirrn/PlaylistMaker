package com.example.playlistmaker.media.data

import com.example.playlistmaker.media.data.db.dao.PlaylistDao
import com.example.playlistmaker.media.domain.Playlist
import com.example.playlistmaker.media.domain.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val playlistDbConvertor: PlaylistDbConvertor): PlaylistRepository {
    override suspend fun addPlaylist(playlist: Playlist) {
        playlistDao.insertPlaylist(playlistDbConvertor.map(playlist))
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistDao.updatePlaylist(playlistDbConvertor.map(playlist))
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> = flow {
        val entities = playlistDao.getPlaylists()
        val playlists = entities.map { playlistDbConvertor.map(it) }
        emit(playlists)
    }
}