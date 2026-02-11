package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.databinding.FragmentAudioPlayerBinding
import com.example.playlistmaker.player.domain.ImageLoadRepository
import com.example.playlistmaker.search.domain.Track
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class AudioPlayerFragment : Fragment() {

    companion object {
        const val ARGS_TRACK = "track"

        fun createArgs(track: Track): Bundle =
            bundleOf(ARGS_TRACK to track)
    }

    private lateinit var binding: FragmentAudioPlayerBinding
    private val imageLoadRepository: ImageLoadRepository by inject()
    private val viewModel: AudioPlayerViewModel by viewModel {
        parametersOf(track.previewUrl)
    }

    private lateinit var track: Track
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAudioPlayerBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        track = requireArguments().getParcelable<Track>(ARGS_TRACK)!!
        binding.backbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.song.text = track.trackName
        binding.singer.text = track.artistName
        binding.collectionName.text = track.collectionName ?: ""
        binding.trackYear.text = track.releaseDate?.substring(0, 4) ?: ""
        binding.trackGenre.text = track.primaryGenreName
        binding.trackCountry.text = track.country
        binding.fullTime.text = track.trackTime

        if (track.collectionName == null) {
            binding.collectionName.visibility = View.GONE
            binding.collection.visibility = View.GONE
        } else {
            binding.collectionName.visibility = View.VISIBLE
            binding.collection.visibility = View.VISIBLE
        }

        if (track.releaseDate == null) {
            binding.trackYear.visibility = View.GONE
            binding.year.visibility = View.GONE
        } else {
            binding.trackYear.visibility = View.VISIBLE
            binding.year.visibility = View.VISIBLE
        }

        imageLoadRepository.loadImage(
            track.artworkUrl100.replaceAfterLast("/", "512x512bb.jpg"),
            binding.albumCover,
            8f
        )

        binding.playBtn.isEnabled = false

        viewModel.observePlayerState().observe(viewLifecycleOwner) { state ->
            updatePlayButtonState(state)
        }

        viewModel.observeProgressTime().observe(viewLifecycleOwner) { time ->
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