package com.example.playlistmaker

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Placeholder
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.model.ITunesApi
import com.example.playlistmaker.model.SearchHistory
import com.example.playlistmaker.model.Track
import com.example.playlistmaker.model.TrackResponse
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SearchActivity : AppCompatActivity() {

    private lateinit var searchHistory: SearchHistory
    private lateinit var inputEditText: EditText
    private lateinit var clearButton: ImageButton
    private lateinit var translationService: ITunesApi
    private lateinit var songAdapter: SongAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var placeholderImage: Drawable
    private lateinit var placeholder: ImageView
    private lateinit var placeholdertext: TextView
    private lateinit var updater: Button
    private var searchText: String = ""
    private lateinit var historyLayout: LinearLayout
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyAdapter: SongAdapter
    private lateinit var removeHistoryButton: Button


    companion object {
        const val KEY_SEARCH_TEXT = "saved_search_text"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                top = systemBars.top,
                bottom = systemBars.bottom
            )

            insets
        }

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        songAdapter = SongAdapter { track ->
            val intent = Intent(this, AudioPlayer::class.java)
            intent.putExtra("trackJson", Gson().toJson(track))
            startActivity(intent)
            searchHistory.addTrack(track)
            updateHistory()
        }
        recyclerView.adapter = songAdapter
        inputEditText = findViewById(R.id.search_bar)
        clearButton = findViewById(R.id.clear_button)
        placeholder = findViewById(R.id.placeholder)
        placeholdertext = findViewById(R.id.placeholder_text)
        updater = findViewById(R.id.update_btn)
        historyLayout = findViewById(R.id.history_layout)
        historyRecyclerView = findViewById(R.id.historyRecyclerView)
        removeHistoryButton = findViewById(R.id.remove)
        searchHistory = SearchHistory(
            getSharedPreferences(PLAYLISTMAKER_PREFERENCES, MODE_PRIVATE)
        )
        historyAdapter = SongAdapter { track ->
            val intent = Intent(this, AudioPlayer::class.java)
            intent.putExtra("trackJson", Gson().toJson(track))
            startActivity(intent)
            searchHistory.addTrack(track)
            updateHistory()
        }
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.adapter = historyAdapter

        updateHistory()

        removeHistoryButton.setOnClickListener {
            searchHistory.clear()
            updateHistory()
        }




        val translateBaseUrl = "https://itunes.apple.com"
        val retrofit = Retrofit.Builder()
            .baseUrl(translateBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        translationService = retrofit.create(ITunesApi::class.java)
        val backBar = findViewById<MaterialToolbar>(R.id.backbar)


        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(KEY_SEARCH_TEXT, "")
            inputEditText.setText(searchText)
        }

        updater.setOnClickListener {
            searchSongs(searchText)
        }

        clearButton.isVisible = !searchText.isNullOrEmpty()
        clearButton.setOnClickListener {
            inputEditText.setText("")
            searchText = ""
            clearButton.visibility = View.GONE
            recyclerView.visibility = View.GONE
            placeholder.visibility = View.GONE
            placeholdertext.visibility = View.GONE
            updater.visibility=View.GONE
            hideKeyboard()
        }
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchSongs(searchText)
                true
            }
            false
        }
        backBar.setNavigationOnClickListener  {
            finish()
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchText = s?.toString() ?: ""
                clearButton.isVisible = !s.isNullOrEmpty()
                updateHistory()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        inputEditText.addTextChangedListener(textWatcher)
    }

    private fun searchSongs(query: String) {
        translationService.searchSongs(query).enqueue(object : retrofit2.Callback<TrackResponse> {
            override fun onResponse(
                call: retrofit2.Call<TrackResponse>,
                response: retrofit2.Response<TrackResponse>
            ) {
                val tracks = response.body()?.results ?: emptyList()
                songAdapter.updateTracks(tracks)
                placeholderImage = ContextCompat.getDrawable(this@SearchActivity, R.drawable.nofound)!!
                placeholder.setImageDrawable(placeholderImage)
                placeholdertext.setText("Ничего не нашлось")
                updater.isVisible = false
                placeholder.isVisible = tracks.isEmpty()
                placeholdertext.isVisible = tracks.isEmpty()
                recyclerView.isVisible = tracks.isNotEmpty()
            }

            override fun onFailure(call: retrofit2.Call<TrackResponse>, t: Throwable) {
                placeholder.setImageDrawable(
                    ContextCompat.getDrawable(this@SearchActivity, R.drawable.nointernet)
                )
                placeholdertext.text = "Проблемы со связью\n\nЗагрузка не удалась. Проверьте подключение к интернету"
                placeholder.isVisible = true
                placeholdertext.isVisible = true
                updater.isVisible = true
                recyclerView.isVisible = false
            }
        })
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_SEARCH_TEXT, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString(KEY_SEARCH_TEXT, "")
        inputEditText.setText(searchText)
        clearButton.isVisible = !searchText.isNullOrEmpty()
    }


    private fun hideKeyboard() {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus ?: View(this)
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
    private fun updateHistory() {
        val history = searchHistory.getHistory()

        if (history.isNotEmpty() && inputEditText.text.isNullOrEmpty()) {
            historyLayout.visibility = View.VISIBLE
            historyAdapter.updateTracks(history)
            val itemCount = history.size
            val params = historyRecyclerView.layoutParams as LinearLayout.LayoutParams
            params.weight = itemCount.toFloat()
            historyRecyclerView.layoutParams = params
        } else {
            historyLayout.visibility = View.GONE
        }
    }

}
