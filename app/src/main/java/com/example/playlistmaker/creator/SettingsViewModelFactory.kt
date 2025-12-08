package com.example.playlistmaker.creator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.settings.domain.ThemeInteractor
import com.example.playlistmaker.settings.ui.SettingsViewModel
import com.example.playlistmaker.sharing.domain.NavigationRepository

class SettingsViewModelFactory(
    private val themeInteractor: ThemeInteractor,
    private val navigationRepository: NavigationRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(themeInteractor, navigationRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}