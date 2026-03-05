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
    private val database: PlaylistMakerDatabase
) : TrackRepository {
    override fun searchTracks(query: String): Flow<SearchResult> = flow {
        val response = networkClient.doRequest(TrackSearchRequest(query))

        when (response.resultCode) {
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
                val favoriteIds = try {
                    database.trackDao().getTracksId()
                } catch (e: Exception) {
                    emptyList()
                }
                tracks.forEach { track ->
                    track.isFavorite = favoriteIds.contains(track.trackId)
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