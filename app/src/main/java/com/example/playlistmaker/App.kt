package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {
    var darkTheme = false
    lateinit var sharedPrefs: SharedPreferences
    override fun onCreate() {
        super.onCreate()
        sharedPrefs = getSharedPreferences(PLAYLISTMAKER_PREFERENCES, MODE_PRIVATE)

        darkTheme = sharedPrefs.getBoolean(SWITCHER_KEY, false)
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}