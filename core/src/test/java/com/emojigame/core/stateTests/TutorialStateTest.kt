package com.emojigame.core.stateTests

import com.emojigame.core.states.GameStateManager
import com.emojigame.core.states.TutorialState
import com.emojigame.core.util.GameIntent
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Test

class TutorialStateTest {

    private lateinit var gameStateManager: GameStateManager
    private lateinit var tutorialState: TutorialState

    @Before
    fun setup() {
        gameStateManager = mockk(relaxed = true)

        every { gameStateManager.tutorialFlow } returns MutableStateFlow(mockk(relaxed = true))
        every { gameStateManager.gameStateFlow } returns MutableStateFlow(mockk(relaxed = true))

        tutorialState = TutorialState(gameStateManager)
    }

    @Test
    fun `onExit should process ClearTutorial intent`() {
        tutorialState.onExit()

        verify { gameStateManager.processIntent(GameIntent.ClearTutorial) }
    }
}
