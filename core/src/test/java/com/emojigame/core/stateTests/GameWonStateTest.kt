package com.emojigame.core.stateTests

import com.emojigame.core.states.GameStateManager
import com.emojigame.core.states.GameWonState
import com.emojigame.core.util.GameIntent
import org.junit.Test

class GameWonStateTest {
    private val manager = GameStateManager()
    private val state = GameWonState(manager)

    @Test
    fun `onEnter should log entry`() {
        state.onEnter()
    }

    @Test
    fun `handleIntent should log handling`() {
        state.handleIntent(GameIntent.CustomIntent("anything"))
    }
}
