package com.emojigame.android.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

/**
 * Handles sprite sheet animations by slicing a bitmap into frames and cycling through them.
 *
 * This class supports both resource-based sprite sheets and direct bitmap inputs. It provides
 * methods to retrieve the current animation frame based on time and a static idle frame.
 */
class BitmapAnimationHandler {
    private val frames: List<Bitmap>
    private var currentFrameIndex = 0
    private var lastFrameTime = System.currentTimeMillis()
    private val frameCount: Int
    private val frameDurationMs: Long

    /**
     * Constructs an animation handler from a sprite sheet in resources.
     *
     * @param context Context used to decode the resource.
     * @param spriteResId Resource ID of the sprite sheet.
     * @param frameCount Number of frames in the sprite sheet.
     * @param frameDurationMs Duration of each frame in milliseconds.
     */
    constructor(
        context: Context,
        spriteResId: Int,
        frameCount: Int,
        frameDurationMs: Long
    ) {
        this.frameCount = frameCount
        this.frameDurationMs = frameDurationMs

        val fullBitmap = BitmapFactory.decodeResource(context.resources, spriteResId)
        val frameWidth = fullBitmap.width / frameCount
        val frameHeight = fullBitmap.height

        frames = List(frameCount) { i ->
            Bitmap.createBitmap(fullBitmap, i * frameWidth, 0, frameWidth, frameHeight)
        }
    }

    /**
     * Constructs an animation handler from a preloaded [Bitmap] sprite sheet.
     *
     * @param bitmap The sprite sheet as a bitmap.
     * @param frameCount Number of frames in the sprite sheet.
     * @param frameDurationMs Duration of each frame in milliseconds.
     */
    constructor(
        bitmap: Bitmap,
        frameCount: Int,
        frameDurationMs: Long
    ) {
        this.frameCount = frameCount
        this.frameDurationMs = frameDurationMs

        val frameWidth = bitmap.width / frameCount
        val frameHeight = bitmap.height

        frames = List(frameCount) { i ->
            Bitmap.createBitmap(bitmap, i * frameWidth, 0, frameWidth, frameHeight)
        }
    }

    /**
     * Returns the current animation frame based on elapsed time.
     *
     * Cycles to the next frame if enough time has passed since the last update.
     *
     * @return The current animation [Bitmap] frame.
     */
    fun getCurrentFrame(): Bitmap {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastFrameTime > frameDurationMs) {
            currentFrameIndex = (currentFrameIndex + 1) % frameCount
            lastFrameTime = currentTime
        }
        return frames[currentFrameIndex]
    }

    /**
     * Returns the idle frame (the first frame of the sprite sheet).
     *
     * Useful when the animation should be paused or reset visually.
     *
     * @return The idle [Bitmap] frame.
     */
    fun getIdleFrame(): Bitmap {
        return frames[0]
    }
}
