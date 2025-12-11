package com.example.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.search.data.HistoryManagerImpl
import com.example.playlistmaker.player.data.ImageLoadRepositoryImpl
import com.example.playlistmaker.search.data.RetrofitNetworkClient
import com.example.playlistmaker.search.data.TrackInteractorImpl
import com.example.playlistmaker.search.data.TrackRepositoryImpl
import com.example.playlistmaker.player.domain.FormatMillisUseCase
import com.example.playlistmaker.search.domain.HistoryManagerRepository
import com.example.playlistmaker.player.domain.ImageLoadRepository
import com.example.playlistmaker.search.domain.TrackInteractor
import com.example.playlistmaker.search.domain.TrackRepository
import com.example.playlistmaker.settings.data.ThemeRepositoryImpl
import com.example.playlistmaker.settings.data.ThemeSwitcherImpl
import com.example.playlistmaker.settings.domain.ThemeInteractor
import com.example.playlistmaker.settings.domain.ThemeRepository
import com.example.playlistmaker.sharing.data.NavigationRepositoryImpl
import com.example.playlistmaker.sharing.domain.NavigationRepository

object Creator {
    private fun getTrackRepository(): TrackRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient())
    }

    private fun getHistoryRepository(context: Context): HistoryManagerRepository {
        val sharedPrefs =
            context.getSharedPreferences(PLAYLISTMAKER_PREFERENCES, Context.MODE_PRIVATE)
        return HistoryManagerImpl(sharedPrefs)
    }

    fun provideTrackInteractor(): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository())
    }

    fun provideHistoryRepository(context: Context): HistoryManagerRepository {
        return getHistoryRepository(context)
    }

    fun provideImageLoader(): ImageLoadRepository {
        return ImageLoadRepositoryImpl()
    }

    fun provideThemeInteractor(context: Context): ThemeInteractor {
        return ThemeInteractor(
            themeRepository = getThemeRepository(context),
            themeSwitcher = ThemeSwitcherImpl()
        )
    }

    private fun getThemeRepository(context: Context): ThemeRepository {
        val sharedPrefs = context.getSharedPreferences(PLAYLISTMAKER_PREFERENCES, Context.MODE_PRIVATE)
        return ThemeRepositoryImpl(sharedPrefs)
    }

    fun provideNavigationUseCase(context: Context): NavigationRepository {
        return NavigationRepositoryImpl(context)
    }
    fun provideFormatTimeUseCase(): FormatMillisUseCase {
        return FormatMillisUseCase()
    }
}