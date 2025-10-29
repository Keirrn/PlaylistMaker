package com.example.playlistmaker.domain.repositories

interface ThemeSwitcher {
    fun switchTheme(darkThemeEnabled: Boolean)
}