package com.example.playlistmaker.data.repositories


import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.data.network.TrackResponse
import com.example.playlistmaker.data.network.TrackSearchRequest
import com.example.playlistmaker.domain.entites.Track
import com.example.playlistmaker.domain.repositories.TrackRepository
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