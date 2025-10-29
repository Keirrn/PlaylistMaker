package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.dto.Response
import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.domain.entites.Track

data class TrackResponse(
    val resultCount: Int,
    val results: List<TrackDto>
) : Response()
