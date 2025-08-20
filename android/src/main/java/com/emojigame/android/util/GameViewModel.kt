package com.emojigame.android.util

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.emojigame.android.R
import com.emojigame.core.CoreManager
import com.emojigame.core.states.GameStateType
import com.emojigame.core.ui.GameStateFlow
import com.emojigame.core.ui.TutorialStateFlow
import com.emojigame.core.util.GameIntent
import com.emojigame.core.util.Logger
import com.emojigame.core.util.Vector2f
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

/**
 * [GameViewModel] acts as the bridge between the Android UI layer and the core game engine.
 * It observes state flows from the [CoreManager], sends [GameIntent]s to [CoreService],
 * and handles UI-driven [GameAction] events.
 */
class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val appContext = application.applicationContext
    private val gameStateManager = CoreManager.getGameStateManager()

    private var coreService: WeakReference<CoreService>? = null
    private var bound = false
    private var pendingIntent: GameIntent? = null

    private val _uiState = MutableStateFlow(GameStateFlow())
    val uiState: StateFlow<GameStateFlow> get() = _uiState.asStateFlow()

    private val _playerPosState = MutableStateFlow(Vector2f(0f, 0f))
    val playerPosState: StateFlow<Vector2f> get() = _playerPosState.asStateFlow()

    private val _gameStateFlow = MutableStateFlow(GameStateType.Menu)
    val gameStateFlow: StateFlow<GameStateType> get() = _gameStateFlow

    private val _tutorialFlow = MutableStateFlow(TutorialStateFlow())
    val tutorialFlow: StateFlow<TutorialStateFlow> get() = _tutorialFlow

    /**
     * Starts observing core state and binds to [CoreService].
     */
    fun initialize() {
        bindCoreService()
        observeGameState()
        observeUiState()
        observePlayerPosition()
        observeTutorialState()
    }

    /** Observes top-level game mode (Menu, Play, etc). */
    private fun observeGameState() = viewModelScope.launch {
        gameStateManager.stateFlow.collect { _gameStateFlow.value = it }
    }

    /** Observes UI-related game state changes during Play mode. */
    private fun observeUiState() = viewModelScope.launch {
        gameStateManager.gameStateFlow.collect {
            if (_gameStateFlow.value == GameStateType.Play) _uiState.value = it.copy()
        }
    }

    /** Observes and updates player position. */
    private fun observePlayerPosition() = viewModelScope.launch {
        gameStateManager.playerPositionFlow.collect {
            if (_playerPosState.value != it) _playerPosState.value = it
        }
    }

    /** Observes tutorial state updates. */
    private fun observeTutorialState() = viewModelScope.launch {
        gameStateManager.tutorialFlow.collect { _tutorialFlow.value = it.copy() }
    }

    /**
     * Sends a [GameIntent] to the bound [CoreService] or stores it if not yet bound.
     */
    fun sendGameIntent(intent: GameIntent) {
        coreService?.get()?.let {
            if (bound) {
                it.processGameIntent(intent)
                return
            }
        }
        pendingIntent = intent
    }

    /**
     * Converts UI actions to core game intents.
     */
    fun handleAction(action: GameAction) = when (action) {
        is GameAction.SelectEmoji -> sendGameIntent(GameIntent.SelectEmoji(action.emojiRes))
        is GameAction.DeselectEmoji -> sendGameIntent(GameIntent.DeselectEmoji(action.emojiRes))
        is GameAction.ClearSelection -> sendGameIntent(GameIntent.ClearSelection)
        is GameAction.UpdateRepeatCount -> sendGameIntent(GameIntent.UpdateRepeatCount(action.count))
        is GameAction.PlaySelectedEmojis -> sendGameIntent(GameIntent.PlayEmoji(action.emojis))
        is GameAction.LoadLevel -> loadLevelAndTryOverload(action.levelName)
        is GameAction.ResetLevel -> sendGameIntent(GameIntent.ResetLevel)
        is GameAction.ChangeState -> sendGameIntent(GameIntent.ChangeState(action.stateType))
        is GameAction.ShowHint -> sendGameIntent(GameIntent.ShowHint)
        is GameAction.ClearHint -> sendGameIntent(GameIntent.ClearHint)
        is GameAction.ClearLevel -> sendGameIntent(GameIntent.ClearLevel)
        is GameAction.PauseGame -> sendGameIntent(GameIntent.PauseGame)
        is GameAction.ResumeGame -> sendGameIntent(GameIntent.ResumeGame)
        is GameAction.CancelRepeatCountDialog -> sendGameIntent(GameIntent.CancelRepeatCountDialog)
        is GameAction.OpenRepeatCountDialog -> sendGameIntent(GameIntent.OpenRepeatCountDialog)
    }

    /** Maps known level names to layout XML resources. */
    private fun mapLevelToLayout(levelName: String): Int? = when (levelName.lowercase()) {
        "level1" -> R.layout.level1
        "level2" -> R.layout.level2
        "level3" -> R.layout.level3
        "level4" -> R.layout.level4
        else -> null
    }

    /**
     * Loads a level and applies layout-based data if available.
     */
    private fun loadLevelAndTryOverload(levelName: String) {
        sendGameIntent(GameIntent.LoadLevel(levelName))
        mapLevelToLayout(levelName)?.let {
            val overloadIntent = XmlLevelParser.parse(appContext, levelName, it)
            sendGameIntent(overloadIntent)
        } ?: Logger.i { "No XML layout found to overload level: $levelName" }
    }

    /** Binds ViewModel to [CoreService] using application context. */
    private fun bindCoreService() {
        val intent = Intent(appContext, CoreService::class.java)
        val success = appContext.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        if (!success) Logger.e { "Failed to bind to CoreService!" }
    }

    /** Defines callbacks for [CoreService] binding lifecycle. */
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as? CoreService.CoreBinder
            coreService = WeakReference(binder?.getService())
            bound = true
            pendingIntent?.let {
                sendGameIntent(it)
                pendingIntent = null
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            coreService = null
            bound = false
        }
    }
}
