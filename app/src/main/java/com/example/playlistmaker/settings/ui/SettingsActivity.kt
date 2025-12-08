package com.example.playlistmaker.settings.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.SettingsViewModelFactory
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.settings.domain.ThemeInteractor
import com.example.playlistmaker.sharing.domain.NavigationRepository
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {
    companion object {
        private val instances = mutableListOf<SettingsActivity>()
        fun finishAll() {
            instances.forEach { it.finish() }
            instances.clear()
        }
    }

    private lateinit var viewModel: SettingsViewModel
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
        val factory = SettingsViewModelFactory(
            Creator.provideThemeInteractor(this),
            Creator.provideNavigationUseCase(this)
        )

        viewModel = ViewModelProvider(this, factory)[SettingsViewModel::class.java]

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
            SettingsActivity.finishAll()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        instances.remove(this)
    }
}