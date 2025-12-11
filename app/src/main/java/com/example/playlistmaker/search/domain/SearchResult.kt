package com.example.playlistmaker.search.domain

data class SearchResult(
    val tracks: List<Track>,
    val errorMessage: String?
)