package com.example.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavoritesBinding
import com.example.playlistmaker.player.ui.AudioPlayerFragment
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.ui.SongAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {
    private val viewModel: FavoritesViewModel by viewModel()
    private lateinit var songAdapter: SongAdapter
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        viewModel.openPlayerEvent.observe(viewLifecycleOwner) { track ->
            findNavController().navigate(
                R.id.action_mediaFragment_to_audioPlayerFragment,
                AudioPlayerFragment.createArgs(track)
            )
        }
        viewModel.fillData()
        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            when (state) {

                is FavoritesState.Empty -> {
                    showPlaceholder()
                }

                is FavoritesState.Content -> {
                    showTracks(state.tracks)
                }

            }
        }
    }

    private fun setupAdapter() {
        songAdapter = SongAdapter(
            onTrackClick = { track -> viewModel.onTrackClicked(track) },
            onLoadImage = viewModel::loadImage
        )
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = songAdapter
        }
    }

    private fun showPlaceholder() {
        binding.placeholderLayout.isVisible = true
        binding.recyclerView.isVisible = false
    }

    private fun showTracks(trackList: List<Track>) {
        binding.placeholderLayout.isVisible = false
        binding.recyclerView.isVisible = true
        songAdapter.updateTracks(trackList)

    }

    companion object {
        fun newInstance() = FavoritesFragment()
    }
}

