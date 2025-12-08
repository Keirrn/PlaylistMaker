package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.TrackResponse
import com.example.playlistmaker.search.data.TrackSearchRequest
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.domain.TrackRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TrackRepositoryImpl(
    private val networkClient: NetworkClient
) : TrackRepository {
    override fun searchTracks(query: String): List<Track> {
        val response = networkClient.doRequest(TrackSearchRequest(query))
        if (response.resultCode == 200) {
             return (response as TrackResponse).results.map {
                 Track(
                     trackName = it.trackName,
                     artistName = it.artistName,
                     trackTime = formatMillis(it.trackTimeMillis),
                     artworkUrl100 = it.artworkUrl100,
                     trackId = it.trackId,
                     collectionName = it.collectionName,
                     releaseDate = it.releaseDate,
                     primaryGenreName = it.primaryGenreName,
                     country = it.country,
                     previewUrl = it.previewUrl
                 )
            }
        } else {
            return emptyList()
        }
    }

    private fun formatMillis(millis: Long): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(Date(millis))
    }
}