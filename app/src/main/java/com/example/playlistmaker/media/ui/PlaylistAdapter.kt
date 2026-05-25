package com.example.playlistmaker.media.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ItemPlaylistBinding
import com.example.playlistmaker.media.domain.Playlist
import com.example.playlistmaker.player.domain.ImageLoadRepository

class PlaylistAdapter(
    private val imageLoader: ImageLoadRepository
) : RecyclerView.Adapter<PlaylistViewHolder>() {

    private val playlists = mutableListOf<Playlist>()

    fun updateData(newData: List<Playlist>) {
        playlists.clear()
        playlists.addAll(newData)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {

        val binding = ItemPlaylistBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return PlaylistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position], imageLoader)
    }

    override fun getItemCount(): Int {
        return playlists.size
    }
}