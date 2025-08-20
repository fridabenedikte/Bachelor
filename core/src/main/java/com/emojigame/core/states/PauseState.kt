package com.emojigame.core.states

import com.emojigame.core.util.GameIntent
import com.emojigame.core.util.Logger

/**
 * Represents the paused state of the game.
 * All game updates are frozen, and only certain intents (like unpausing) are allowed.
 */
class PauseState(
    gameStateManager: GameStateManager
) : GameState(gameStateManager) {

    override fun update() {
        // Game logic is frozen while paused
    }

    override fun handleIntent(intent: GameIntent) {
        when (intent) {
            is GameIntent.ChangeState -> gameStateManager.processIntent(intent)
            else -> Logger.i { "Paused: Ignoring intent: $intent" }
        }
    }

    override fun onEnter() {
        Logger.i { "🔇 Game paused." }
    }

    override fun onExit() {
        Logger.i { "▶️ Resuming game..." }
    }
}
