package com.example.playlistmaker.data.repositories

import com.example.playlistmaker.domain.interactors.TrackInteractor
import com.example.playlistmaker.domain.repositories.TrackRepository
import java.util.concurrent.Executors

class TrackInteractorImpl(private val repository: TrackRepository) : TrackInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTrack(term: String, consumer: TrackInteractor.TrackConsumer) {
        executor.execute {
            consumer.consume(repository.searchTracks(term))
        }
    }
}