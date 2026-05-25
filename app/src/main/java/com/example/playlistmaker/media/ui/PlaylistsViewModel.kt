package com.example.playlistmaker.media.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.Playlist
import com.example.playlistmaker.media.domain.PlaylistInteractor
import com.example.playlistmaker.utill.SingleLiveEvent
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _navigateToPlaylistCreator = SingleLiveEvent<Unit>()
    val navigateToPlaylistCreator: LiveData<Unit> = _navigateToPlaylistCreator

    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> = _playlists

    fun onNewPlaylistClicked() {
        _navigateToPlaylistCreator.value = Unit
    }

    fun loadPlaylists() {

        viewModelScope.launch {

            playlistInteractor
                .getAllPlaylists()
                .collect {

                    _playlists.value = it
                }
        }
    }
}