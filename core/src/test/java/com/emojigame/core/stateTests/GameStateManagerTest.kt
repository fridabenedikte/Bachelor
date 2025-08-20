package com.emojigame.core.stateTests

import com.emojigame.core.states.GameStateManager
import com.emojigame.core.states.GameStateType
import com.emojigame.core.states.GameWonState
import com.emojigame.core.states.MenuState
import com.emojigame.core.states.PauseState
import com.emojigame.core.states.PlayState
import com.emojigame.core.states.RoadmapState
import com.emojigame.core.util.GameIntent
import io.mockk.clearMocks
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

@ExperimentalCoroutinesApi
class GameStateManagerTest {

    private lateinit var gameStateManager: GameStateManager
    private lateinit var playState: PlayState
    private lateinit var roadmapState: RoadmapState
    private lateinit var menuState: MenuState
    private lateinit var gameWonState: GameWonState
    private lateinit var pauseState: PauseState

    @Before
    fun setup() {
        playState = mockk(relaxed = true)
        roadmapState = mockk(relaxed = true)
        menuState = mockk(relaxed = true)
        gameWonState = mockk(relaxed = true)
        pauseState = mockk(relaxed = true)

        val testStateMap = mapOf(
            GameStateType.Play to playState,
            GameStateType.Roadmap to roadmapState,
            GameStateType.Menu to menuState,
            GameStateType.GameWon to gameWonState,
            GameStateType.Pause to pauseState,
        )

        gameStateManager = GameStateManager { type, _ -> testStateMap[type] }

        gameStateManager.processIntent(GameIntent.ChangeState(GameStateType.Roadmap))
    }

    @Test
    fun `update stateFlow and call onEnter`() {
        gameStateManager.processIntent(GameIntent.ChangeState(GameStateType.Play))
        assertEquals(GameStateType.Play, gameStateManager.stateFlow.value)
        verify { playState.onEnter() }
    }

    @Test
    fun `call onExit on previous state and onEnter on new state`() {
        gameStateManager.processIntent(GameIntent.ChangeState(GameStateType.Play))
        verify { playState.onEnter() }

        gameStateManager.processIntent(GameIntent.ChangeState(GameStateType.Menu))
        verify { playState.onExit() }
        verify { menuState.onEnter() }
    }

    @Test
    fun `changeState should ignore redundant state changes`() {
        gameStateManager.processIntent(GameIntent.ChangeState(GameStateType.Menu))
        clearMocks(menuState)

        gameStateManager.processIntent(GameIntent.ChangeState(GameStateType.Menu))

        verify(exactly = 0) { menuState.onExit() }
        verify(exactly = 0) { menuState.onEnter() }
    }

    @Test
    fun `processIntent should forward non-ChangeState intents to current state`() {
        gameStateManager.processIntent(GameIntent.ChangeState(GameStateType.Roadmap))
        val intent = GameIntent.CustomIntent("Test")
        gameStateManager.processIntent(intent)
        verify { roadmapState.handleIntent(intent) }
    }

    @Test
    fun `update should call update on currentState`() {
        gameStateManager.processIntent(GameIntent.ChangeState(GameStateType.Play))
        gameStateManager.update()
        verify { playState.update() }
    }

    @Test
    fun `updatePlayerPosition should update position if new position differs`() {
        val initial = gameStateManager.playerPositionFlow.value
        gameStateManager.updatePlayerPosition(initial.copy(x = initial.x + 1f))
        assertNotEquals(initial, gameStateManager.playerPositionFlow.value)
    }

    @Test
    fun `updatePlayerPosition should not update if rounded position is same`() {
        val initial = gameStateManager.playerPositionFlow.value
        gameStateManager.updatePlayerPosition(initial.copy(x = initial.x + 0.1f))
        assertEquals(initial, gameStateManager.playerPositionFlow.value)
    }
}
