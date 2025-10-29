package com.example.playlistmaker.data.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApi {
    @GET("/search")
    fun searchSongs(
        @Query("term") term: String,
        @Query("entity") entity: String = "song"
    ): Call<TrackResponse>
}
