package com.example.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import org.koin.android.ext.android.inject
import kotlin.getValue

class SettingsFragment : Fragment() {
    private val viewModel: SettingsViewModel by inject()
    private lateinit var binding: FragmentSettingsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(layoutInflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupButtons()
        setupThemeSwitcher()
    }



    private fun setupThemeSwitcher() {
        val switcher = binding.themeSwitcher

        viewModel.isDarkTheme.observe(viewLifecycleOwner) { isDark ->
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

    }


}