package com.example.playlistmaker.model

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Call

interface ITunesApi {
    @GET("/search")
    fun searchSongs(
        @Query("term") term: String,
        @Query("entity") entity: String = "song"
    ): Call<TrackResponse>
}
