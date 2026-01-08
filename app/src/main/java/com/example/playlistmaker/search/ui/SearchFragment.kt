package com.example.playlistmaker.search.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.search.domain.Track
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private val viewModel: SearchViewModel by viewModel()

    private lateinit var songAdapter: SongAdapter
    private lateinit var historyAdapter: SongAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.clearButton.isVisible = false
        setupRecyclerViews()
        setupListeners()
        setupObservers()

    }

    private fun setupRecyclerViews() {
        songAdapter = SongAdapter(
            onTrackClick = { track -> onTrackClicked(track) },
            onLoadImage = viewModel::loadImage
        )

        historyAdapter = SongAdapter(
            onTrackClick = { track -> onTrackClicked(track) },
            onLoadImage = viewModel::loadImage
        )
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = songAdapter
        }

        binding.historyRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }
    }

    private fun setupListeners() {
        binding.clearButton.setOnClickListener {
            binding.searchBar.setText("")
            viewModel.clearSearch()
        }

        binding.remove.setOnClickListener {
            viewModel.clearHistory()
        }

        binding.updateBtn.setOnClickListener {
            viewModel.refreshSearch()
        }

        binding.searchBar.doOnTextChanged { text, _, _, _ ->
            val query = text?.toString() ?: ""
            viewModel.onTextChanged(query)
        }
    }

    private fun setupObservers() {
        viewModel.searchState.observe(this) { state ->
            handleSearchState(state)
        }

        viewModel.historyState.observe(this) { history ->
            handleHistoryState(history)
        }

        viewModel.clearButtonVisible.observe(this) { isVisible ->
            binding.clearButton.isVisible = isVisible
        }
    }

    private fun handleSearchState(state: SearchState) {
        when (state) {
            is SearchState.Loading -> {
                showLoading()
            }

            is SearchState.Content -> {
                showContent(state.tracks)
            }

            is SearchState.Error -> {
                showError(state.errorMessage, state.isNetworkError)
            }

            is SearchState.History -> {
                binding.historyLayout.isVisible = true
                binding.recyclerView.isVisible = false
            }

            SearchState.Empty -> {
                showEmpty()
            }
        }
    }

    private fun handleHistoryState(history: List<Track>) {
        historyAdapter.updateTracks(history)
        val shouldShowHistory = history.isNotEmpty() &&
                binding.searchBar.text.isNullOrEmpty()

        binding.historyLayout.isVisible = shouldShowHistory

        if (shouldShowHistory) {
            binding.recyclerView.isVisible = false
            binding.placeholder.isVisible = false
            binding.placeholderText.isVisible = false
            binding.updateBtn.isVisible = false
        }
    }

    private fun showLoading() {
        binding.progressBarLayout.isVisible = true
        binding.recyclerView.isVisible = false
        binding.placeholder.isVisible = false
        binding.placeholderText.isVisible = false
        binding.updateBtn.isVisible = false
        binding.historyLayout.isVisible = false
    }

    private fun showContent(tracks: List<Track>) {
        binding.progressBarLayout.isVisible = false
        songAdapter.updateTracks(tracks)
        binding.recyclerView.isVisible = true
        binding.placeholder.isVisible = false
        binding.placeholderText.isVisible = false
        binding.updateBtn.isVisible = false
        binding.historyLayout.isVisible = false
    }

    private fun showError(errorMessage: String, isNetworkError: Boolean) {
        binding.progressBarLayout.isVisible = false
        binding.recyclerView.isVisible = false
        binding.placeholder.isVisible = true
        binding.placeholderText.isVisible = true
        binding.placeholderText.text = errorMessage

        if (isNetworkError) {
            binding.placeholder.setImageResource(R.drawable.nointernet)
            binding.updateBtn.isVisible = true
        } else {
            binding.placeholder.setImageResource(R.drawable.nofound)
            binding.updateBtn.isVisible = false
        }

        binding.historyLayout.isVisible = false
    }

    private fun showEmpty() {
        binding.progressBarLayout.isVisible = false
        binding.recyclerView.isVisible = false
        binding.placeholder.isVisible = false
        binding.placeholderText.isVisible = false
        binding.updateBtn.isVisible = false
    }

    private fun onTrackClicked(track: Track) {
        if (viewModel.onTrackClicked(track)) {
//            findNavController().navigate(
//                R.id.,
//                AudioPlayerFragment.createArgs(track)
//            )
        }
    }
}
