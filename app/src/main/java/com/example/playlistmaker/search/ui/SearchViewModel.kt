package com.example.playlistmaker.search.ui

import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.player.domain.ImageLoadRepository
import com.example.playlistmaker.search.domain.HistoryManagerRepository
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.domain.TrackInteractor

class SearchViewModel(
    private val trackInteractor: TrackInteractor,
    private val historyRepository: HistoryManagerRepository,
    private val imageLoader: ImageLoadRepository
) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
    fun loadImage(url: String, imageView: ImageView) {
        imageLoader.loadImage(url, imageView, 8f)
    }
    private val handler = Handler(Looper.getMainLooper())
    private var currentQuery = ""
    private val searchRunnable = Runnable { searchTracks(currentQuery) }
    private var isClickAllowed = true

    private val _searchState = MutableLiveData<SearchState>(SearchState.Empty)
    val searchState: LiveData<SearchState> = _searchState

    private val _historyState = MutableLiveData<List<Track>>()
    val historyState: LiveData<List<Track>> = _historyState

    private val _clearButtonVisible = MutableLiveData<Boolean>(false)
    val clearButtonVisible: LiveData<Boolean> = _clearButtonVisible

    fun onTextChanged(text: String) {
        currentQuery = text
        _clearButtonVisible.postValue( text.isNotEmpty())

        if (text.isEmpty()) {
            showHistory()
            handler.removeCallbacks(searchRunnable)
            _searchState.postValue(SearchState.Empty)
        } else {
            searchDebounce()
        }
    }

    fun clearSearch() {
        currentQuery = ""
        _clearButtonVisible.postValue(false)
        showHistory()
        _searchState.postValue(SearchState.Empty)
    }

    fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun searchTracks(query: String) {
        if (query.isEmpty()) return

        _searchState.postValue(SearchState.Loading)

        trackInteractor.searchTrack(query, object : TrackInteractor.TrackConsumer {
            override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                when {
                    errorMessage != null && errorMessage != "Ничего не нашлось" -> {
                        _searchState.postValue( SearchState.Error(errorMessage, true))
                    }
                    foundTracks.isNullOrEmpty() -> {
                        _searchState.postValue( SearchState.Error("Ничего не нашлось", false))
                    }
                    else -> {
                        _searchState.postValue(SearchState.Content(foundTracks))
                    }
                }
            }
        })
    }

    fun onTrackClicked(track: Track): Boolean {
        return if (clickDebounce()) {
            historyRepository.addTrackToHistory(track)
            showHistory()
            true
        } else {
            false
        }
    }

    fun refreshSearch() {
        if (currentQuery.isNotEmpty()) {
            searchTracks(currentQuery)
        }
    }

    fun clearHistory() {
        historyRepository.clearHistory()
        _historyState.postValue(emptyList())
        if (currentQuery.isEmpty()) {
            _searchState.postValue(SearchState.Empty)
        }
    }

    private fun showHistory() {
        val history = historyRepository.getHistory()
        _historyState.postValue(history)
        if (currentQuery.isEmpty() && history.isNotEmpty()) {
            _searchState.postValue(SearchState.History(history))
        }
    }

    init {
        showHistory()
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(searchRunnable)
    }
}