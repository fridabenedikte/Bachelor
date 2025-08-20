package com.emojigame.core.util

/**
 * Represents a 2D ray used for collision detection or visibility tracing.
 *
 * A ray starts at a given origin point and extends in a specified direction.
 * Optionally, it can have a maximum length, after which intersections are ignored.
 *
 * @property origin The starting point of the ray in world coordinates.
 * @property direction The direction vector of the ray (should be normalized for accurate results).
 * @property length The maximum distance the ray can travel. Defaults to [Float.POSITIVE_INFINITY] for an infinite ray.
 */
data class Ray(
    val origin: Vector2f,
    val direction: Vector2f,
    val length: Float = Float.POSITIVE_INFINITY,
)
