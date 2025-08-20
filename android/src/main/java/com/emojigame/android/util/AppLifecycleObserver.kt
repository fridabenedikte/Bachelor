package com.emojigame.android.util

import android.content.Context
import android.content.Intent
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

/**
 * AppLifecycleObserver monitors the app's foreground and background transitions.
 * It sends START and PAUSE actions to MusicService based on app visibility.
 */
class AppLifecycleObserver(private val context: Context) : DefaultLifecycleObserver {

    override fun onStart(owner: LifecycleOwner) {
        val intent = Intent(context, MusicService::class.java).apply {
            action = "START"
        }
        context.startService(intent)
    }

    override fun onStop(owner: LifecycleOwner) {
        val intent = Intent(context, MusicService::class.java).apply {
            action = "PAUSE"
        }
        context.startService(intent)
    }
}
