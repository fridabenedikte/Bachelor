package com.emojigame.core.ui

import com.emojigame.core.data.BackgroundElement
import com.emojigame.core.data.Hint
import com.emojigame.core.data.Platform
import com.emojigame.core.entities.Emoji
import com.emojigame.core.entities.Player

/**
 * Abstract interface to unify different UI state types (GameStateFlow, TutorialStateFlow, etc.)
 */
interface BaseStateFlow {
    val availableEmojis: List<Emoji>
    val selectedEmojis: List<Emoji>
    val playedEmojis: List<Emoji>
    val player: Player?
    val platforms: List<Platform>
    val backgroundElements: List<BackgroundElement>
    val hint: Hint? get() = null
}
