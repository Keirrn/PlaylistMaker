package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.data.TrackResponse
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