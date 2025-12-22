package com.example.playlistmaker.main.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.utill.SingleLiveEvent

class MainViewModel : ViewModel() {
    sealed interface NavigationEvent {
        object OpenSearch : NavigationEvent
        object OpenMedia : NavigationEvent
        object OpenSettings : NavigationEvent
    }

    private val _navigationEvent = SingleLiveEvent<NavigationEvent>()
    val navigationEvent: LiveData<NavigationEvent> = _navigationEvent

    fun onSearchClicked() {
        _navigationEvent.value = NavigationEvent.OpenSearch
    }

    fun onMediaClicked() {
        _navigationEvent.value = NavigationEvent.OpenMedia
    }

    fun onSettingsClicked() {
        _navigationEvent.value = NavigationEvent.OpenSettings
    }
}