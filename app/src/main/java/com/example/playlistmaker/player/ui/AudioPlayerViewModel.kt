package com.example.playlistmaker.player.ui

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.FavoritesInteractor
import com.example.playlistmaker.media.domain.Playlist
import com.example.playlistmaker.media.domain.PlaylistInteractor
import com.example.playlistmaker.player.domain.FormatMillisUseCase
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class AudioPlayerViewModel(
    private val track: Track,
    private val formatTimeUseCase: FormatMillisUseCase,
    private val mediaPlayer: MediaPlayer,
    private val favoritesInteractor: FavoritesInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val playerStateLiveData = MutableLiveData(STATE_DEFAULT)
    fun observePlayerState(): LiveData<Int> = playerStateLiveData
    private val isFavoriteLiveData = MutableLiveData(track.isFavorite)
    fun observeIsFavorite(): LiveData<Boolean> = isFavoriteLiveData

    private val progressTimeLiveData = MutableLiveData(START_VALUE)
    private var timerJob: Job? = null
    fun observeProgressTime(): LiveData<String> = progressTimeLiveData

    private val playlistsLiveData = MutableLiveData<List<Playlist>>()

    fun observePlaylists(): LiveData<List<Playlist>> = playlistsLiveData
    private val playlistMessageLiveData = MutableLiveData<String>()

    fun observePlaylistMessage(): LiveData<String> {
        return playlistMessageLiveData
    }

    init {
        preparePlayer()
        syncFavoriteState()
    }
    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (mediaPlayer.isPlaying) {
                delay(DELAY)
                val formattedTime = formatTimeUseCase(mediaPlayer.currentPosition.toLong())
                progressTimeLiveData.postValue(formattedTime)
            }
        }
    }
    private fun syncFavoriteState() {
        viewModelScope.launch {
            favoritesInteractor.getFavorites().collect { favorites ->
                val isFavorite = favorites.any { it.trackId == track.trackId }
                track.isFavorite = isFavorite
                isFavoriteLiveData.postValue(isFavorite)
            }
        }
    }
    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        mediaPlayer.release()
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
    fun onFavoriteClicked(){
        viewModelScope.launch {

            if (!track.isFavorite) {
                favoritesInteractor.addToFavorites(track)
            } else {
                favoritesInteractor.removeFromFavorites(track)
            }

            track.isFavorite = !track.isFavorite
            isFavoriteLiveData.postValue(track.isFavorite)
        }

    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerStateLiveData.postValue(STATE_PREPARED)
        }
        mediaPlayer.setOnCompletionListener {
            timerJob?.cancel()
            resetTimer()
            playerStateLiveData.postValue(STATE_PREPARED)
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerStateLiveData.postValue(STATE_PLAYING)
        startTimer()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        timerJob?.cancel()
        playerStateLiveData.postValue(STATE_PAUSED)

    }

    private fun resetTimer() {
        mediaPlayer.stop()
        progressTimeLiveData.postValue(START_VALUE)
    }
    fun loadPlaylists() {

        viewModelScope.launch {

            playlistInteractor
                .getAllPlaylists()
                .collect {

                    playlistsLiveData.postValue(it)
                }
        }
    }
    fun onPlaylistClicked(playlist: Playlist) {
        if (playlist.trackIds.contains(track.trackId)) {

            playlistMessageLiveData.postValue(
                "Трек уже добавлен в плейлист ${playlist.playlistName}"
            )

            return
        }
        viewModelScope.launch {

            val result = playlistInteractor.addTrackToPlaylist(
                track,
                playlist
            )

            if (result) {

                playlistMessageLiveData.postValue(
                    "Добавлено в плейлист ${playlist.playlistName}"
                )
            }
        }
    }

    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
        const val  DELAY = 300L
        const val START_VALUE = "00:00"

    }
}