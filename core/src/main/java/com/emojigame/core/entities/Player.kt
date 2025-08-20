package com.emojigame.core.entities

import com.emojigame.core.data.Platform
import com.emojigame.core.util.AABB
import com.emojigame.core.util.Collision
import com.emojigame.core.util.Ray
import com.emojigame.core.util.Vector2f

/**
 * Represents the controllable player entity in the game world.
 *
 * The player moves toward a specified target using simple physics with
 * acceleration and gravity. It detects collisions with blocking platforms
 * and responds accordingly.
 */
class Player(
    override var pos: Vector2f,
    override val tag: String,
    override val width: Float = 100f,
    override val height: Float = 100f,
    private var isBlocked: Boolean = false
) : Entity() {

    /**
     * Current high-level state of the player.
     */
    enum class MovementState {
        IDLE,
        MOVING
    }

    /** If true, the player will not update its movement. */
    var isPaused: Boolean = false

    private var target: Vector2f = pos.copy()
    private val stopThreshold = 2.0f

    var movementState: MovementState = MovementState.IDLE
        private set

    init {
        acceleration = 1.0f
        deacceleration = 0.8f
        maxSpeed = 6.0f
        grounded = true
    }

    /**
     * Applies a gravity vector to the player.
     */
    fun applyGravity(value: Vector2f) {
        gravity = value
    }

    /**
     * Sets a new target position the player should move toward.
     */
    fun setTarget(targetPos: Vector2f) {
        this.target = targetPos
        dir = (target - pos).normalize()
    }

    /**
     * Returns the current movement target of the player.
     */
    fun getTarget(): Vector2f {
        return target
    }

    /**
     * Updates the player position, handling movement and platform collisions.
     */
    fun update(platforms: List<Platform>) {
        if (isPaused) return

        super.update()
        checkGround(platforms)

        val delta = target - pos
        val distSq = delta.lengthSquared()

        if (distSq < stopThreshold * stopThreshold) {
            stopAtTarget()
            return
        }

        if (grounded) {
            dir = delta.normalize()
            speed += dir * acceleration
            movementState = MovementState.MOVING
        } else {
            speed += gravity
        }

        speed = speed.clampLength(maxSpeed)

        val futurePos = pos + speed
        val futureHitbox = AABB(futurePos, width, height)

        val blockingPlatforms = platforms.filter { it.isEnabled && it.tag.isBlocking() }
        isBlocked = blockingPlatforms.any { Collision.aabbVsAabb(futureHitbox, it.hitbox) }

        if (!isBlocked) {
            pos = futurePos
        } else {
            speed = Vector2f(0f, 0f)
        }
    }

    /**
     * Uses a downward raycast to check if the player is grounded.
     */
    private fun checkGround(platforms: List<Platform>) {
        val rayOrigin = Vector2f(pos.x, pos.y + height)
        val ray = Ray(origin = rayOrigin, direction = Vector2f(0f, 1f), length = 5f)
        grounded = platforms.any { it.isEnabled && Collision.rayVsAabb(ray, it.hitbox) }
    }

    /**
     * Called when the player reaches the target or gets close enough.
     */
    private fun stopAtTarget() {
        pos.setVector(target.x, target.y)
        speed = Vector2f(0f, 0f)
        dir = Vector2f(0f, 0f)
        movementState = MovementState.IDLE
    }

    /**
     * Returns whether the player is currently blocked by a platform.
     */
    fun getIsBlocked(): Boolean = isBlocked

    /**
     * Creates a copy of this player instance with position and state preserved.
     */
    fun clone(): Player {
        return Player(
            pos = Vector2f(pos.x, pos.y),
            tag = tag,
            width = width,
            height = height,
        ).also {
            it.movementState = this.movementState
        }
    }
}
