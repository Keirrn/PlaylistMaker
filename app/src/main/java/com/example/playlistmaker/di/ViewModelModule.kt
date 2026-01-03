package com.example.playlistmaker.di

import android.media.MediaPlayer
import com.example.playlistmaker.player.ui.AudioPlayerViewModel
import com.example.playlistmaker.search.ui.SearchViewModel
import com.example.playlistmaker.settings.ui.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import android.os.Handler
import android.os.Looper
import com.example.playlistmaker.media.ui.FavoritesViewModel
import com.example.playlistmaker.media.ui.PlaylistsViewModel
import org.koin.dsl.module

val viewModelModule = module{
    factory { MediaPlayer() }

    single { Handler(Looper.getMainLooper()) }
    viewModel{
        SearchViewModel(get(), get(),get())
    }
    viewModel{
        SettingsViewModel(get(), get())
    }
    viewModel {
            FavoritesViewModel()
    }
    viewModel {
            PlaylistsViewModel()
    }

    viewModel { (url: String) ->
        AudioPlayerViewModel(url, get(), get(),get())
    }
}