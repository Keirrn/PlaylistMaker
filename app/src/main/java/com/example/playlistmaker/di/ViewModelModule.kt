package com.example.playlistmaker.di

import com.example.playlistmaker.main.ui.MainViewModel
import com.example.playlistmaker.player.ui.AudioPlayerViewModel
import com.example.playlistmaker.search.ui.SearchViewModel
import com.example.playlistmaker.settings.ui.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module{

    viewModel{
        MainViewModel()
    }
    viewModel{
        SearchViewModel(get(), get())
    }
    viewModel{
        SettingsViewModel(get(), get())
    }
    viewModel { (url: String) ->
        AudioPlayerViewModel(url, get())
    }
}