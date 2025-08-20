package com.emojigame.core.states

import com.emojigame.core.util.GameIntent
import com.emojigame.core.util.Logger

/**
 * Represents the state of the game when the player has completed or won the current level.
 *
 * This state is typically entered after all goals have been fulfilled.
 * It currently does not perform updates or react to intents meaningfully,
 * but the structure is here for future extensibility (e.g. UI popups, score tracking).
 *
 * @param gameStateManager The manager that handles state transitions and game flow.
 */
class GameWonState(
    gameStateManager: GameStateManager
) : GameState(gameStateManager) {

    override fun onEnter() {
    }

    override fun update() {
    }

    /**
     * Handles any [GameIntent] received while in this state.
     * Currently logs that an intent was received, but no functional behavior is triggered.
     */
    override fun handleIntent(intent: GameIntent) {
        Logger.i { "Handling intent in GameWonState: $intent" }
    }
}
