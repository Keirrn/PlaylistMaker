package com.example.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.player.domain.ImageLoadRepository
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private val viewModel: PlaylistsViewModel by viewModel()

    private val imageLoader: ImageLoadRepository by inject()

    private lateinit var adapter: PlaylistAdapter

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PlaylistAdapter(imageLoader)

        binding.playlistsRecyclerView.layoutManager =
            GridLayoutManager(requireContext(), 2)

        binding.playlistsRecyclerView.adapter = adapter

        binding.newPlaylistButton.setOnClickListener {
            viewModel.onNewPlaylistClicked()
        }

        viewModel.navigateToPlaylistCreator.observe(viewLifecycleOwner) {

            findNavController().navigate(
                R.id.action_mediaFragment_to_playlistCreatorFragment
            )
        }

        viewModel.playlists.observe(viewLifecycleOwner) {

            adapter.updateData(it)

            if (it.isEmpty()) {

                binding.placeholderLayout.visibility = View.VISIBLE
                binding.playlistsRecyclerView.visibility = View.GONE

            } else {

                binding.placeholderLayout.visibility = View.GONE
                binding.playlistsRecyclerView.visibility = View.VISIBLE
            }
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.loadPlaylists()
    }

    companion object {

        fun newInstance() = PlaylistsFragment()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}