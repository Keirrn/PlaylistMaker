package com.example.playlistmaker.main.ui

import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.search.ui.SearchActivity

class MainViewModel : ViewModel() {
    sealed class NavigationEvent {
        object OpenSearch : NavigationEvent()
        object OpenMedia : NavigationEvent()
        object OpenSettings : NavigationEvent()
    }

    private val _navigationEvent = MutableLiveData<NavigationEvent>()
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