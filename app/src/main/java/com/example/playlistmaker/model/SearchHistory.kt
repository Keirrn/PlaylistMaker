package com.example.playlistmaker.model

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val sharedPrefs: SharedPreferences) {

    private val gson = Gson()
    private val historyKey = "SEARCH_HISTORY_KEY"
    private val maxSize = 10

    fun getHistory(): List<Track> {
        val json = sharedPrefs.getString(historyKey, null) ?: return emptyList()
        val type = object : TypeToken<ArrayList<Track>>() {}.type
        return gson.fromJson(json, type)
    }

    private fun saveHistory(history: List<Track>) {
        val json = gson.toJson(history)
        sharedPrefs.edit().putString(historyKey, json).apply()
    }

    fun addTrack(track: Track) {
        val history = getHistory().toMutableList()
        history.removeAll { it.trackId == track.trackId }
        history.add(0, track)

        if (history.size > maxSize) {
            history.removeAt(history.lastIndex)
        }
        saveHistory(history)
    }

    fun clear() {
        sharedPrefs.edit().remove(historyKey).apply()
    }
}
