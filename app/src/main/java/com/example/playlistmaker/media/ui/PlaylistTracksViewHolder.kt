package com.example.playlistmaker.media.ui

import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.ItemSongBinding
import com.example.playlistmaker.player.domain.ImageLoadRepository
import com.example.playlistmaker.search.domain.Track

class PlaylistTracksViewHolder(
    private val binding: ItemSongBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        track: Track,
        imageLoader: ImageLoadRepository
    ) {

        binding.titleView.text =
            track.trackName

        binding.artistView.text =
            track.artistName

        binding.timeView.text =
            track.trackTime

        imageLoader.loadImage(
            track.artworkUrl100,
            binding.cover,
            2f
        )
    }
}