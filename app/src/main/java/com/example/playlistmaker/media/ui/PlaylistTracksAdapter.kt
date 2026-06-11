package com.example.playlistmaker.media.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.ItemSongBinding
import com.example.playlistmaker.player.domain.ImageLoadRepository
import com.example.playlistmaker.search.domain.Track

class PlaylistTracksAdapter(
    private val imageLoader: ImageLoadRepository,
    private val onTrackClick: (Track) -> Unit,
    private val onTrackLongClick: (Track) -> Unit
) : RecyclerView.Adapter<PlaylistTracksViewHolder>() {

    private val tracks = mutableListOf<Track>()

    fun updateData(newTracks: List<Track>) {
        tracks.clear()
        tracks.addAll(newTracks)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistTracksViewHolder {

        val binding = ItemSongBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return PlaylistTracksViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: PlaylistTracksViewHolder,
        position: Int
    ) {

        val track = tracks[position]

        holder.bind(
            track,
            imageLoader
        )

        holder.itemView.setOnClickListener {
            onTrackClick(track)
        }

        holder.itemView.setOnLongClickListener {
            onTrackLongClick(track)
            true
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }
}