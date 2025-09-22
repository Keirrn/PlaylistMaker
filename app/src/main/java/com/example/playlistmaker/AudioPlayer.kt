package com.example.playlistmaker


import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
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

    companion object {
        const val TRACK_JSON_KEY = "trackJson"
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    private var playerState = STATE_DEFAULT

    private lateinit var mediaPlayer : MediaPlayer
    private lateinit var playButton: ImageButton
    private lateinit var url: String

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
        url = track.previewUrl
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
        mediaPlayer= MediaPlayer()
        playButton = findViewById(R.id.play_btn)
        playButton.isEnabled = false
        preparePlayer()
        playButton.setOnClickListener {
            playbackControl()
        }
    }


    private fun preparePlayer() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playButton.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playButton.setImageResource(R.drawable.play_song_ic)
            playerState = STATE_PREPARED
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playButton.setImageResource(R.drawable.stop_song_ic)
        playerState = STATE_PLAYING
    }
    private fun pausePlayer() {
        mediaPlayer.pause()
        playButton.setImageResource(R.drawable.play_song_ic)
        playerState = STATE_PAUSED
    }

    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }


    override fun onPause() {
        super.onPause()
        pausePlayer()
    }
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}