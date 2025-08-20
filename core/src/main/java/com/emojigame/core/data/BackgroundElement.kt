package com.emojigame.core.data

import com.emojigame.core.util.Vector2f

/**
 * Represents a background element in the game world.
 * The element has no collision
 *
 * @property pos The top-left position of the platform in virtual coordinates.
 * @property width The width of the platform in world units.
 * @property height The height of the platform in world units.
 * @property isEnabled Whether the platform is active (visible) or not.
 * @property visualStyle Meta data on color or image for this element
 */
data class BackgroundElement(
    var pos: Vector2f,
    var width: Float,
    var height: Float,
    var tag: PlatformTags,
    var isEnabled: Boolean = true,
    var visualStyle: VisualStyle = VisualStyle(),
) {
    /**
     * Returns a clone of the BackgroundElement
     */
    fun clone(): BackgroundElement {
        return BackgroundElement(
            pos = Vector2f(pos.x, pos.y),
            width = width,
            height = height,
            tag = tag,
            isEnabled = isEnabled,
            visualStyle = visualStyle,
        )
    }
}
