package com.example.playlistmaker.media.domain

data class Playlist(
    val playlistId: Int,
    val playlistName: String,
    val playlistDescription: String?,
    val coverPath: String?,
    val trackIds: List<Int>,
    val tracksCount: Int
)