package com.example.playlistmaker.settings.data

import android.content.SharedPreferences
import com.example.playlistmaker.creator.SWITCHER_KEY
import com.example.playlistmaker.settings.domain.ThemeRepository

class ThemeRepositoryImpl(
    private val sharedPrefs: SharedPreferences
) : ThemeRepository {
    override fun isDarkThemeEnabled(): Boolean {
        return sharedPrefs.getBoolean(SWITCHER_KEY, false)
    }

    override fun setDarkThemeEnabled(enabled: Boolean) {
        sharedPrefs.edit().putBoolean(SWITCHER_KEY, enabled).apply()
    }

}