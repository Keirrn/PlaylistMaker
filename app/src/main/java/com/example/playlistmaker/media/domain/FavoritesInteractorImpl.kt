package com.example.playlistmaker.media.domain

import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesInteractorImpl(
    private val repository: FavoriteTracksRepository
) : FavoritesInteractor {
    override suspend fun addToFavorites(track: Track) {
        repository.addToFavorites(track)
    }

    override fun getFavorites(): Flow<List<Track>> {
        return repository.getFavorites().map { tracks ->
            tracks.reversed()
        }
    }

    override suspend fun removeFromFavorites(track: Track) {
        repository.removeFromFavorites(track)
    }
}