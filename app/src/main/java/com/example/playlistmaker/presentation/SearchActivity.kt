package com.example.playlistmaker.presentation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.*
import com.example.playlistmaker.domain.repositories.HistoryManagerRepository
import com.example.playlistmaker.domain.interactors.TrackInteractor
import com.example.playlistmaker.domain.entites.Track
import com.google.android.material.appbar.MaterialToolbar

class SearchActivity : AppCompatActivity() {

    private lateinit var trackInteractor: TrackInteractor
    private lateinit var historyRepository: HistoryManagerRepository

    private lateinit var songAdapter: SongAdapter
    private lateinit var historyAdapter: SongAdapter

    private lateinit var inputEditText: EditText
    private lateinit var clearButton: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var historyLayout: LinearLayout
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var removeHistoryButton: Button
    private lateinit var placeholder: ImageView
    private lateinit var placeholderText: TextView
    private lateinit var updater: Button
    private lateinit var progressBar: View

    private val handler = Handler(Looper.getMainLooper())
    private var searchText = ""
    private val searchRunnable = Runnable { searchTracks(searchText) }

    private var isClickAllowed = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        trackInteractor = Creator.provideTrackInteractor()
        historyRepository = Creator.provideHistoryRepository(this)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
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
        updateHistory()
    }
    private fun initViews() {
        inputEditText = findViewById(R.id.search_bar)
        clearButton = findViewById(R.id.clear_button)
        clearButton.isVisible = false
        recyclerView = findViewById(R.id.recyclerView)
        historyLayout = findViewById(R.id.history_layout)
        historyRecyclerView = findViewById(R.id.historyRecyclerView)
        removeHistoryButton = findViewById(R.id.remove)
        placeholder = findViewById(R.id.placeholder)
        placeholderText = findViewById(R.id.placeholder_text)
        updater = findViewById(R.id.update_btn)
        progressBar = findViewById(R.id.progressBarLayout)

        val backBar = findViewById<MaterialToolbar>(R.id.backbar)
        backBar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerViews() {
        songAdapter = SongAdapter(
            onTrackClick = { track ->  onTrackClicked(track) },
            imageLoader = Creator.provideImageLoader()
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = songAdapter

        historyAdapter = SongAdapter(
            onTrackClick = { track ->  onTrackClicked(track) },
            imageLoader = Creator.provideImageLoader()
        )
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.adapter = historyAdapter
    }

    private fun setupListeners() {
        clearButton.setOnClickListener {
            inputEditText.setText("")
            searchText = ""
            clearButton.isVisible = false
            recyclerView.isVisible = false
            placeholder.isVisible = false
            placeholderText.isVisible = false
            updater.isVisible = false
            hideKeyboard()
            updateHistory()
        }

        removeHistoryButton.setOnClickListener {
            historyRepository.clearHistory()
            updateHistory()
        }

        updater.setOnClickListener {
            searchTracks(searchText)
        }

        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchText = s?.toString() ?: ""
                clearButton.isVisible = !s.isNullOrEmpty()
                searchDebounce()
                updateHistory()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun searchTracks(query: String) {
        if (query.isEmpty()) return
        progressBar.isVisible = true

        trackInteractor.searchTrack(query, object : TrackInteractor.TrackConsumer {
            override fun consume(foundTracks: List<Track>) {
                runOnUiThread {
                    progressBar.isVisible = false
                    if (foundTracks.isNotEmpty()) {
                        songAdapter.updateTracks(foundTracks)
                        recyclerView.isVisible = true
                        placeholder.isVisible = false
                        placeholderText.isVisible = false
                        updater.isVisible = false
                    } else {
                        recyclerView.isVisible = false
                        placeholder.isVisible = true
                        placeholderText.isVisible = true
                        placeholderText.text = "Ничего не нашлось"
                        updater.isVisible = false
                    }
                }
            }
        })
    }

    private fun onTrackClicked(track: Track) {
        if (clickDebounce()) {
            historyRepository.addTrackToHistory(track)
            updateHistory()

            val intent = Intent(this, AudioPlayer::class.java)
            intent.putExtra("track", track)
            startActivity(intent)
        }
    }

    private fun updateHistory() {
        val history = historyRepository.getHistory()
        if (history.isNotEmpty() && inputEditText.text.isNullOrEmpty()) {
            historyLayout.isVisible = true
            historyAdapter.updateTracks(history)
        } else {
            historyLayout.isVisible = false
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus ?: View(this)
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}
