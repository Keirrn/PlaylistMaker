package com.example.playlistmaker.domain.interactors

import com.example.playlistmaker.domain.entites.Track

interface TrackInteractor {

    fun searchTrack(term: String, consumer: TrackConsumer)

    interface TrackConsumer {
        fun consume(foundTracks: List<Track>)
    }
}