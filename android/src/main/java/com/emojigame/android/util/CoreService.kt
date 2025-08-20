package com.emojigame.android.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.emojigame.core.CoreManager
import com.emojigame.core.util.GameIntent
import com.emojigame.core.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Foreground service responsible for managing the core thread and communication with it.
 */
class CoreService : Service() {
    private val binder = CoreBinder()
    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        startForegroundService()
        serviceScope.launch {
            CoreManager.start()
            Logger.i { "CoreService started in background" }
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        CoreManager.stop()
        Logger.i { "CoreService stopped" }
    }

    /**
     * Unified function to process all game intents.
     */
    fun processGameIntent(intent: GameIntent) {
        Logger.i { "Processing Game Intent: $intent" }
        CoreManager.processIntent(intent)
    }

    /**
     * Binder class for clients to interact with the service.
     */
    inner class CoreBinder : Binder() {
        /**
         * Returns an instance of [CoreService].
         */
        fun getService(): CoreService = this@CoreService
    }

    /**
     * Initializes and starts the foreground service with a notification.
     */
    private fun startForegroundService() {
        val channelId = "CoreServiceChannel"
        val notificationManager = getSystemService(NotificationManager::class.java)

        val channel =
            NotificationChannel(
                channelId,
                "Core Service",
                NotificationManager.IMPORTANCE_LOW,
            )
        notificationManager.createNotificationChannel(channel)

        val notification =
            NotificationCompat.Builder(this, channelId)
                .setContentTitle("Emoji Game Running")
                .setContentText("Managing game state...")
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build()

        startForeground(1, notification)
    }
}
