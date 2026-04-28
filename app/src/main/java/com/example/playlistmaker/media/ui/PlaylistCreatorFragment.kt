package com.example.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.databinding.FragmentPlaylistCreatorBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistCreatorFragment : Fragment() {
    private val viewModel: PlaylistCreatorViewModel by viewModel()

    private var _binding: FragmentPlaylistCreatorBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistCreatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.playlistNameEditText.doAfterTextChanged {
            binding.createButton.isEnabled = !it.isNullOrBlank()
        }
    }
}