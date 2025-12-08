package com.example.playlistmaker.settings.domain

class ThemeInteractor (
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