package com.emojigame.android.util

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import com.emojigame.android.R

/**
 * A foreground service responsible for handling background music playback.
 *
 * Responds to "START" and "PAUSE" actions via [Intent]s. Playback state is
 * synchronized with [MusicManager].
 */
class MusicService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private var isPaused = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "START" -> {
                if (!MusicManager.userHasDisabledMusic) {
                    startMusic()
                }
            }
            "PAUSE" -> {
                pauseMusic()
            }
        }
        return START_STICKY
    }

    /**
     * Starts or resumes the music playback, reinitializing the [MediaPlayer] if needed.
     */
    private fun startMusic() {
        if (mediaPlayer == null || !mediaPlayer!!.isPlaying) {
            resetMediaPlayer()
        }

        isPaused = false
        MusicManager.isPlaying = true
        MusicManager.userHasDisabledMusic = false
    }

    /**
     * Safely initializes and starts the [MediaPlayer] with looping enabled.
     */
    private fun resetMediaPlayer() {
        try {
            mediaPlayer?.release()
        } catch (_: Exception) {
        }
        mediaPlayer = MediaPlayer.create(this, R.raw.background_music).apply {
            isLooping = true
            start()
        }
    }

    /**
     * Pauses music playback and updates the [MusicManager] state.
     */
    private fun pauseMusic() {
        mediaPlayer?.pause()
        isPaused = true
        MusicManager.isPlaying = false
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
