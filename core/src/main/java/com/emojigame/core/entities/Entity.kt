package com.emojigame.core.entities

import com.emojigame.core.util.AABB
import com.emojigame.core.util.Vector2f

/**
 * Base class for all entities in the game.
 * Provides basic physics properties and collision box.
 */
abstract class Entity {
    abstract val pos: Vector2f
    abstract val tag: String
    abstract val width: Float
    abstract val height: Float

    protected var acceleration = 1.0f
    protected var deacceleration = 0.8f
    protected var maxSpeed = 3.0f

    protected var speed = Vector2f(0f, 0f)
    protected var dir = Vector2f(0f, 0f)

    protected var gravity = Vector2f(0f, 0f)
    protected var grounded = false

    fun fetchSpeed(): Vector2f = speed

    val aabb: AABB
        get() = AABB(pos, width, height)

    fun collidesWith(other: Entity): Boolean {
        return this.aabb.intersects(other.aabb)
    }

    fun update() {}
}
