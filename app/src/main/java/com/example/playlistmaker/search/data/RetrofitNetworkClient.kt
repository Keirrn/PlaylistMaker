package com.example.playlistmaker.search.data

import com.example.playlistmaker.utill.BASE_URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class RetrofitNetworkClient : NetworkClient {

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesApi = retrofit.create(ITunesApi::class.java)
    override suspend fun doRequest(dto: Any): Response {
        if (dto is TrackSearchRequest) {
            return withContext(Dispatchers.IO) {
                try {
                    val retrofitResponse = iTunesApi.searchSongs(dto.query)

                    retrofitResponse.apply { resultCode = 200 }

                } catch (e: IOException) {
                    Response().apply { resultCode = -1 }
                }
            }
        } else {
            return Response().apply { resultCode = 400 }
        }
    }
}