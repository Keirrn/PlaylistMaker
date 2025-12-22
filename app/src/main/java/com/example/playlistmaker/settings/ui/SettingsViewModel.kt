package com.example.playlistmaker.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.settings.domain.ThemeInteractor
import com.example.playlistmaker.sharing.domain.NavigationRepository

class SettingsViewModel(
    private val themeInteractor: ThemeInteractor,
    private val navigationRepository: NavigationRepository
) : ViewModel() {

    private val _isDarkTheme = MutableLiveData(themeInteractor.getTheme())
    val isDarkTheme: LiveData<Boolean> = _isDarkTheme

    fun onThemeSwitched(enabled: Boolean) {
        themeInteractor.saveAndApplyTheme(enabled)
        _isDarkTheme.value = enabled
    }

    fun onShareClicked() {
        navigationRepository.shareApp()
    }

    fun onAgreementClicked() {
        navigationRepository.openAgreement()
    }

    fun onSupportClicked() {
        navigationRepository.contactSupport()
    }

}
