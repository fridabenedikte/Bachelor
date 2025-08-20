package com.emojigame.core.util

import com.emojigame.core.data.BackgroundElement
import com.emojigame.core.data.Platform
import com.emojigame.core.entities.Emoji
import com.emojigame.core.states.GameStateType

/**
 * Intents (functions) that android can call
 */
sealed class GameIntent {
    data class ChangeState(val newSateType: GameStateType) : GameIntent()

    data class CustomIntent(val action: String) : GameIntent()

    data class LoadLevel(val levelName: String) : GameIntent()

    data object BackToMenu : GameIntent()

    data class PlayEmoji(val emojis: List<Emoji>) : GameIntent()

    data class SelectEmoji(val emoji: Emoji) : GameIntent()

    data class DeselectEmoji(val emoji: Emoji) : GameIntent()

    data object ClearSelection : GameIntent()

    data class UpdateRepeatCount(val count: Int) : GameIntent()

    data object ResetLevel : GameIntent()

    data class OverloadLevel(
        val levelName: String,
        val platforms: List<Platform>,
        val backgroundElements: List<BackgroundElement>,
    ) : GameIntent()

    data object ClearTutorial : GameIntent()

    data object ShowHint : GameIntent()

    data object ClearHint : GameIntent()

    data object ClearLevel : GameIntent()

    data object PauseGame : GameIntent()

    data object ResumeGame : GameIntent()

    data class UpdateIfOrElse(val newIfOrElse: List<Boolean>) : GameIntent()

    data object OpenRepeatCountDialog : GameIntent()

    data object CancelRepeatCountDialog : GameIntent()
}
