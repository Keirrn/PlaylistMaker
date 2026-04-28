package com.example.playlistmaker.media.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.utill.SingleLiveEvent

class PlaylistsViewModel : ViewModel() {

    private val _navigateToPlaylistCreator = SingleLiveEvent<Unit>()
    val navigateToPlaylistCreator: LiveData<Unit> = _navigateToPlaylistCreator

    fun onNewPlaylistClicked() {
        _navigateToPlaylistCreator.value = Unit
    }
}