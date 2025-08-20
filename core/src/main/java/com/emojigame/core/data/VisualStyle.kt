package com.emojigame.core.data

import com.emojigame.core.entities.Emoji

/**
 * Represents the visual appearance of a drawable game element.
 *
 * This can define either a solid color fill or an image reference by tag.
 * Only one should typically be used at a time.
 *
 * @property color The ARGB color used to render the element. Optional.
 * @property imageTag The tag or name of a drawable resource. Optional.
 */
data class VisualStyle(
    val color: Int? = null,
    val imageTag: String? = null,
    val emoji: Emoji? = null
)
