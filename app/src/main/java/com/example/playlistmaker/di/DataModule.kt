package com.example.playlistmaker.di

import android.content.Context
import androidx.room.Room
import com.example.playlistmaker.media.data.ImageRepositoryImpl
import com.example.playlistmaker.media.data.PlaylistDbConvertor
import com.example.playlistmaker.media.data.db.PlaylistMakerDatabase
import com.example.playlistmaker.media.domain.ImageRepository
import com.example.playlistmaker.player.data.ImageLoadRepositoryImpl
import com.example.playlistmaker.player.domain.ImageLoadRepository
import com.example.playlistmaker.search.data.HistoryManagerImpl
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.RetrofitNetworkClient
import com.example.playlistmaker.search.data.TrackRepositoryImpl
import com.example.playlistmaker.search.domain.HistoryManagerRepository
import com.example.playlistmaker.search.domain.TrackRepository
import com.example.playlistmaker.settings.data.ThemeRepositoryImpl
import com.example.playlistmaker.settings.data.ThemeSwitcherImpl
import com.example.playlistmaker.settings.domain.ThemeRepository
import com.example.playlistmaker.settings.domain.ThemeSwitcher
import com.example.playlistmaker.sharing.data.NavigationRepositoryImpl
import com.example.playlistmaker.sharing.domain.NavigationRepository
import com.example.playlistmaker.utill.PLAYLISTMAKER_PREFERENCES
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {

    single {
        androidContext()
            .getSharedPreferences(
                PLAYLISTMAKER_PREFERENCES,
                Context.MODE_PRIVATE
            )
    }

    single<NetworkClient> {
        RetrofitNetworkClient()
    }
    single<ImageLoadRepository> {
        ImageLoadRepositoryImpl()
    }
    single<HistoryManagerRepository> {
        HistoryManagerImpl(get())
    }
    factory { PlaylistDbConvertor() }
    single<ThemeRepository> {
        ThemeRepositoryImpl(
            sharedPrefs = androidContext().getSharedPreferences(
                "PLAYLISTMAKER_PREFERENCES",
                Context.MODE_PRIVATE
            )
        )
    }
    single<NavigationRepository> {
        NavigationRepositoryImpl(androidContext())
    }
    single<ThemeSwitcher> { ThemeSwitcherImpl() }
    single<TrackRepository> {
        TrackRepositoryImpl(get())
    }
    single<PlaylistMakerDatabase> {
        Room.databaseBuilder(
            androidContext(),
            PlaylistMakerDatabase::class.java,
            "playlist_maker_database.db"
        ).build()
    }
    single { get<PlaylistMakerDatabase>().trackDao() }
    single { get<PlaylistMakerDatabase>().playlistDao() }
    single { get<PlaylistMakerDatabase>().playlistTrackDao() }
    single<ImageRepository> { ImageRepositoryImpl(androidContext()) }
}