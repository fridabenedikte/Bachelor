package com.emojigame.core.data

import com.emojigame.core.entities.Emoji

/**
 * Represents a contextual hint displayed to the player during a level.
 *
 * @property emoji The emoji related to the hint (if applicable).
 * @property message A message to be shown alongside the emoji.
 */
data class Hint(
    val emoji: Emoji?,
    val message: String?,
)
