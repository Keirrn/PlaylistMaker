package com.example.playlistmaker

import android.content.Context
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.repositories.HistoryManagerImpl
import com.example.playlistmaker.data.repositories.ImageLoadRepositoryImpl
import com.example.playlistmaker.data.repositories.NavigationRepositoryImpl
import com.example.playlistmaker.data.repositories.ThemeRepositoryImpl
import com.example.playlistmaker.data.repositories.ThemeSwitcherImpl
import com.example.playlistmaker.data.repositories.TrackInteractorImpl
import com.example.playlistmaker.data.repositories.TrackRepositoryImpl
import com.example.playlistmaker.domain.interactors.FormatMillisUseCase
import com.example.playlistmaker.domain.interactors.ThemeInteractor
import com.example.playlistmaker.domain.interactors.TrackInteractor
import com.example.playlistmaker.domain.repositories.HistoryManagerRepository
import com.example.playlistmaker.domain.repositories.ImageLoadRepository
import com.example.playlistmaker.domain.repositories.NavigationRepository
import com.example.playlistmaker.domain.repositories.ThemeRepository
import com.example.playlistmaker.domain.repositories.TrackRepository

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