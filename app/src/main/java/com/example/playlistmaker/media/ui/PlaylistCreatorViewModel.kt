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

    fun createPlaylist(onComplete: () -> Unit) {

        viewModelScope.launch {

            val savedImagePath = selectedImageUri?.let {
                imageInteractor.saveImage(it)
            }

            val playlist = Playlist(
                playlistId = 0,
                playlistName = currentName,
                playlistDescription = currentDescription,
                coverPath = savedImagePath,
                trackIds = emptyList(),
                tracksCount = 0
            )

            playlistInteractor.addPlaylist(playlist)

            onComplete()
        }
    }
}