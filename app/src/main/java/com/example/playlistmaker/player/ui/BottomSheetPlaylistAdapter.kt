package com.example.playlistmaker.player.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.ItemPlaylistMiniBinding
import com.example.playlistmaker.media.domain.Playlist
import com.example.playlistmaker.player.domain.ImageLoadRepository

class BottomSheetPlaylistAdapter(
    private val imageLoader: ImageLoadRepository,private val onClick: (Playlist) -> Unit
) : RecyclerView.Adapter<BottomSheetPlaylistViewHolder>() {

    private val playlists = mutableListOf<Playlist>()

    fun updateData(newData: List<Playlist>) {
        playlists.clear()
        playlists.addAll(newData)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BottomSheetPlaylistViewHolder {

        val binding = ItemPlaylistMiniBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return BottomSheetPlaylistViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: BottomSheetPlaylistViewHolder,
        position: Int
    ) {
        holder.bind(playlists[position], imageLoader)
        holder.itemView.setOnClickListener {
            onClick(playlists[position])
        }
    }

    override fun getItemCount(): Int {
        return playlists.size
    }
}