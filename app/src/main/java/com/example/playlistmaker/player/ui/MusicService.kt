package com.example.playlistmaker.player.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MusicService : Service(), PlayerServiceInteractor {

    inner class MusicBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    private val binder = MusicBinder()

    private var mediaPlayer: MediaPlayer? = null
    private var currentTrack: Track? = null

    private val playerStateFlow = MutableStateFlow(STATE_DEFAULT)
    override val playerState: StateFlow<Int> = playerStateFlow

    private val playerPositionFlow = MutableStateFlow(0L)
    override val playerPosition: StateFlow<Long> = playerPositionFlow

    private var timerJob: Job? = null
    private val serviceScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        createNotificationChannel()
    }

    override fun onBind(intent: Intent): IBinder {
        val track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("track", Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("track")
        }

        if (track != null) {
            currentTrack = track
            preparePlayer()
        }

        return binder
    }


    override fun play() {
        startPlayer()
    }

    override fun pause() {
        pausePlayer()
    }

    override fun showNotification() {


        val trackInfo =
            "${currentTrack?.artistName ?: "Неизвестный исполнитель"} - ${currentTrack?.trackName ?: "Неизвестный трек"}"

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Playlist Maker")
            .setContentText(trackInfo)
            .setSmallIcon(com.example.playlistmaker.R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    override fun hideNotification() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Playlist Maker Playback",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Канал для отображения играющего трека в Playlist Maker"
            }

            val notificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    private fun preparePlayer() {
        val url = currentTrack?.previewUrl ?: return
        try {
            mediaPlayer?.reset()
            mediaPlayer?.setDataSource(url)
            mediaPlayer?.prepareAsync()

            mediaPlayer?.setOnPreparedListener {
                playerStateFlow.value = STATE_PREPARED
            }
            mediaPlayer?.setOnCompletionListener {
                timerJob?.cancel()
                playerPositionFlow.value = 0L
                playerStateFlow.value = STATE_PREPARED
                hideNotification()
            }
        } catch (e: Exception) {
            playerStateFlow.value = STATE_DEFAULT
        }
    }

    private fun startPlayer() {
        mediaPlayer?.start()
        playerStateFlow.value = STATE_PLAYING
        startTimer()
    }

    private fun pausePlayer() {
        mediaPlayer?.pause()
        playerStateFlow.value = STATE_PAUSED
        timerJob?.cancel()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = serviceScope.launch {
            while (mediaPlayer?.isPlaying == true) {
                delay(DELAY)
                playerPositionFlow.value = mediaPlayer?.currentPosition?.toLong() ?: 0L
            }
        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        timerJob?.cancel()

        val playerToRelease = mediaPlayer
        mediaPlayer = null

        CoroutineScope(Dispatchers.IO).launch {
            try {
                playerToRelease?.stop()
                playerToRelease?.release()
            } catch (e: Exception) {

            }
        }

        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        timerJob?.cancel()

        val playerToRelease = mediaPlayer
        mediaPlayer = null

        CoroutineScope(Dispatchers.IO).launch {
            try {
                playerToRelease?.release()
            } catch (e: Exception) {
            }
        }
    }

    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
        private const val DELAY = 300L
        private const val NOTIFICATION_CHANNEL_ID = "playlist_maker_channel"
        private const val NOTIFICATION_ID = 101
    }
}