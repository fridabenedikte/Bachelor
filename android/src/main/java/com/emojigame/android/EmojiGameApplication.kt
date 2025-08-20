package com.emojigame.android

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.emojigame.android.util.AppLifecycleObserver

/**
 * EmojiGameApplication initializes global lifecycle observers for the app.
 * It attaches AppLifecycleObserver to monitor foreground/background transitions.
 */
class EmojiGameApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleObserver(this))
    }
}
