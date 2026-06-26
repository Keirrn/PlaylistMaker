package com.example.playlistmaker.di

import android.os.Handler
import android.os.Looper
import com.example.playlistmaker.media.ui.FavoritesViewModel
import com.example.playlistmaker.media.ui.PlaylistCreatorViewModel
import com.example.playlistmaker.media.ui.PlaylistInfoViewModel
import com.example.playlistmaker.media.ui.PlaylistsViewModel
import com.example.playlistmaker.player.ui.AudioPlayerViewModel
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.ui.SearchViewModel
import com.example.playlistmaker.settings.ui.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    single { Handler(Looper.getMainLooper()) }
    viewModel {
        SearchViewModel(get(), get())
    }
    viewModel {
        SettingsViewModel(get(), get())
    }
    viewModel {
        FavoritesViewModel(get())
    }
    viewModel {
        PlaylistsViewModel(get())
    }
    viewModel {
        PlaylistCreatorViewModel(get(), get())
    }
    viewModel { (track: Track) ->
        AudioPlayerViewModel(track, get(), get(), get())
    }
    viewModel {
        PlaylistInfoViewModel(get())
    }
}