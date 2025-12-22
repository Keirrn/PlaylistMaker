package com.example.playlistmaker.di

import com.example.playlistmaker.search.domain.TrackInteractor
import com.example.playlistmaker.search.domain.TrackInteractorImpl
import com.example.playlistmaker.settings.data.ThemeSwitcherImpl
import com.example.playlistmaker.settings.domain.ThemeInteractor
import com.example.playlistmaker.settings.domain.ThemeSwitcher
import org.koin.dsl.module

val interactorModule= module {
    factory<TrackInteractor> { TrackInteractorImpl(get()) }
    single<ThemeSwitcher> { ThemeSwitcherImpl() }
    single<ThemeInteractor> {
        ThemeInteractor(
            themeRepository = get(),
            themeSwitcher = get()
        )
    }
}