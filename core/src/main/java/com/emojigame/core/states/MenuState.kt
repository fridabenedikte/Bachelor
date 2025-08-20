package com.emojigame.core.states

import com.emojigame.core.util.GameIntent

/**
 * Represents the game state when the player is in the main menu.
 *
 * This state is usually idle and awaits input to transition into gameplay or other screens.
 */
class MenuState(gameStateManager: GameStateManager) : GameState(gameStateManager) {
    override fun update() {
    }

    override fun handleIntent(intent: GameIntent) {
    }
}
