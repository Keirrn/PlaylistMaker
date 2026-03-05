package com.example.playlistmaker.media.data

import com.example.playlistmaker.media.data.db.PlaylistMakerDatabase
import com.example.playlistmaker.media.data.db.TrackEntity
import com.example.playlistmaker.media.domain.FavoriteTracksRepository
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteTracksRepositoryImpl(
    private val playlistMakerDatabase: PlaylistMakerDatabase,
    private val trackDbConvertor: TrackDbConvertor
) : FavoriteTracksRepository {
    override suspend fun addToFavorites(track: Track) {
        playlistMakerDatabase.trackDao().insertTrack(trackDbConvertor.map(track))
    }

    override fun getFavorites(): Flow<List<Track>> = flow {
        val track = playlistMakerDatabase.trackDao().getTracks()
        emit(convertFromTrackEntity(track))
    }
    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConvertor.map(track) }
    }

    override suspend fun removeFromFavorites(track: Track) {
        playlistMakerDatabase.trackDao().deleteTrack(trackDbConvertor.map(track))
    }
}