package com.example.playlistmaker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class SearchActivity : AppCompatActivity() {

    private lateinit var inputEditText: EditText
    private lateinit var clearButton: ImageButton
    private lateinit var backButton: ImageButton

    private var searchText: String = ""

    companion object {
        const val KEY_SEARCH_TEXT = "saved_search_text"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        inputEditText = findViewById(R.id.search_bar)
        clearButton = findViewById(R.id.clear_button)
        backButton = findViewById(R.id.backButton)

        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(KEY_SEARCH_TEXT, "")
            inputEditText.setText(searchText)
        }

        clearButton.visibility = clearButtonVisibility(searchText)
        clearButton.setOnClickListener {
            inputEditText.setText("")
            searchText = ""
            clearButton.visibility = View.GONE
            hideKeyboard()
        }

        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchText = s?.toString() ?: ""
                clearButton.visibility = clearButtonVisibility(s)
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        inputEditText.addTextChangedListener(textWatcher)
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_SEARCH_TEXT, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString(KEY_SEARCH_TEXT, "")
        inputEditText.setText(searchText)
        clearButton.visibility = clearButtonVisibility(searchText)
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }
    private fun hideKeyboard() {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus ?: View(this)
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
