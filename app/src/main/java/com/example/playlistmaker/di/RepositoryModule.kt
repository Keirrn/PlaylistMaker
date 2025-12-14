package com.example.playlistmaker.di

import com.example.playlistmaker.player.data.ImageLoadRepositoryImpl
import com.example.playlistmaker.player.domain.FormatMillisUseCase
import com.example.playlistmaker.player.domain.ImageLoadRepository
import org.koin.dsl.module

val repositoryModule = module{
    single<ImageLoadRepository> { ImageLoadRepositoryImpl() }
    single { FormatMillisUseCase() }
}
