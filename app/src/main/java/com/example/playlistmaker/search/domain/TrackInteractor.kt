package com.example.playlistmaker.search.domain

import kotlinx.coroutines.flow.Flow

interface TrackInteractor {

    fun searchTrack(term: String): Flow<Pair<List<Track>?, String?>>

}