package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class SongAdapter (
    private val tracks : List<Track>
): RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int {
        return tracks.size
    }


    class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val  track: TextView = itemView.findViewById(R.id.titleView)
        private val artist: TextView = itemView.findViewById(R.id.artistView)
        private val time: TextView = itemView.findViewById(R.id.timeView)
        private val artwork: ImageView = itemView.findViewById(R.id.cover)
        fun bind(track: Track) {
            this.track.text = track.trackName
            this.artist.text = track.artistName
            this.time.text = track.trackTime
            Glide.with(itemView.context)
                .load(track.artworkUrl100)
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .transform(RoundedCorners(2))
                .into(artwork)

        }
    }
}
