package com.emojigame.core.ui

import com.emojigame.core.data.BackgroundElement
import com.emojigame.core.data.Hint
import com.emojigame.core.data.Level
import com.emojigame.core.data.Platform
import com.emojigame.core.entities.Emoji
import com.emojigame.core.entities.Entity
import com.emojigame.core.entities.Player
import com.emojigame.core.util.Vector2f

/**
 * Centralized game state representation for the UI.
 */
data class GameStateFlow(
    val entities: List<Entity> = emptyList(),
    val level: Level? = null,
    override val availableEmojis: List<Emoji> = emptyList(),
    override val playedEmojis: List<Emoji> = emptyList(),
    override val selectedEmojis: List<Emoji> = emptyList(),
    override val player: Player? = null,
    override val hint: Hint? = null,
    override val platforms: List<Platform> = emptyList(),
    override val backgroundElements: List<BackgroundElement> = emptyList(),
    val playerPosition: Vector2f = Vector2f(0f, 0f),
    val repeatCount: Int = 1,
    val ifOrElse: List<Boolean> = emptyList(),
    val ifElsePhotos: List<String> = emptyList()
) : BaseStateFlow
