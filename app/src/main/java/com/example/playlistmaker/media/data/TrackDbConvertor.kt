package com.example.playlistmaker.media.data

import com.example.playlistmaker.media.data.db.TrackEntity
import com.example.playlistmaker.search.domain.Track

class TrackDbConvertor {
    fun map(track: Track): TrackEntity {
        return TrackEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTime,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
            addedAt = System.currentTimeMillis()
        )
    }

    fun map(trackEntity: TrackEntity): Track {
        return Track(
            trackEntity.trackName,
            trackEntity.artistName,
            trackEntity.trackTime,
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