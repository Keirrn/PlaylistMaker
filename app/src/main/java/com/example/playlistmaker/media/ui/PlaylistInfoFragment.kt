package com.example.playlistmaker.media.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistInfoBinding
import com.example.playlistmaker.databinding.ItemPlaylistMiniBinding
import com.example.playlistmaker.player.domain.ImageLoadRepository
import com.example.playlistmaker.player.ui.AudioPlayerFragment
import com.example.playlistmaker.search.domain.Track
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistInfoFragment : Fragment() {

    private var _binding: FragmentPlaylistInfoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistInfoViewModel by viewModel()

    private val imageLoader: ImageLoadRepository by inject()
    private lateinit var menuBottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var adapter: PlaylistTracksAdapter
    private var miniCardBinding: ItemPlaylistMiniBinding? = null

    private var playlistId: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        playlistId = requireArguments().getLong("playlistId")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPlaylistInfoBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = PlaylistTracksAdapter(
            imageLoader = imageLoader,

            onTrackClick = { track ->

                findNavController().navigate(
                    R.id.action_playlistInfoFragment_to_audioPlayerFragment,
                    AudioPlayerFragment.createArgs(track)
                )
            },

            onTrackLongClick = { track ->

                showDeleteDialog(track)
            }
        )
        binding.editPlaylistButton.setOnClickListener {

            menuBottomSheetBehavior.state =
                BottomSheetBehavior.STATE_HIDDEN

            findNavController().navigate(
                R.id.action_playlistInfoFragment_to_playlistCreatorFragment,
                Bundle().apply {
                    putLong("playlistId", playlistId)
                }
            )
        }
        miniCardBinding = binding.currentPlaylistCard

        binding.tracksRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())

        binding.tracksRecyclerView.adapter = adapter

        binding.backbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.deletePlaylistButton.setOnClickListener {

            menuBottomSheetBehavior.state =
                BottomSheetBehavior.STATE_HIDDEN

            showDeletePlaylistDialog()
        }
        binding.shareButton.setOnClickListener {
            sharePlaylist()
        }
        binding.sharePlaylistButton.setOnClickListener {

            menuBottomSheetBehavior.state =
                BottomSheetBehavior.STATE_HIDDEN

            sharePlaylist()
        }
        setupBottomSheets()

        viewModel.loadPlaylist(playlistId)

        observeViewModel()
    }
    private fun sharePlaylist() {

        val tracks = viewModel.tracks.value.orEmpty()

        if (tracks.isEmpty()) {

            Toast.makeText(
                requireContext(),
                "В этом плейлисте нет списка треков, которым можно поделиться",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(
                Intent.EXTRA_TEXT,
                viewModel.createShareText()
            )
        }

        startActivity(Intent.createChooser(intent, null))
    }
    private fun setupBottomSheets() {
        val tracksBottomSheetBehavior = BottomSheetBehavior.from(binding.tracksContainer)
        menuBottomSheetBehavior = BottomSheetBehavior.from(binding.playlistMenuBottomSheet)
        menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        binding.root.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val screenHeight = binding.root.height
                val buttonsBottom = binding.playlistActions.bottom

                if (screenHeight > 0) {
                    tracksBottomSheetBehavior.peekHeight = screenHeight - buttonsBottom
                    binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }
        })

        binding.moreButton.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        menuBottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(
                bottomSheet: View,
                newState: Int
            ) {
                _binding?.overlay?.visibility =
                    if (newState == BottomSheetBehavior.STATE_HIDDEN)
                        View.GONE
                    else
                        View.VISIBLE
            }

            override fun onSlide(
                bottomSheet: View,
                slideOffset: Float
            ) {
                _binding?.overlay?.alpha = slideOffset
            }
        })
    }

    private fun observeViewModel() {

        viewModel.playlist.observe(viewLifecycleOwner) { playlist ->

            binding.playlistName.text = playlist.playlistName
            binding.playlistDescription.text = playlist.playlistDescription
            binding.countTracks.text = "${playlist.tracksCount} треков"

            miniCardBinding?.playlistName?.text = playlist.playlistName
            miniCardBinding?.tracksCount?.text = "${playlist.tracksCount} треков"
            if (playlist.playlistDescription.isNullOrBlank()) {
                binding.playlistDescription.visibility = View.GONE
            } else {
                binding.playlistDescription.visibility = View.VISIBLE
                binding.playlistDescription.text = playlist.playlistDescription
            }
            if (!playlist.coverPath.isNullOrEmpty()) {

                imageLoader.loadImage(
                    playlist.coverPath,
                    miniCardBinding!!.coverImage,
                    2f
                )
            }

            viewModel.duration.observe(viewLifecycleOwner) {
                binding.playlistDuration.text = "$it мин"
            }

            if (!playlist.coverPath.isNullOrEmpty()) {

                imageLoader.loadImage(
                    playlist.coverPath,
                    binding.albumCover,
                    8f
                )

            } else {

                binding.albumCover.setImageResource(
                    R.drawable.placeholder_audioplayer
                )
            }
        }

        viewModel.tracks.observe(viewLifecycleOwner) {
            adapter.updateData(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun showDeletePlaylistDialog() {

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Удалить плейлист")
            .setMessage("Хотите удалить плейлист?")
            .setNegativeButton("Нет", null)
            .setPositiveButton("Да") { _, _ ->

                viewModel.deletePlaylist {

                    findNavController().navigateUp()
                }
            }
            .show()
    }
private fun showDeleteDialog(track: Track) {

        MaterialAlertDialogBuilder(requireContext())
            .setMessage("Хотите удалить трек?")
            .setNegativeButton("НЕТ", null)
            .setPositiveButton("ДА") { _, _ ->

                viewModel.deleteTrack(track)
            }
            .show()
    }

}