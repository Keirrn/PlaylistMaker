package com.example.playlistmaker.search.data

import android.content.SharedPreferences
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.domain.HistoryManagerRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HistoryManagerImpl(private val sharedPrefs: SharedPreferences) : HistoryManagerRepository {
    private val gson = Gson()
    private val historyKey = "SEARCH_HISTORY_KEY"
    private val maxSize = 10

    override fun getHistory(): List<Track> {
        val json = sharedPrefs.getString(historyKey, null) ?: return emptyList()
        val type = object : TypeToken<ArrayList<Track>>() {}.type
        return gson.fromJson(json, type)
    }

    private fun saveHistory(history: List<Track>) {
        val json = gson.toJson(history)
        sharedPrefs.edit().putString(historyKey, json)
            .apply()
    }

    override fun addTrackToHistory(track: Track) {
        val history = getHistory().toMutableList()
        history.removeAll { it.trackId == track.trackId }
        history.add(0, track)
        if (history.size > maxSize) {
            history.removeAt(
                history.lastIndex
            )
        }
        saveHistory(history)
    }

    override fun clearHistory() {
        sharedPrefs.edit().remove(historyKey).apply()
    }
}