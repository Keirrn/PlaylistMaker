package com.example.playlistmaker

import android.content.Context
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.model.Track
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SongAdapter (
    private val onTrackClick: (Track) -> Unit
): RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    private var tracks= ArrayList<Track>()

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
        holder.bind(track)
        holder.itemView.setOnClickListener { onTrackClick(track) }
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
            val durationFormatted = formatMillis(track.trackTimeMillis)
            this.time.text = durationFormatted
            Glide.with(itemView.context)
                .load(track.artworkUrl100)
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .transform(RoundedCorners(dpToPx(2f, itemView.context)))
                .into(artwork)

        }


    }

}
