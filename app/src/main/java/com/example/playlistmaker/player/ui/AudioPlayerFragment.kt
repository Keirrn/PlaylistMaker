package com.example.playlistmaker.player.ui

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.databinding.FragmentAudioPlayerBinding
import com.example.playlistmaker.player.domain.ImageLoadRepository
import com.example.playlistmaker.search.domain.Track
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class AudioPlayerFragment : Fragment() {

    companion object {
        const val ARGS_TRACK = "track"

        fun createArgs(track: Track): Bundle =
            bundleOf(ARGS_TRACK to track)
    }

    private lateinit var binding: FragmentAudioPlayerBinding
    private val imageLoadRepository: ImageLoadRepository by inject()
    private val viewModel: AudioPlayerViewModel by viewModel {
        parametersOf(track)
    }
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private lateinit var adapter: BottomSheetPlaylistAdapter

    private val track: Track by lazy {
        requireArguments().getParcelable<Track>(ARGS_TRACK)!!
    }
    private var mService: MusicService? = null
    private var mBound: Boolean = false
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            bindMusicService()
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                showPermissionRationaleDialog()
            } else {
                showSettingsDialog()
            }
        }
    }

    private fun showPermissionRationaleDialog() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Уведомления плеера")
            .setMessage("Разрешение нужно для того, чтобы вы могли управлять музыкой через шторку уведомлений, когда приложение свернуто.")
            .setPositiveButton("Разрешить") { _, _ ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
            .setNegativeButton("Продолжить без шторки") { _, _ ->
                bindMusicService()
            }
            .show()
    }

    private fun showSettingsDialog() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Доступ к уведомлениям ограничен")
            .setMessage("Вы запретили уведомления. Чтобы плеер работал в фоновом режиме, пожалуйста, включите их вручную в настройках приложения.")
            .setPositiveButton("В настройки") { _, _ ->
                openAppSettings()
                bindMusicService()
            }
            .setNegativeButton("Отмена") { _, _ ->
                bindMusicService()
            }
            .show()
    }

    private fun openAppSettings() {
        val intent = android.content.Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            data = android.net.Uri.fromParts("package", requireContext().packageName, null)
        }
        requireContext().startActivity(intent)
    }
    private val connection = object : android.content.ServiceConnection {
        override fun onServiceConnected(className: android.content.ComponentName, service: android.os.IBinder) {
            val binder = service as MusicService.MusicBinder
            mService = binder.getService()
            mBound = true
            mService?.let { viewModel.onServiceConnected(it) }
        }

        override fun onServiceDisconnected(arg0: android.content.ComponentName) {
            mBound = false
            mService = null
            viewModel.onServiceDisconnected()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAudioPlayerBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.song.text = track.trackName
        binding.singer.text = track.artistName
        binding.collectionName.text = track.collectionName ?: ""
        binding.trackYear.text = track.releaseDate?.substring(0, 4) ?: ""
        binding.trackGenre.text = track.primaryGenreName
        binding.trackCountry.text = track.country
        binding.fullTime.text = track.trackTime

        if (track.collectionName == null) {
            binding.collectionName.visibility = View.GONE
            binding.collection.visibility = View.GONE
        } else {
            binding.collectionName.visibility = View.VISIBLE
            binding.collection.visibility = View.VISIBLE
        }

        if (track.releaseDate == null) {
            binding.trackYear.visibility = View.GONE
            binding.year.visibility = View.GONE
        } else {
            binding.trackYear.visibility = View.VISIBLE
            binding.year.visibility = View.VISIBLE
        }

        imageLoadRepository.loadImage(
            track.artworkUrl100.replaceAfterLast("/", "512x512bb.jpg"),
            binding.albumCover,
            8f
        )

        binding.playBtn.isEnabled = false

        viewModel.observePlayerState().observe(viewLifecycleOwner) { state ->
            updatePlayButtonState(state)
        }

        viewModel.observeProgressTime().observe(viewLifecycleOwner) { time ->
            binding.timerSong.text = time
        }
        viewModel.observeIsFavorite().observe(viewLifecycleOwner){ isFavorite ->
            if (isFavorite){
            binding.likeBtn.setImageResource(com.example.playlistmaker.R.drawable.liked_ic)}
            else{
            binding.likeBtn.setImageResource(com.example.playlistmaker.R.drawable.like_ic)}
        }

        binding.likeBtn.setOnClickListener {
            viewModel.onFavoriteClicked()
        }
        binding.playBtn.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }
        adapter = BottomSheetPlaylistAdapter(
            imageLoadRepository
        ) { playlist ->

            viewModel.onPlaylistClicked(playlist)
        }

        binding.playlistsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())

        binding.playlistsRecyclerView.adapter = adapter
        bottomSheetBehavior = BottomSheetBehavior
            .from(binding.playlistsBottomSheet)

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior.addBottomSheetCallback(
            object : BottomSheetBehavior.BottomSheetCallback() {

                override fun onStateChanged(
                    bottomSheet: View,
                    newState: Int
                ) {

                    binding.overlay.visibility =
                        if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                            View.GONE
                        } else {
                            View.VISIBLE
                        }
                }

                override fun onSlide(
                    bottomSheet: View,
                    slideOffset: Float
                ) {
                    binding.overlay.alpha =
                        ((slideOffset + 1f) / 2f)
                }
            }
        )
        viewModel.observePlaylists().observe(viewLifecycleOwner) {

            adapter.updateData(it)
        }
        binding.playlistBtn.setOnClickListener {

            viewModel.loadPlaylists()

            bottomSheetBehavior.state =
                BottomSheetBehavior.STATE_EXPANDED
        }
        binding.newPlaylistBottomButton.setOnClickListener {

            findNavController().navigate(
                com.example.playlistmaker.R.id.playlistCreatorFragment
            )
        }
        viewModel.observePlaylistMessage().observe(viewLifecycleOwner) {

            Toast
                .makeText(requireContext(), it, Toast.LENGTH_SHORT)
                .show()

            bottomSheetBehavior.state =
                BottomSheetBehavior.STATE_HIDDEN
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            bindMusicService()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.onAppInBackground()
    }
    override fun onResume() {
        super.onResume()

        viewModel.loadPlaylists()
        viewModel.onAppInForeground()
    }
    private fun updatePlayButtonState(state: Int) {
        when (state) {
            MusicService.STATE_DEFAULT -> {
                binding.playBtn.isEnabled = false
                binding.playBtn.setPlayingState(false)
            }

            MusicService.STATE_PREPARED -> {
                binding.playBtn.isEnabled = true
                binding.playBtn.setPlayingState(false)
            }

            MusicService.STATE_PLAYING -> {
                binding.playBtn.isEnabled = true
                binding.playBtn.setPlayingState(true)
            }

            MusicService.STATE_PAUSED -> {
                binding.playBtn.isEnabled = true
                binding.playBtn.setPlayingState(false)
            }
        }
    }
    private fun bindMusicService() {
        val intent = android.content.Intent(requireContext(), MusicService::class.java).apply {
            putExtra(ARGS_TRACK, track)
        }
        requireContext().bindService(intent, connection, android.content.Context.BIND_AUTO_CREATE)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (mBound) {
            requireContext().unbindService(connection)
            mBound = false
        }
    }
}