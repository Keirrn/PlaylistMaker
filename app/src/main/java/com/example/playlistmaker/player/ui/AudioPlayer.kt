package com.example.playlistmaker.player.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.domain.FormatMillisUseCase
import com.example.playlistmaker.search.domain.ImageLoadRepository
import com.google.android.material.appbar.MaterialToolbar

class AudioPlayer : AppCompatActivity() {

    private lateinit var timerSong: TextView
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var playButton: ImageButton
    private lateinit var mainHandler: Handler
    private lateinit var url: String
    private lateinit var formatTimeUseCase: FormatMillisUseCase
    private lateinit var imageLoader: ImageLoadRepository

    private var playerState = STATE_DEFAULT
    private val updateTimerRunnable = object : Runnable {
        override fun run() {
            if (playerState == STATE_PLAYING) {
                timerSong.text = formatTimeUseCase(mediaPlayer.currentPosition.toLong())
                mainHandler.postDelayed(this, DELAY)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_audio_player)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        formatTimeUseCase = Creator.provideFormatTimeUseCase()
        imageLoader = Creator.provideImageLoader()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                top = systemBars.top,
                bottom = systemBars.bottom
            )
            insets
        }

        val track = intent.getParcelableExtra<Track>("track")


        val backBar = findViewById<MaterialToolbar>(R.id.backbar)
        backBar.setNavigationOnClickListener {
            finish()
        }
        url = track!!.previewUrl
        timerSong = findViewById(R.id.timer_song)
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
        year.text = track.releaseDate?.substring(0, 4) ?: ""
        genre.text = track.primaryGenreName
        country.text = track.country
        duration.text = track.trackTime
        if (track.collectionName == null) {
            collection.visibility = View.GONE
            collection_nc.visibility = View.GONE
        } else {
            collection.visibility = View.VISIBLE
            collection_nc.visibility = View.VISIBLE
        }
        if (track.releaseDate == null) {
            year.visibility = View.GONE
            year_nc.visibility = View.GONE
        } else {
            year.visibility = View.VISIBLE
            year_nc.visibility = View.VISIBLE
        }
        imageLoader.loadImage(
            track.artworkUrl100.replaceAfterLast("/", "512x512bb.jpg"),
            cover,
            8f
        )

        mediaPlayer = MediaPlayer()
        playButton = findViewById(R.id.play_btn)
        playButton.isEnabled = false
        preparePlayer()
        playButton.setOnClickListener {
            playbackControl()
        }

        mainHandler = Handler(Looper.getMainLooper())

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
            mainHandler.removeCallbacks(updateTimerRunnable)
            timerSong.text = DEFAULT_TIME
            playerState = STATE_PREPARED
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playButton.setImageResource(R.drawable.stop_song_ic)
        mainHandler.postDelayed(updateTimerRunnable, DELAY)
        playerState = STATE_PLAYING
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playButton.setImageResource(R.drawable.play_song_ic)
        mainHandler.removeCallbacks(updateTimerRunnable)
        playerState = STATE_PAUSED
    }

    private fun playbackControl() {
        when (playerState) {
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
        mainHandler.removeCallbacks(updateTimerRunnable)
        mediaPlayer.release()
    }


    companion object {
        const val DEFAULT_TIME = "00:00"
        private const val DELAY = 250L
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }
}