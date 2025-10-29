package com.example.playlistmaker

import android.app.Application
import com.example.playlistmaker.domain.interactors.ThemeInteractor


class App : Application() {

    private lateinit var themeInteractor: ThemeInteractor

    override fun onCreate() {
        super.onCreate()
        themeInteractor = Creator.provideThemeInteractor(this)
        restoreTheme()
    }

    private fun restoreTheme() {
        val isDarkTheme = themeInteractor.getTheme()
        themeInteractor.saveAndApplyTheme(isDarkTheme)
    }
}