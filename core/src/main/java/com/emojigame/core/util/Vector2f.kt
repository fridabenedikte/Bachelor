package com.emojigame.core.util

import kotlin.math.sqrt

/**
 * A simple 2D vector class for handling positions, movement, and physics.
 */
data class Vector2f(var x: Float, var y: Float) {
    /** Returns the magnitude (length) of the vector. */
    fun magnitude(): Float = sqrt(x * x + y * y)

    /** Returns the squared length (more efficient when no sqrt is needed). */
    fun lengthSquared(): Float = x * x + y * y

    /** Returns a normalized copy of the vector (length = 1). */
    fun normalize(): Vector2f {
        val length = magnitude()
        return if (length > 0f) Vector2f(x / length, y / length) else Vector2f(0f, 0f)
    }

    /** Scales the vector and returns a new one. */
    fun scale(scalar: Float): Vector2f = Vector2f(x * scalar, y * scalar)

    /** Dot product with another vector. */
    fun dot(other: Vector2f): Float = x * other.x + y * other.y

    /** Set the vector's components directly. */
    fun setVector(
        x: Float,
        y: Float,
    ) {
        this.x = x
        this.y = y
    }

    /** Adds a value to the x-component. */
    fun addX(value: Float) {
        x += value
    }

    /** Adds a value to the y-component. */
    fun addY(value: Float) {
        y += value
    }

    /** Returns a copy of this vector. */
    fun copy(): Vector2f = Vector2f(x, y)

    /** Clamps the vector to a said length*/
    fun clampLength(maxLength: Float): Vector2f {
        val lenSq = lengthSquared()
        if (lenSq > maxLength * maxLength) {
            val len = sqrt(lenSq)
            val scale = maxLength / len
            return Vector2f(x * scale, y * scale)
        }
        return this
    }

    // -------- Operator overloads --------

    operator fun plus(other: Vector2f): Vector2f = Vector2f(x + other.x, y + other.y)

    operator fun minus(other: Vector2f): Vector2f = Vector2f(x - other.x, y - other.y)

    operator fun times(scalar: Float): Vector2f = Vector2f(x * scalar, y * scalar)

    operator fun div(scalar: Float): Vector2f = Vector2f(x / scalar, y / scalar)

    operator fun unaryMinus(): Vector2f = Vector2f(-x, -y)
}
