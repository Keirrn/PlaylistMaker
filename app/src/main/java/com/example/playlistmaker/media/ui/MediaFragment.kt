package com.example.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.player.domain.ImageLoadRepository
import com.example.playlistmaker.player.ui.AudioPlayerFragment
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaFragment : Fragment() {

    private val favoritesViewModel: FavoritesViewModel by viewModel()
    private val playlistsViewModel: PlaylistsViewModel by viewModel()

    private val imageLoader: ImageLoadRepository by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return ComposeView(requireContext()).apply {

            setContent {

                val favoritesState by favoritesViewModel
                    .observeState()
                    .observeAsState(FavoritesState.Empty)

                val playlists by playlistsViewModel
                    .playlists
                    .observeAsState(emptyList())

                MediaScreen(
                    favoritesState = favoritesState,
                    playlists = playlists,
                    imageLoader = imageLoader,

                    onTrackClick = {
                        favoritesViewModel.onTrackClicked(it)
                    },

                    onPlaylistClick = {
                        findNavController().navigate(
                            R.id.action_mediaFragment_to_playlistInfoFragment,
                            bundleOf(
                                "playlistId" to it.playlistId
                            )
                        )
                    },

                    onCreatePlaylistClick = {
                        playlistsViewModel.onNewPlaylistClicked()
                    }
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistsViewModel.loadPlaylists()

        favoritesViewModel.openPlayerEvent.observe(viewLifecycleOwner) {

            findNavController().navigate(
                R.id.action_mediaFragment_to_audioPlayerFragment,
                AudioPlayerFragment.createArgs(it)
            )
        }

        playlistsViewModel.navigateToPlaylistCreator.observe(viewLifecycleOwner) {

            findNavController().navigate(
                R.id.action_mediaFragment_to_playlistCreatorFragment
            )
        }
    }

    override fun onResume() {
        super.onResume()
        playlistsViewModel.loadPlaylists()
    }
}