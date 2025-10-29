package com.example.playlistmaker.domain.interactors

import com.example.playlistmaker.domain.repositories.ThemeRepository
import com.example.playlistmaker.domain.repositories.ThemeSwitcher

class ThemeInteractor(
    private val themeRepository: ThemeRepository,
    private val themeSwitcher: ThemeSwitcher
) {
    fun getTheme(): Boolean {
        return themeRepository.isDarkThemeEnabled()
    }

    fun saveAndApplyTheme(isDark: Boolean) {
        themeRepository.setDarkThemeEnabled(isDark)
        themeSwitcher.switchTheme(isDark)
    }
}