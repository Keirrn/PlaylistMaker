package com.example.playlistmaker.search.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TrackInteractorImpl(private val repository: TrackRepository) : TrackInteractor {

    override fun searchTrack(term: String): Flow<Pair<List<Track>?, String?>> {
        return repository.searchTracks(term)
            .map { result ->
                Pair(result.tracks, result.errorMessage)
            }
    }
}