package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.domain.SearchResult
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.domain.TrackRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TrackRepositoryImpl(
    private val networkClient: NetworkClient
) : TrackRepository {
    override fun searchTracks(query: String): SearchResult {
        val response = networkClient.doRequest(TrackSearchRequest(query))

        return when (response.resultCode) {
            200 -> {
                val tracks = (response as TrackResponse).results.map {
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
                SearchResult(tracks, null)
            }

            -1 -> {
                SearchResult(
                    emptyList(),
                    "Проблемы со связью\n\nЗагрузка не удалась. Проверьте подключение к интернету"
                )
            }

            else -> {
                SearchResult(emptyList(), "Что-то пошло не так")
            }
        }
    }

    private fun formatMillis(millis: Long): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(Date(millis))
    }
}