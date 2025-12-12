package com.example.playlistmaker.search.ui

import com.example.playlistmaker.search.domain.Track

sealed interface SearchState {
    data object Loading : SearchState
    data class Content(val tracks: List<Track>) : SearchState
    data class Error(val errorMessage: String, val isNetworkError: Boolean) : SearchState
    data object Empty : SearchState
    data class History(val tracks: List<Track>) : SearchState
}