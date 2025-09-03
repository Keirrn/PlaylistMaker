package com.example.playlistmaker


import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.model.Track
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson

class AudioPlayer : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_audio_player)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                top = systemBars.top,
                bottom = systemBars.bottom
            )

            insets
        }
        val track = if (savedInstanceState != null) {
            val trackJson = savedInstanceState.getString(TRACK_JSON_KEY)
            Gson().fromJson(trackJson, Track::class.java)
        } else {
            Gson().fromJson(intent.getStringExtra(TRACK_JSON_KEY), Track::class.java)
        }
        val backBar = findViewById<MaterialToolbar>(R.id.backbar)
        backBar.setNavigationOnClickListener {
            finish()
        }
        val cover = findViewById<ImageView>(R.id.album_cover)
        val song = findViewById<TextView>(R.id.song)
        val singer = findViewById<TextView>(R.id.singer)
        val collection = findViewById<TextView>(R.id.collection_name)
        val collection_nc = findViewById<TextView>(R.id.collection)
        val year_nc = findViewById<TextView>(R.id.year)
        val year = findViewById<TextView>(R.id.track_year)
        val genre = findViewById<TextView>(R.id.track_genre)
        val country = findViewById<TextView>(R.id.track_country)
        val duration = findViewById<TextView>(R.id.full_time)
        song.text = track.trackName
        singer.text = track.artistName
        collection.text = track.collectionName ?: ""
        year.text = track.releaseDate?.substring(0,4) ?: ""
        genre.text = track.primaryGenreName
        country.text = track.country

        duration.text = formatMillis(track.trackTimeMillis)
        if (track.collectionName == null){
            collection.visibility = View.GONE
            collection_nc.visibility = View.GONE
        }
        else{
            collection.visibility = View.VISIBLE
            collection_nc.visibility = View.VISIBLE
        }
        if (track.releaseDate == null){
            year.visibility = View.GONE
            year_nc.visibility = View.GONE
        }
        else{
            year.visibility = View.VISIBLE
            year_nc.visibility = View.VISIBLE
        }
        Glide.with(this)
            .load(track.artworkUrl100.replaceAfterLast("/", "512x512bb.jpg"))
            .placeholder(R.drawable.placeholder_audioplayer)
            .centerCrop()
            .transform(RoundedCorners(dpToPx(8f, this)))
            .into(cover)
    }
    companion object {
        const val TRACK_JSON_KEY = "trackJson"
    }

}