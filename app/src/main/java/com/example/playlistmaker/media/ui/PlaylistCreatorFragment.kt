package com.example.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistCreatorBinding
import com.example.playlistmaker.player.domain.ImageLoadRepository
import com.example.playlistmaker.utill.ToastUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistCreatorFragment : Fragment() {

    private val viewModel: PlaylistCreatorViewModel by viewModel()
    private val imageLoader: ImageLoadRepository by inject()

    private var _binding: FragmentPlaylistCreatorBinding? = null
    private val binding get() = _binding!!

    private var editPlaylistId: Long = -1L

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let {
                viewModel.onImageSelected(it)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistCreatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        editPlaylistId =
            arguments?.getLong("playlistId", -1L) ?: -1L

        setupBackPressed()

        binding.backbar.setNavigationOnClickListener {
            closeScreen()
        }

        binding.pickerImage.setOnClickListener {
            pickMedia.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        }

        binding.playlistNameEditText.doAfterTextChanged {
            viewModel.onNameChanged(it.toString())
        }

        binding.descriptionEditText.doAfterTextChanged {
            viewModel.onDescriptionChanged(it.toString())
        }

        viewModel.buttonEnabled.observe(viewLifecycleOwner) {
            binding.createButton.isEnabled = it
        }

        viewModel.coverPath.observe(viewLifecycleOwner) { path ->
            path?.let {
                imageLoader.loadImage(
                    it,
                    binding.pickerImage,
                    8f
                )
            }
        }

        viewModel.editablePlaylist.observe(viewLifecycleOwner) { playlist ->

            binding.playlistNameEditText.setText(
                playlist.playlistName
            )

            binding.descriptionEditText.setText(
                playlist.playlistDescription
            )
        }

        if (editPlaylistId != -1L) {

            binding.backbar.title = "Редактировать"

            binding.createButton.text = "Сохранить"

            viewModel.loadPlaylistForEdit(editPlaylistId)
        }

        binding.createButton.setOnClickListener {

            viewModel.savePlaylist {

                if (editPlaylistId == -1L) {

                    ToastUtils.showPlaylistToast(
                        requireContext(),
                        "Плейлист ${viewModel.getPlaylistName()} создан"
                    )
                }

                findNavController().navigateUp()
            }
        }
    }

    private fun setupBackPressed() {

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {

                override fun handleOnBackPressed() {
                    closeScreen()
                }
            }
        )
    }

    private fun closeScreen() {

        if (editPlaylistId != -1L) {
            findNavController().navigateUp()
            return
        }

        if (viewModel.hasUnsavedData()) {

            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.finish_create_playlist))
                .setMessage(getString(R.string.warning_save))
                .setNeutralButton(getString(R.string.cancel), null)
                .setPositiveButton(getString(R.string.finish)) { _, _ ->
                    findNavController().navigateUp()
                }
                .show()

        } else {

            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}