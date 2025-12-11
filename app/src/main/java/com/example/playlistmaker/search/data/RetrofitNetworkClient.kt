package com.example.playlistmaker.search.data

import com.example.playlistmaker.creator.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class RetrofitNetworkClient: NetworkClient {

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

private val iTunesApi = retrofit.create(ITunesApi::class.java)
    override fun doRequest(dto: Any): Response {
        if (dto is TrackSearchRequest) {
            return try {
                val retrofitResponse = iTunesApi.searchSongs(dto.query).execute()
                val body = retrofitResponse.body() ?: Response()

                body.apply { resultCode = retrofitResponse.code() }

            } catch (e: IOException) {
                Response().apply { resultCode = -1 }
            }
        } else {
            return Response().apply { resultCode = 400 }
        }
    }
}