package com.example.playlistmaker.search.domain

interface HistoryManagerRepository {
    fun getHistory(): List<Track>
    fun addTrackToHistory(track: Track)
    fun clearHistory()
}