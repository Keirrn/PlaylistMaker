package com.example.playlistmaker.search.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.utill.TRACK
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.player.domain.ImageLoadRepository
import com.example.playlistmaker.player.ui.AudioPlayer
import com.example.playlistmaker.search.domain.Track
import com.google.android.material.appbar.MaterialToolbar
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private val viewModel: SearchViewModel by viewModel()
    private val imageLoader: ImageLoadRepository by inject()
    private lateinit var songAdapter: SongAdapter
    private lateinit var historyAdapter: SongAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                top = systemBars.top,
                bottom = systemBars.bottom
            )
            insets
        }



        initViews()
        setupRecyclerViews()
        setupListeners()
        setupObservers()
    }

    private fun initViews() {
        val backBar = findViewById<MaterialToolbar>(R.id.backbar)
        backBar.setNavigationOnClickListener { finish() }
        binding.clearButton.isVisible = false
    }

    private fun setupRecyclerViews() {
        songAdapter = SongAdapter(
            onTrackClick = { track -> onTrackClicked(track) },
            imageLoader = imageLoader
        )
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = songAdapter
        }

        historyAdapter = SongAdapter(
            onTrackClick = { track -> onTrackClicked(track) },
            imageLoader = imageLoader
        )
        binding.historyRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
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
            val intent = Intent(this, AudioPlayer::class.java)
            intent.putExtra(TRACK, track)
            startActivity(intent)
        }
    }
}
