package com.example.playlistmaker.data.repositories

import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.domain.repositories.ThemeSwitcher

class ThemeSwitcherImpl: ThemeSwitcher {
    override fun switchTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}