package com.example.playlistmaker.media.data

import com.example.playlistmaker.media.data.db.PlaylistEntity
import com.example.playlistmaker.media.data.db.PlaylistTrackEntity
import com.example.playlistmaker.media.domain.Playlist
import com.example.playlistmaker.search.domain.Track
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
            gson.fromJson(playlistEntity.trackIds, Array<Long>::class.java).toList(),
            playlistEntity.tracksCount
        )
    }
    fun mapToPlaylistTrackEntity(track: Track): PlaylistTrackEntity {
        return PlaylistTrackEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTime,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
            System.currentTimeMillis()
        )
    }
    fun map(trackEntity: PlaylistTrackEntity): Track {
        return Track(
            trackEntity.trackName,
            trackEntity.artistName,
            trackEntity.trackTime,
            trackEntity.trackTimeMillis,
            trackEntity.artworkUrl100,
            trackEntity.trackId,
            trackEntity.collectionName,
            trackEntity.releaseDate,
            trackEntity.primaryGenreName,
            trackEntity.country,
            trackEntity.previewUrl
        )
    }
}