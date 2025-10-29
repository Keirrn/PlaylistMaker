package com.example.playlistmaker.domain.repositories

import com.example.playlistmaker.domain.entites.Track

interface TrackRepository {
    fun searchTracks(query: String): List<Track>
}