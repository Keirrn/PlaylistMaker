package com.example.playlistmaker.player.ui

import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ItemPlaylistMiniBinding
import com.example.playlistmaker.media.domain.Playlist
import com.example.playlistmaker.player.domain.ImageLoadRepository

class BottomSheetPlaylistViewHolder(
    private val binding: ItemPlaylistMiniBinding
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
                2f
            )

        } else {

            binding.coverImage.setImageResource(R.drawable.placeholder)
        }
    }
}