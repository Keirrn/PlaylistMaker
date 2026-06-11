package com.example.playlistmaker.media.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.Playlist
import com.example.playlistmaker.media.domain.PlaylistInteractor
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class PlaylistInfoViewModel(
    private val playlistsInteractor: PlaylistInteractor
) : ViewModel() {

    private val _playlist = MutableLiveData<Playlist>()
    val playlist: LiveData<Playlist> = _playlist
    private val _duration = MutableLiveData<String>()
    val duration: LiveData<String> = _duration
    private val _tracks = MutableLiveData<List<Track>>()
    val tracks: LiveData<List<Track>> = _tracks

    fun loadPlaylist(id: Long) {

        viewModelScope.launch {

            val playlist = playlistsInteractor.getPlaylistById(id)

            _playlist.postValue(playlist)

            val tracks = playlistsInteractor.getTracksByIds(
                playlist.trackIds
            )

            _tracks.postValue(tracks)

            val durationSum = tracks.sumOf { it.trackTimeMillis }

            val minutes = TimeUnit.MILLISECONDS.toMinutes(durationSum)

            _duration.postValue(minutes.toString())
        }
    }
    fun createShareText(): String {
        val playlist = _playlist.value ?: return ""

        val tracks = _tracks.value ?: emptyList()

        return buildString {
            appendLine(playlist.playlistName)

            if (!playlist.playlistDescription.isNullOrBlank()) {
                appendLine(playlist.playlistDescription)
            }

            appendLine("${playlist.tracksCount} треков")

            tracks.forEachIndexed { index, track ->
                appendLine(
                    "${index + 1}. ${track.artistName} - ${track.trackName} (${track.trackTime})"
                )
            }
        }
    }
    fun deleteTrack(track: Track) {

        viewModelScope.launch {

            playlistsInteractor.deleteTrackFromPlaylist(
                playlistId = _playlist.value!!.playlistId,
                trackId = track.trackId
            )

            loadPlaylist(_playlist.value!!.playlistId)
        }
    }
    fun deletePlaylist(onComplete: () -> Unit) {

        viewModelScope.launch {

            val playlist = _playlist.value ?: return@launch

            playlistsInteractor.deletePlaylist(playlist)

            onComplete()
        }
    }
}