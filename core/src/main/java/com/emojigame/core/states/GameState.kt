package com.emojigame.core.states

import com.emojigame.core.util.GameIntent

/**
 * Abstract base class for all game states.
 * Each state must implement update and input handling logic.
 */
abstract class GameState(val gameStateManager: GameStateManager) {
    /**
     * Called when this state becomes active.
     */

    open fun onEnter() {}

    /**
     * Called when transitioning out of this state.
     */
    open fun onExit() {}

    /**
     * Updates the game logic specific to this state. Called from StateManger that is controlled by CoreManager
     */
    abstract fun update()

    /**
     * Handles user intents related to this state.
     */
    abstract fun handleIntent(intent: GameIntent)
}
