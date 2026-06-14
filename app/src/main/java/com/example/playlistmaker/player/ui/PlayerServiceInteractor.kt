package com.example.playlistmaker.player.ui

import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.StateFlow

interface PlayerServiceInteractor {
    val playerState: StateFlow<Int>
    val playerPosition: StateFlow<Long>
    fun play()
    fun pause()
    fun showNotification()
    fun hideNotification()
}