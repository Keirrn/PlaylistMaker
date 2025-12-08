package com.example.playlistmaker.settings.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.settings.domain.ThemeInteractor
import com.example.playlistmaker.sharing.domain.NavigationRepository
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {
    private lateinit var themeInteractor: ThemeInteractor
    private lateinit var navigationUseCase: NavigationRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                top = systemBars.top,
                bottom = systemBars.bottom
            )

            insets
        }
        themeInteractor = Creator.provideThemeInteractor(this)
        navigationUseCase = Creator.provideNavigationUseCase(this)
        setupButtons()
        setupThemeSwitcher()
    }

    private fun setupThemeSwitcher() {
        val themeSwitcherView = findViewById<SwitchMaterial>(R.id.themeSwitcher)
        val isDarkTheme = themeInteractor.getTheme()
        themeSwitcherView.isChecked = isDarkTheme

        themeSwitcherView.setOnCheckedChangeListener { _, checked ->
            themeInteractor.saveAndApplyTheme(checked)
        }
    }

    private fun setupButtons() {
        val shareButton = findViewById<MaterialTextView>(R.id.shareButton)
        val agreementButton = findViewById<MaterialTextView>(R.id.agreementButton)
        val supportButton = findViewById<MaterialTextView>(R.id.supportButton)
        val backBar = findViewById<MaterialToolbar>(R.id.backbar)
        backBar.setNavigationOnClickListener {
            finish()
        }
        shareButton.setOnClickListener {
            navigationUseCase.shareApp()
        }

        agreementButton.setOnClickListener {
            navigationUseCase.openAgreement()
        }

        supportButton.setOnClickListener {
            navigationUseCase.contactSupport()
        }
    }
}