package com.emojigame.core

import com.emojigame.core.states.GameStateManager
import com.emojigame.core.states.GameStateType
import com.emojigame.core.util.GameIntent
import com.emojigame.core.util.Logger
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Singleton CoreManager that runs the game loop on a separate thread.
 */
object CoreManager : Runnable {
    private val running = AtomicBoolean(false)
    private lateinit var thread: Thread
    private val gameStateManager = GameStateManager()

    private const val GAME_HERTZ = 60.0
    private const val NANOSECONDS_PER_UPDATE = (1_000_000_000 / GAME_HERTZ)
    private const val MAX_UPDATES_BEFORE_RENDER = 5

    /**
     * Starts the game loop in a separate thread.
     */
    fun start() {
        if (running.compareAndSet(false, true)) {
            thread = Thread(this, "GameThread")
            thread.start()
        }
        gameStateManager.processIntent(GameIntent.ChangeState(GameStateType.Menu))
    }

    /**
     * Stops the game loop and waits for the thread to terminate.
     */
    fun stop() {
        running.set(false)
        try {
            thread.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    /**
     * The main game loop that continuously updates the game state.
     */
    override fun run() {
        var lastUpdateTime = System.nanoTime()
        var lastRenderTime: Long

        var tickCount = 0
        var frameCount = 0
        var lastSecondTime = (lastUpdateTime / 1_000_000_000).toInt()

        while (running.get()) {
            val now = System.nanoTime()
            var updateCount = 0

            if (now - lastUpdateTime > 10 * NANOSECONDS_PER_UPDATE) {
                lastUpdateTime = (now - NANOSECONDS_PER_UPDATE).toLong()
            }

            while ((now - lastUpdateTime) > NANOSECONDS_PER_UPDATE && updateCount < MAX_UPDATES_BEFORE_RENDER) {
                update()
                lastUpdateTime += NANOSECONDS_PER_UPDATE.toLong()
                updateCount++
                tickCount++
            }

            if (now - lastUpdateTime > NANOSECONDS_PER_UPDATE) {
                lastUpdateTime = (now - NANOSECONDS_PER_UPDATE).toLong()
            }

            lastRenderTime = now
            frameCount++

            val thisSecond = (lastUpdateTime / 1_000_000_000).toInt()
            if (thisSecond > lastSecondTime) {
                tickCount = 0
                frameCount = 0
                lastSecondTime = thisSecond
            }

            while (System.nanoTime() - lastRenderTime < NANOSECONDS_PER_UPDATE) {
                Thread.yield()
            }
        }
    }

    /**
     * Update functions that runs 60 times a second
     */
    private fun update() {
        gameStateManager.update()
    }

    fun getGameStateManager(): GameStateManager {
        return gameStateManager
    }

    fun isRunning(): Boolean = running.get()

    fun processIntent(intent: GameIntent) {
        Logger.i { "Processing GameIntent: $intent" }

        gameStateManager.processIntent(intent)
    }
}
