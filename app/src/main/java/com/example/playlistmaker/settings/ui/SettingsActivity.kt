package com.example.playlistmaker.settings.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import org.koin.android.ext.android.inject

class SettingsActivity : AppCompatActivity() {
    companion object {
        private val instances = mutableListOf<SettingsActivity>()

    }

    private val viewModel: SettingsViewModel by inject()
    private lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instances.add(this)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                top = systemBars.top,
                bottom = systemBars.bottom
            )

            insets
        }

        setupButtons()
        setupThemeSwitcher()
    }

    private fun setupThemeSwitcher() {
        val switcher = binding.themeSwitcher

        viewModel.isDarkTheme.observe(this) { isDark ->
            switcher.isChecked = isDark
        }

        switcher.setOnCheckedChangeListener { _, checked ->
            viewModel.onThemeSwitched(checked)
        }
    }


    private fun setupButtons() {
        binding.shareButton.setOnClickListener {
            viewModel.onShareClicked()
        }

        binding.agreementButton.setOnClickListener {
            viewModel.onAgreementClicked()
        }

        binding.supportButton.setOnClickListener {
            viewModel.onSupportClicked()
        }

        binding.backbar.setNavigationOnClickListener {
            finish()
        }
    }


}