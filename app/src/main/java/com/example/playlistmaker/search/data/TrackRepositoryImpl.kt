package com.example.playlistmaker.search.data

import com.example.playlistmaker.media.data.db.PlaylistMakerDatabase
import com.example.playlistmaker.search.domain.SearchResult
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.domain.TrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TrackRepositoryImpl(
    private val networkClient: NetworkClient,
) : TrackRepository {
    override fun searchTracks(query: String): Flow<SearchResult> = flow {
        val response = networkClient.doRequest(TrackSearchRequest(query))

        when (response.resultCode) {
            200 -> {
                val tracks = (response as TrackResponse).results.mapNotNull { dto ->
                    try {
                        Track(
                            trackName = dto.trackName,
                            artistName = dto.artistName,
                            trackTime = formatMillis(dto.trackTimeMillis),
                            trackTimeMillis = dto.trackTimeMillis,
                            artworkUrl100 = dto.artworkUrl100,
                            trackId = dto.trackId,
                            collectionName = dto.collectionName,
                            releaseDate = dto.releaseDate,
                            primaryGenreName = dto.primaryGenreName,
                            country = dto.country,
                            previewUrl = dto.previewUrl
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
                emit(SearchResult(tracks, null))
            }

            -1 -> {
                emit(
                    SearchResult(
                        emptyList(),
                        "Проблемы со связью\n\nЗагрузка не удалась. Проверьте подключение к интернету"
                    )
                )
            }

            else -> {
                emit(SearchResult(emptyList(), "Ничего не нашлось"))
            }
        }
    }

    private fun formatMillis(millis: Long): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(Date(millis))
    }
}