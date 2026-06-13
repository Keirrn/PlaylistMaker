package com.example.playlistmaker.media.domain

import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val repository: PlaylistRepository
) : PlaylistInteractor {

    override suspend fun addPlaylist(
        playlist: Playlist
    ) {
        repository.addPlaylist(playlist)
    }

    override suspend fun updatePlaylist(
        playlist: Playlist
    ) {
        repository.updatePlaylist(playlist)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return repository.getAllPlaylists()
    }
    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist): Boolean {

        return repository.addTrackToPlaylist(
            track,
            playlist
        )
    }
    override suspend fun getPlaylistById(id: Long): Playlist {
        return repository.getPlaylistById(id)
    }

    override suspend fun getTracksByIds(ids: List<Long>): List<Track> {
        return repository.getTracksByIds(ids)
    }
    override suspend fun deleteTrackFromPlaylist(
        trackId: Long,
        playlistId: Long
    ) {
        repository.deleteTrackFromPlaylist(
            trackId,
            playlistId
        )
    }
    override suspend fun deletePlaylist(playlist: Playlist) {
        repository.deletePlaylist(playlist)
    }
}