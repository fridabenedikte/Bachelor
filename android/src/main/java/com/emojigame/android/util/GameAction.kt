package com.emojigame.android.util

import com.emojigame.core.entities.Emoji
import com.emojigame.core.states.GameStateType

/**
 * Represents UI-driven actions for the MVI pattern.
 */
sealed class GameAction {
    data class SelectEmoji(val emojiRes: Emoji) : GameAction()

    data class DeselectEmoji(val emojiRes: Emoji) : GameAction()

    data object ClearSelection : GameAction()

    data class UpdateRepeatCount(val count: Int) : GameAction()

    data class PlaySelectedEmojis(val emojis: List<Emoji>) : GameAction()

    data class LoadLevel(val levelName: String) : GameAction()

    data object ResetLevel : GameAction()

    class ChangeState(val stateType: GameStateType) : GameAction()

    data object ClearLevel : GameAction()

    data object ShowHint : GameAction()

    data object ClearHint : GameAction()

    data object PauseGame : GameAction()

    data object ResumeGame : GameAction()

    data object OpenRepeatCountDialog : GameAction()

    data object CancelRepeatCountDialog : GameAction()
}
