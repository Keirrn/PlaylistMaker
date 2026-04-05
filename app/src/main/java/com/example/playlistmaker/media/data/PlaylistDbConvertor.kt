package com.example.playlistmaker.media.data

import com.example.playlistmaker.media.data.db.PlaylistEntity
import com.example.playlistmaker.media.domain.Playlist
import com.google.gson.Gson

class PlaylistDbConvertor {
    private val gson = Gson()

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlist.playlistId,
            playlist.playlistName,
            playlist.playlistDescription,
            playlist.coverPath,
            gson.toJson(playlist.trackIds),
            playlist.tracksCount
        )
    }

    fun map(playlistEntity: PlaylistEntity): Playlist {
        return Playlist(
            playlistEntity.playlistId,
            playlistEntity.playlistName,
            playlistEntity.playlistDescription,
            playlistEntity.coverPath,
            gson.fromJson(playlistEntity.trackIds, Array<Int>::class.java).toList(),
            playlistEntity.tracksCount
        )
    }
}