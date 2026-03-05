package com.example.playlistmaker.media.ui

import com.example.playlistmaker.search.domain.Track

sealed class FavoritesState {

    object Empty : FavoritesState()

    data class Content(
        val tracks: List<Track>
    ) : FavoritesState()

}