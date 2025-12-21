package com.example.playlistmaker.search.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.player.domain.ImageLoadRepository
import com.example.playlistmaker.search.domain.Track

class SongAdapter(
    private val onTrackClick: (Track) -> Unit,
    private val onLoadImage: (String, ImageView) -> Unit
) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    private var tracks = ArrayList<Track>()

    fun updateTracks(newTracks: List<Track>) {
        tracks.clear()
        tracks.addAll(newTracks)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track,  onLoadImage)
        holder.itemView.setOnClickListener { onTrackClick(track) }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }


    class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val track: TextView = itemView.findViewById(R.id.titleView)
        private val artist: TextView = itemView.findViewById(R.id.artistView)
        private val time: TextView = itemView.findViewById(R.id.timeView)
        private val artwork: ImageView = itemView.findViewById(R.id.cover)
        fun bind(
            track: Track,
            onLoadImage: (String, ImageView) -> Unit
        ) {
            this.track.text = track.trackName
            this.artist.text = track.artistName
            this.time.text = track.trackTime

            onLoadImage(track.artworkUrl100, artwork)
        }

    }

}