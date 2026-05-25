package com.example.playlistmaker.di

import com.example.playlistmaker.media.data.FavoriteTracksRepositoryImpl
import com.example.playlistmaker.media.data.PlaylistRepositoryImpl
import com.example.playlistmaker.media.data.TrackDbConvertor
import com.example.playlistmaker.media.domain.FavoriteTracksRepository
import com.example.playlistmaker.media.domain.PlaylistRepository
import com.example.playlistmaker.player.data.ImageLoadRepositoryImpl
import com.example.playlistmaker.player.domain.FormatMillisUseCase
import com.example.playlistmaker.player.domain.ImageLoadRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<ImageLoadRepository> { ImageLoadRepositoryImpl() }
    single { FormatMillisUseCase() }
    single<FavoriteTracksRepository> {
        FavoriteTracksRepositoryImpl(get(), get())
    }
    factory { TrackDbConvertor() }
    single<PlaylistRepository> {
        PlaylistRepositoryImpl(get(), get(),get())
    }
}
