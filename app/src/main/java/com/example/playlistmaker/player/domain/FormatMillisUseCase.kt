package com.example.playlistmaker.player.domain

class FormatMillisUseCase {
    operator fun invoke(milliseconds: Long): String {
        val minutes = milliseconds / 1000 / 60
        val seconds = milliseconds / 1000 % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}