package com.example.playlistmaker.media.data

import com.example.playlistmaker.media.data.db.dao.PlaylistDao
import com.example.playlistmaker.media.data.db.dao.PlaylistTrackDao
import com.example.playlistmaker.media.domain.Playlist
import com.example.playlistmaker.media.domain.PlaylistRepository
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val playlistDbConvertor: PlaylistDbConvertor,
    private val playlistTrackDao: PlaylistTrackDao): PlaylistRepository {
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
    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist): Boolean {

        if (playlist.trackIds.contains(track.trackId)) {
            return false
        }

        val updatedTrackIds = playlist.trackIds.toMutableList()
        updatedTrackIds.add(track.trackId)

        val updatedPlaylist = playlist.copy(
            trackIds = updatedTrackIds,
            tracksCount = playlist.tracksCount + 1
        )

        playlistDao.updatePlaylist(
            playlistDbConvertor.map(updatedPlaylist)
        )

        playlistTrackDao.insertTrack(
            playlistDbConvertor.mapToPlaylistTrackEntity(track)
        )

        return true
    }
}