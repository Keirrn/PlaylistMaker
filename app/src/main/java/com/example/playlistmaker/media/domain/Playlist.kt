package com.example.playlistmaker.media.domain

data class Playlist(
    val playlistId: Long,
    val playlistName: String,
    val playlistDescription: String?,
    val coverPath: String?,
    val trackIds: List<Long>,
    val tracksCount: Int
)