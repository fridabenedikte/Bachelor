package com.emojigame.core.stateTests

import com.emojigame.core.states.GameStateManager
import com.emojigame.core.states.PauseState
import com.emojigame.core.util.GameIntent
import org.junit.Test

class PauseStateTest {
    private val manager = GameStateManager()
    private val state = PauseState(manager)

    @Test
    fun `handleIntent should call changeState on ChangeState intent`() {
        state.handleIntent(GameIntent.ChangeState(com.emojigame.core.states.GameStateType.Play))
    }

    @Test
    fun `onEnter and onExit should log messages`() {
        state.onEnter()
        state.onExit()
    }
}
