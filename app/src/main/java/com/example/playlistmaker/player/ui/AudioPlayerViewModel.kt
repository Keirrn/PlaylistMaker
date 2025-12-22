package com.example.playlistmaker.player.ui

import android.media.MediaPlayer
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.player.domain.FormatMillisUseCase
import kotlinx.coroutines.launch


class AudioPlayerViewModel(
    private val url: String,
    private val formatTimeUseCase: FormatMillisUseCase,
    private val mediaPlayer: MediaPlayer,
    private val handler: android.os.Handler
) : ViewModel() {

    private val playerStateLiveData = MutableLiveData(STATE_DEFAULT)
    fun observePlayerState(): LiveData<Int> = playerStateLiveData

    private val progressTimeLiveData = MutableLiveData(START_VALUE)
    fun observeProgressTime(): LiveData<String> = progressTimeLiveData

    private val updateTimerRunnable = object : Runnable {
        override fun run() {
            if (playerStateLiveData.value == STATE_PLAYING) {
                updateTimer()
                handler.postDelayed(this, DELAY)
            }
        }
    }

    init {
        preparePlayer()
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
        resetTimer()
    }

    fun onPlayButtonClicked() {
        when (playerStateLiveData.value) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }

    fun onPause() {
        pausePlayer()
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerStateLiveData.postValue(STATE_PREPARED)
        }
        mediaPlayer.setOnCompletionListener {
            playerStateLiveData.postValue(STATE_PREPARED)
            resetTimer()
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerStateLiveData.postValue(STATE_PLAYING)
        updateTimer()
        handler.postDelayed(updateTimerRunnable, DELAY)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerStateLiveData.postValue(STATE_PAUSED)
        handler.removeCallbacks(updateTimerRunnable)
    }

    private fun updateTimer() {
        viewModelScope.launch {
            val formattedTime = formatTimeUseCase(mediaPlayer.currentPosition.toLong())
            progressTimeLiveData.postValue(formattedTime)
        }
    }

    private fun resetTimer() {
        handler.removeCallbacks(updateTimerRunnable)
        progressTimeLiveData.postValue(START_VALUE)
    }

    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
        const val  DELAY = 250L
        const val START_VALUE = "00:00"

    }
}
