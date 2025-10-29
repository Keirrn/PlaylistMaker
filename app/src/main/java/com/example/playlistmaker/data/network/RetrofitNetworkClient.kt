package com.example.playlistmaker.data.network

import com.example.playlistmaker.BASE_URL
import com.example.playlistmaker.data.dto.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient: NetworkClient {

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

private val iTunesApi = retrofit.create(ITunesApi::class.java)
    override fun doRequest(dto: Any): Response {
        if (dto is TrackSearchRequest) {
            val response  = iTunesApi.searchSongs(dto.query).execute()

            val body = response.body() ?: Response()

            return body.apply { resultCode = response.code() }
        } else {
            return Response().apply { resultCode = 400 }
        }
    }
}