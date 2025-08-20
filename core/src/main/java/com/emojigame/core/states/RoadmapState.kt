package com.emojigame.core.states

import com.emojigame.core.util.GameIntent
import com.emojigame.core.util.Logger

/**
 * [RoadmapState] represents the screen where players can view and select available levels.
 *
 * This state is passive and typically serves as a navigation hub before gameplay.
 */
class RoadmapState(gameStateManager: GameStateManager) : GameState(gameStateManager) {
    override fun onEnter() {
        Logger.i { "Entering RoadmapState" }
    }

    override fun update() {
        // No periodic logic needed here (for now)
    }

    override fun onExit() {
        Logger.i { "Exiting RoadmapState" }
    }

    override fun handleIntent(intent: GameIntent) {
        // No intent handling implemented yet
    }
}
