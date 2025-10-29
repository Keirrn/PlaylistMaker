package com.example.playlistmaker.domain.repositories

import com.example.playlistmaker.domain.entites.Track

interface HistoryManagerRepository {
    fun getHistory(): List<Track>
    fun addTrackToHistory(track: Track)
    fun clearHistory()
}