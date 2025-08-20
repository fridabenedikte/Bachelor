package com.emojigame.core.util

/**
 * Represents an Axis-Aligned Bounding Box (AABB) used for collision detection.
 *
 * An AABB is defined by a position (bottom-left corner) and its width and height.
 * It is used to check for intersections between entities in the game.
 *
 * @property pos The bottom-left position (minimum X and Y) of the AABB.
 * @property width The width of the bounding box.
 * @property height The height of the bounding box.
 */
data class AABB(val pos: Vector2f, val width: Float, val height: Float) {
    val left: Float get() = pos.x
    val right: Float get() = pos.x + width
    val top: Float get() = pos.y
    val bottom: Float get() = pos.y + height

    /**
     * Checks if this AABB intersects with another AABB.
     *
     * @param other The other AABB to check collision with.
     * @return `true` if the bounding boxes overlap, `false` otherwise.
     */
    fun intersects(other: AABB): Boolean {
        return this.right > other.left &&
            this.left < other.right &&
            this.bottom > other.top &&
            this.top < other.bottom
    }
}
