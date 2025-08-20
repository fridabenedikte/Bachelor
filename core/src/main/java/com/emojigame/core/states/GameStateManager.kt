package com.emojigame.core.states

import com.emojigame.core.entities.Player
import com.emojigame.core.ui.GameStateFlow
import com.emojigame.core.ui.TutorialStateFlow
import com.emojigame.core.util.GameIntent
import com.emojigame.core.util.Logger
import com.emojigame.core.util.Vector2f
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Manages the active game state and facilitates state transitions.
 * It ensures that only one state is active at a time.
 */
class GameStateManager(
    private val stateFactory: (GameStateType, GameStateManager) -> GameState? = { type, manager ->
        // Can use default value for prod, and include state variable for testing
        when (type) {
            GameStateType.Play -> PlayState(manager)
            GameStateType.Pause -> PauseState(manager)
            GameStateType.Roadmap -> RoadmapState(manager)
            GameStateType.Menu -> MenuState(manager)
            GameStateType.GameWon -> GameWonState(manager)
            GameStateType.Tutorial -> TutorialState(manager)
            else -> null
        }
    }
) {
    var currentState: GameState? = null

    private val _gameStateFlow = MutableStateFlow(GameStateFlow())
    val gameStateFlow: StateFlow<GameStateFlow> get() = _gameStateFlow

    private val _playerPositionFlow = MutableStateFlow(Vector2f(0f, 0f))
    val playerPositionFlow: StateFlow<Vector2f> get() = _playerPositionFlow

    private val _stateFlow = MutableStateFlow(GameStateType.Menu)
    val stateFlow: StateFlow<GameStateType> get() = _stateFlow

    private val _tutorialFlow = MutableStateFlow(TutorialStateFlow())
    val tutorialFlow: StateFlow<TutorialStateFlow> get() = _tutorialFlow

    /**
     * Changes the active game state.
     */
    private fun changeState(newState: GameStateType) {
        if (_stateFlow.value == newState) {
            Logger.i { "Ignoring redundant state change to $newState" }
            return
        }

        Logger.i { "GameManager is changing state to $newState..." }
        currentState?.onExit()

        _stateFlow.value = newState

        currentState = stateFactory(newState, this)

        currentState?.onEnter()
    }

    /**
     * Updates the current [GameState].
     */
    fun update() {
        currentState?.update()
        detectCollisions()
    }

    /**
     * Updates [GameStateFlow] with new information.
     */
    fun updateState(update: GameStateFlow.() -> GameStateFlow) {
        _gameStateFlow.value = _gameStateFlow.value.update()
    }

    /**
     * Updates [Player] position with new position.
     * Handled outside GameStateFlow to minimize ui updates
     */
    fun updatePlayerPosition(newPosition: Vector2f) {
        val roundedNewPosition = Vector2f(newPosition.x.toInt().toFloat(), newPosition.y.toInt().toFloat())
        val roundedOldPosition = _playerPositionFlow.value

        if (roundedOldPosition.x != roundedNewPosition.x || roundedOldPosition.y != roundedNewPosition.y) {
            _playerPositionFlow.value = roundedNewPosition.copy()
        }
    }

    fun updateTutorial(update: TutorialStateFlow.() -> TutorialStateFlow) {
        _tutorialFlow.value = _tutorialFlow.value.update()
    }

    /**
     * Handles game intents by forwarding them to the current state.
     */
    fun processIntent(intent: GameIntent) {
        when (intent) {
            is GameIntent.ChangeState -> changeState(intent.newSateType)
            is GameIntent.ClearLevel -> updateState { GameStateFlow().copy() }
            is GameIntent.ClearTutorial -> updateTutorial { TutorialStateFlow().copy() }
            else -> currentState?.handleIntent(intent)
        }
    }

    /**
     * Detects collisions between the player and other game objects (PlayedEmojis).
     *
     * 1. Retrieves the player from the game state. If the player doesn't exist, the function exits.
     * 2. Gets the player's Axis-Aligned Bounding Box (AABB).
     * 3. Filters out all emojiFields that have an intersecting AABB with the player.
     * 4. If there are colliding entities, they are logged, and further collision handling can be implemented.
     *
     * TODO: Add specific collision handling (e.g., stop movement).
     */
    private fun detectCollisions() {
        val player = _gameStateFlow.value.player ?: return
        val playerAABB = player.aabb

        val collidedEntities = _gameStateFlow.value.playedEmojis.filter { it.aabb.intersects(playerAABB) }

        if (collidedEntities.isNotEmpty()) {
            println("Collision detected with: ${collidedEntities.map { it.tag }}")
        }
    }
}
