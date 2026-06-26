package com.example.playlistmaker.media.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.FavoritesInteractor
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.utill.SingleLiveEvent
import com.example.playlistmaker.utill.debounce
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoritesInteractor: FavoritesInteractor,
) : ViewModel() {
    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private val stateLiveData = MutableLiveData<FavoritesState>()
    private val _openPlayerEvent = SingleLiveEvent<Track>()
    val openPlayerEvent: LiveData<Track> = _openPlayerEvent
    private val clickDebounce = debounce<Track>(
        delayMillis = CLICK_DEBOUNCE_DELAY,
        coroutineScope = viewModelScope,
        useLastParam = false
    ) { track ->
        _openPlayerEvent.postValue(track)
    }

    init {
        fillData()
    }

    fun fillData() {
        viewModelScope.launch {
            favoritesInteractor
                .getFavorites()
                .collect { tracks ->
                    if (tracks.isEmpty()) {
                        renderState(FavoritesState.Empty)
                    } else {
                        val reversedTracks = tracks.reversed()
                        renderState(FavoritesState.Content(reversedTracks))
                    }
                }
        }
    }

    private fun renderState(state: FavoritesState) {
        stateLiveData.postValue(state)
    }


    fun onTrackClicked(track: Track) {
        clickDebounce(track)
    }

    fun observeState(): LiveData<FavoritesState> = stateLiveData

}
