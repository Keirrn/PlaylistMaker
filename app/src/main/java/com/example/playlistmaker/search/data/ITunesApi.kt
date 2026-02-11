package com.example.playlistmaker.search.data

import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApi {
    @GET("/search")
    suspend fun searchSongs(
        @Query("term") term: String,
        @Query("entity") entity: String = "song"
    ): TrackResponse
}