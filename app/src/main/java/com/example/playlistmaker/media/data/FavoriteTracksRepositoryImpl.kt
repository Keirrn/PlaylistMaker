package com.example.playlistmaker.media.data

import com.example.playlistmaker.media.data.db.TrackEntity
import com.example.playlistmaker.media.data.db.dao.TrackDao
import com.example.playlistmaker.media.domain.FavoriteTracksRepository
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteTracksRepositoryImpl(
    private val trackDao: TrackDao,
    private val trackDbConvertor: TrackDbConvertor
) : FavoriteTracksRepository {
    override suspend fun addToFavorites(track: Track) {
        trackDao.insertTrack(trackDbConvertor.map(track))
    }

    override fun getFavorites(): Flow<List<Track>> = flow {
        val track = trackDao.getTracks()
        emit(convertFromTrackEntity(track))
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConvertor.map(track) }
    }

    override suspend fun removeFromFavorites(track: Track) {
        trackDao.deleteTrack(trackDbConvertor.map(track))
    }

}