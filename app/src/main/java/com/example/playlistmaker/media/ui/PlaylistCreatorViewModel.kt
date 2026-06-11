package com.example.playlistmaker.media.ui

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.ImageInteractor
import com.example.playlistmaker.media.domain.Playlist
import com.example.playlistmaker.media.domain.PlaylistInteractor
import kotlinx.coroutines.launch

class PlaylistCreatorViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val imageInteractor: ImageInteractor
) : ViewModel() {

    private val _coverPath = MutableLiveData<String?>()
    val coverPath: LiveData<String?> = _coverPath

    private val _buttonEnabled = MutableLiveData(false)
    val buttonEnabled: LiveData<Boolean> = _buttonEnabled

    private var selectedImageUri: Uri? = null

    private var currentName: String = ""
    private var currentDescription: String? = null
    private var editingPlaylistId: Long? = null
    private var oldTrackIds: List<Long> = emptyList()
    private var oldTracksCount = 0
    private val _editablePlaylist = MutableLiveData<Playlist>()
    val editablePlaylist: LiveData<Playlist> = _editablePlaylist
    fun loadPlaylist(playlist: Playlist) {

        editingPlaylistId = playlist.playlistId

        oldTrackIds = playlist.trackIds
        oldTracksCount = playlist.tracksCount

        currentName = playlist.playlistName
        currentDescription = playlist.playlistDescription

        _coverPath.value = playlist.coverPath

        _buttonEnabled.value = currentName.isNotBlank()
    }
    fun loadPlaylistForEdit(id: Long) {

        viewModelScope.launch {

            val playlist =
                playlistInteractor.getPlaylistById(id)

            editingPlaylistId = playlist.playlistId

            oldTrackIds = playlist.trackIds
            oldTracksCount = playlist.tracksCount

            currentName = playlist.playlistName
            currentDescription = playlist.playlistDescription

            _coverPath.postValue(playlist.coverPath)
            _editablePlaylist.postValue(playlist)

            _buttonEnabled.postValue(
                playlist.playlistName.isNotBlank()
            )
        }
    }
    fun onNameChanged(name: String) {
        currentName = name
        _buttonEnabled.value = name.isNotBlank()
    }

    fun onDescriptionChanged(description: String?) {
        currentDescription = description
    }

    fun onImageSelected(uri: Uri) {
        selectedImageUri = uri
        _coverPath.value = uri.toString()
    }

    fun hasUnsavedData(): Boolean {
        return currentName.isNotBlank()
                || !currentDescription.isNullOrBlank()
                || selectedImageUri != null
    }

    fun getPlaylistName(): String {
        return currentName
    }

    fun savePlaylist(onComplete: () -> Unit) {

        viewModelScope.launch {

            val savedImagePath = selectedImageUri?.let {
                imageInteractor.saveImage(it)
            } ?: _coverPath.value

            val playlist = Playlist(
                playlistId = editingPlaylistId ?: 0,
                playlistName = currentName,
                playlistDescription = currentDescription,
                coverPath = savedImagePath,
                trackIds = oldTrackIds,
                tracksCount = oldTracksCount
            )

            if (editingPlaylistId == null) {

                playlistInteractor.addPlaylist(playlist)

            } else {

                playlistInteractor.updatePlaylist(playlist)
            }

            onComplete()
        }
    }
}