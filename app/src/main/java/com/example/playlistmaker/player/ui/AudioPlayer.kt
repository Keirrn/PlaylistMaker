package com.example.playlistmaker.player.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.playlistmaker.utill.TRACK
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.player.domain.FormatMillisUseCase
import com.example.playlistmaker.player.domain.ImageLoadRepository
import com.example.playlistmaker.search.domain.Track
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class AudioPlayer : AppCompatActivity() {

    private lateinit var binding: ActivityAudioPlayerBinding
    private lateinit var imageLoader: ImageLoadRepository
    private val imageLoadRepository : ImageLoadRepository by inject ()
    private val viewModel: AudioPlayerViewModel by viewModel{
        parametersOf( track?.previewUrl ?: "")
    }

    private lateinit var track: Track

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        imageLoader = imageLoadRepository

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                top = systemBars.top,
                bottom = systemBars.bottom
            )
            insets
        }

        track = intent.getParcelableExtra<Track>(TRACK)!!
        binding.backbar.setNavigationOnClickListener {
            finish()
        }

        binding.song.text = track.trackName
        binding.singer.text = track.artistName
        binding.collectionName.text = track.collectionName ?: ""
        binding.trackYear.text = track.releaseDate?.substring(0, 4) ?: ""
        binding.trackGenre.text = track.primaryGenreName
        binding.trackCountry.text = track.country
        binding.fullTime.text = track.trackTime

        if (track.collectionName == null) {
            binding.collectionName.visibility = android.view.View.GONE
            binding.collection.visibility = android.view.View.GONE
        } else {
            binding.collectionName.visibility = android.view.View.VISIBLE
            binding.collection.visibility = android.view.View.VISIBLE
        }

        if (track.releaseDate == null) {
            binding.trackYear.visibility = android.view.View.GONE
            binding.year.visibility = android.view.View.GONE
        } else {
            binding.trackYear.visibility = android.view.View.VISIBLE
            binding.year.visibility = android.view.View.VISIBLE
        }

        imageLoader.loadImage(
            track.artworkUrl100.replaceAfterLast("/", "512x512bb.jpg"),
            binding.albumCover,
            8f
        )

        binding.playBtn.isEnabled = false

        viewModel.observePlayerState().observe(this) { state ->
            updatePlayButtonState(state)
        }

        viewModel.observeProgressTime().observe(this) { time ->
            binding.timerSong.text = time
        }

        binding.playBtn.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    private fun updatePlayButtonState(state: Int) {
        when (state) {
            AudioPlayerViewModel.STATE_DEFAULT -> {
                binding.playBtn.isEnabled = false
                binding.playBtn.setImageResource(com.example.playlistmaker.R.drawable.play_song_ic)
            }
            AudioPlayerViewModel.STATE_PREPARED -> {
                binding.playBtn.isEnabled = true
                binding.playBtn.setImageResource(com.example.playlistmaker.R.drawable.play_song_ic)
            }
            AudioPlayerViewModel.STATE_PLAYING -> {
                binding.playBtn.isEnabled = true
                binding.playBtn.setImageResource(com.example.playlistmaker.R.drawable.stop_song_ic)
            }
            AudioPlayerViewModel.STATE_PAUSED -> {
                binding.playBtn.isEnabled = true
                binding.playBtn.setImageResource(com.example.playlistmaker.R.drawable.play_song_ic)
            }
        }
    }


}