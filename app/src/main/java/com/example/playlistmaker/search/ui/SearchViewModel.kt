package com.example.playlistmaker.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.HistoryManagerRepository
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.domain.TrackInteractor
import com.example.playlistmaker.utill.SingleLiveEvent
import com.example.playlistmaker.utill.debounce
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val trackInteractor: TrackInteractor,
    private val historyRepository: HistoryManagerRepository,
) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }



    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchState = MutableLiveData<SearchState>(SearchState.Empty)
    val searchState: LiveData<SearchState> = _searchState

    private val _historyState = MutableLiveData<List<Track>>()
    val historyState: LiveData<List<Track>> = _historyState

    private val _clearButtonVisible = MutableLiveData<Boolean>(false)
    val clearButtonVisible: LiveData<Boolean> = _clearButtonVisible

    private val _openPlayerEvent = SingleLiveEvent<Track>()
    val openPlayerEvent: LiveData<Track> = _openPlayerEvent

    private val clickDebounce = debounce<Track>(
        delayMillis = CLICK_DEBOUNCE_DELAY,
        coroutineScope = viewModelScope,
        useLastParam = false
    ) { track ->
        historyRepository.addTrackToHistory(track)
        _openPlayerEvent.postValue(track)
    }

    private val searchDebounce = debounce<String>(
        delayMillis = SEARCH_DEBOUNCE_DELAY,
        coroutineScope = viewModelScope,
        useLastParam = true
    ) { query ->
        searchTracks(query)
    }

    fun onTrackClicked(track: Track) {
        clickDebounce(track)
    }

    fun onTextChanged(text: String) {
        _searchQuery.value = text
        _clearButtonVisible.postValue(text.isNotEmpty())

        if (text.isEmpty()) {
            showHistory()
            _searchState.postValue(SearchState.Empty)
        } else {
            searchDebounce(text)
        }
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _clearButtonVisible.postValue(false)
        showHistory()
        _searchState.postValue(SearchState.Empty)
    }

    private fun searchTracks(query: String) {
        if (query.isEmpty()) return

        _searchState.postValue(SearchState.Loading)

        viewModelScope.launch {
            trackInteractor.searchTrack(query)
                .collect { (foundTracks, errorMessage) ->
                    when {
                        errorMessage != null && errorMessage != "Ничего не нашлось" -> {
                            _searchState.postValue(SearchState.Error(errorMessage, true))
                        }

                        foundTracks.isNullOrEmpty() -> {
                            _searchState.postValue(SearchState.Error("Ничего не нашлось", false))
                        }

                        else -> {
                            _searchState.postValue(SearchState.Content(foundTracks))
                        }
                    }
                }
        }
    }

    fun refreshSearch() {
        val currentQuery = _searchQuery.value
        if (currentQuery.isNotEmpty()) {
            searchTracks(currentQuery)
        }
    }

    fun clearHistory() {
        historyRepository.clearHistory()
        _historyState.postValue(emptyList())
        if (_searchQuery.value.isEmpty()) {
            _searchState.postValue(SearchState.Empty)
        }
    }

    private fun showHistory() {
        val history = historyRepository.getHistory()
        _historyState.postValue(history)
        if (_searchQuery.value.isEmpty() && history.isNotEmpty()) {
            _searchState.postValue(SearchState.History(history))
        }
    }

    init {
        showHistory()
    }
}