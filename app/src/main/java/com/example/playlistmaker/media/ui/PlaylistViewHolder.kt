package com.example.playlistmaker.media.ui

import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ItemPlaylistBinding
import com.example.playlistmaker.media.domain.Playlist
import com.example.playlistmaker.player.domain.ImageLoadRepository

class PlaylistViewHolder(
    private val binding: ItemPlaylistBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        playlist: Playlist,
        imageLoader: ImageLoadRepository
    ) {

        binding.playlistName.text = playlist.playlistName

        binding.tracksCount.text =
            "${playlist.tracksCount} треков"

        if (playlist.coverPath != null) {

            imageLoader.loadImage(
                playlist.coverPath,
                binding.coverImage,
                8f
            )

        } else {

            binding.coverImage.setImageResource(R.drawable.placeholder)
        }
    }
}