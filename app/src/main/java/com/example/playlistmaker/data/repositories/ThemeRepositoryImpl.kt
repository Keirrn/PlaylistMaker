package com.example.playlistmaker.data.repositories

import android.content.SharedPreferences
import com.example.playlistmaker.SWITCHER_KEY
import com.example.playlistmaker.domain.repositories.ThemeRepository


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