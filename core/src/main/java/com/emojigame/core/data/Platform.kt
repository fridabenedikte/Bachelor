package com.emojigame.core.data

import com.emojigame.core.util.AABB
import com.emojigame.core.util.Vector2f

/**
 * Represents a solid platform in the game world.
 *
 * Platforms define collidable geometry that players or other entities can stand on.
 * Each platform has a position, size, a type tag, and an optional visual style.
 *
 * @property pos The top-left position of the platform in virtual coordinates.
 * @property width The width of the platform in virtual world units (usually based on a 1000x1000 space).
 * @property height The height of the platform in virtual world units.
 * @property tag The [PlatformTags] value describing the platform’s category (e.g., ground, bridge).
 * @property isEnabled Whether the platform is active (i.e., visible and collidable).
 * @property visualStyle Optional visual styling (color or image) used for rendering the platform.
 */
data class Platform(
    var id: String = "",
    var pos: Vector2f,
    var width: Float,
    var height: Float,
    var tag: PlatformTags,
    var isEnabled: Boolean = true,
    var visualStyle: VisualStyle = VisualStyle(),
) {
    /**
     * Returns the platform’s hitbox as an [AABB] (Axis-Aligned Bounding Box).
     *
     * Used for collision detection and physics. Only valid if [isEnabled] is `true`.
     */
    val hitbox: AABB
        get() = AABB(pos, width, height)

    /**
     * Creates and returns a deep clone of this platform.
     *
     * Useful when loading levels or duplicating platform state.
     *
     * @return A new [Platform] with copied position and properties.
     */
    fun clone(): Platform {
        return Platform(
            id = id, // 🔥 Ta med ID her
            pos = Vector2f(pos.x, pos.y),
            width = width,
            height = height,
            tag = tag,
            isEnabled = isEnabled,
            visualStyle = visualStyle,
        )
    }
}
