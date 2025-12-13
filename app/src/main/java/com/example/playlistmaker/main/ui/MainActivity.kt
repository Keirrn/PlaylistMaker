package com.example.playlistmaker.main.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityMainBinding
import com.example.playlistmaker.media.ui.MediaActivity
import com.example.playlistmaker.search.ui.SearchActivity
import com.example.playlistmaker.settings.ui.SettingsActivity

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                top = systemBars.top,
                bottom = systemBars.bottom
            )

            insets
        }
        viewModel.navigationEvent.observe(this) { event ->
            when (event) {
                is MainViewModel.NavigationEvent.OpenSearch ->
                    startActivity(Intent(this, SearchActivity::class.java))

                is MainViewModel.NavigationEvent.OpenMedia ->
                    startActivity(Intent(this, MediaActivity::class.java))

                is MainViewModel.NavigationEvent.OpenSettings ->
                    startActivity(Intent(this, SettingsActivity::class.java))
            }
        }

        binding.searchBtn.setOnClickListener {
            viewModel.onSearchClicked()
        }

        binding.mediaBtn.setOnClickListener {
            viewModel.onMediaClicked()
        }

        binding.settingsBtn.setOnClickListener {
            viewModel.onSettingsClicked()
        }
    }
}