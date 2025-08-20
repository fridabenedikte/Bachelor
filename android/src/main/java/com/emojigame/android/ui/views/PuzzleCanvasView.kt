package com.emojigame.android.ui.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.emojigame.android.ui.TextureStorage
import com.emojigame.core.data.BackgroundElement
import com.emojigame.core.data.Platform
import com.emojigame.core.definitions.LevelDefinitions
import com.emojigame.core.ui.Theme
import com.emojigame.core.util.Logger
import com.emojigame.core.util.Vector2f

/**
 * Custom view responsible for rendering the puzzle scene, including:
 * - Platforms
 * - Background elements
 * - Player character
 *
 * All rendering is resolution-independent, scaling logical game units to screen space.
 */
class PuzzleCanvasView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        style = Paint.Style.FILL
    }

    private val bitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val tempRectF = RectF()

    private var playerBitmap: Bitmap? = null
    private var playerPosition = Vector2f(0f, 0f)

    private var platforms: List<Platform> = emptyList()
    private var backgroundElements: List<BackgroundElement> = emptyList()

    init {
        setWillNotDraw(false)
        setLayerType(LAYER_TYPE_HARDWARE, null)
        setBackgroundColor(Color.TRANSPARENT)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val scaleX = width / LevelDefinitions.WIDTH
        val scaleY = height / LevelDefinitions.HEIGHT

        drawBackgroundElements(canvas, scaleX, scaleY)
        drawPlatforms(canvas, scaleX, scaleY)
        drawPlayer(canvas, scaleX, scaleY)
    }

    /**
     * Sets the player’s position in the canvas.
     * @param position The logical game position of the player.
     */
    fun updatePlayerPosition(position: Vector2f) {
        playerPosition = position
    }

    /**
     * Sets the bitmap representing the player.
     * @param bitmap The image of the character.
     */
    fun setPlayerBitmap(bitmap: Bitmap) {
        playerBitmap = bitmap
    }

    /**
     * Updates the list of platforms to render.
     * @param platforms The platforms in the scene.
     */
    fun updatePlatforms(platforms: List<Platform>) {
        Logger.i { "Updating puzzle platforms $platforms" }
        this.platforms = platforms
    }

    /**
     * Updates the list of background elements to render.
     * @param backgroundElements The background elements in the scene.
     */
    fun updateBackgroundElements(backgroundElements: List<BackgroundElement>) {
        this.backgroundElements = backgroundElements
    }

    /**
     * Forces the view to redraw its contents.
     */
    fun redraw() {
        invalidate()
    }

    // region Drawing logic
    /**
     * Draws all enabled background elements onto the canvas.
     */
    private fun drawBackgroundElements(canvas: Canvas, scaleX: Float, scaleY: Float) {
        backgroundElements.filter { it.isEnabled }.forEach { element ->
            val bitmap = getBitmapForElement(element)

            if (bitmap != null) {
                drawBitmap(canvas, bitmap, element.pos, element.width, element.height, scaleX, scaleY)
            } else {
                paint.color = element.visualStyle.color ?: getColorForTag(element.tag.toString())
                drawRect(canvas, element.pos, element.width, element.height, scaleX, scaleY, paint)
            }
        }
    }

    /**
     * Draws all enabled platforms onto the canvas.
     */
    private fun drawPlatforms(canvas: Canvas, scaleX: Float, scaleY: Float) {
        platforms.filter { it.isEnabled }.forEach { platform ->
            val bitmap = getBitmapForPlatform(platform)

            if (bitmap != null) {
                drawBitmap(canvas, bitmap, platform.pos, platform.width, platform.height, scaleX, scaleY)
            } else {
                paint.color = platform.visualStyle.color ?: getColorForTag(platform.tag.toString())
                drawRect(canvas, platform.pos, platform.width, platform.height, scaleX, scaleY, paint)
            }
        }
    }

    /**
     * Draws the player character onto the canvas using the current bitmap and position.
     */
    private fun drawPlayer(canvas: Canvas, scaleX: Float, scaleY: Float) {
        val bitmap = playerBitmap ?: return

        val playerSize = 150f
        val originalSize = 100f
        val sizeDiff = playerSize - originalSize

        val left = playerPosition.x * scaleX
        val top = (playerPosition.y - sizeDiff) * scaleY
        val right = (playerPosition.x + playerSize) * scaleX
        val bottom = (playerPosition.y + originalSize) * scaleY

        tempRectF.set(left, top, right, bottom)
        canvas.drawBitmap(bitmap, null, tempRectF, bitmapPaint)
    }

    /**
     * Draws a bitmap to the canvas at the specified scaled position and size.
     */
    private fun drawBitmap(
        canvas: Canvas,
        bitmap: Bitmap,
        pos: Vector2f,
        width: Float,
        height: Float,
        scaleX: Float,
        scaleY: Float
    ) {
        val left = pos.x * scaleX
        val top = pos.y * scaleY
        val right = (pos.x + width) * scaleX
        val bottom = (pos.y + height) * scaleY

        tempRectF.set(left, top, right, bottom)
        canvas.drawBitmap(bitmap, null, tempRectF, bitmapPaint)
    }

    /**
     * Draws a solid rectangle to the canvas using the given dimensions and paint.
     */
    private fun drawRect(
        canvas: Canvas,
        pos: Vector2f,
        width: Float,
        height: Float,
        scaleX: Float,
        scaleY: Float,
        paint: Paint
    ) {
        canvas.drawRect(
            pos.x * scaleX,
            pos.y * scaleY,
            (pos.x + width) * scaleX,
            (pos.y + height) * scaleY,
            paint
        )
    }

    // endregion

    // region Helpers
    /**
     * Retrieves the bitmap for a background element based on its visual style.
     */
    private fun getBitmapForElement(element: BackgroundElement): Bitmap? {
        return when {
            element.visualStyle.emoji != null ->
                TextureStorage.getEmojiBitmap(context, emoji = element.visualStyle.emoji)

            element.visualStyle.imageTag != null ->
                TextureStorage.getNamedBitmap(context, element.visualStyle.imageTag!!)

            else -> null
        }
    }

    /**
     * Retrieves the bitmap for a platform based on its visual style.
     */
    private fun getBitmapForPlatform(platform: Platform): Bitmap? {
        return when {
            platform.visualStyle.emoji != null ->
                TextureStorage.getEmojiBitmap(context, emoji = platform.visualStyle.emoji)

            platform.visualStyle.imageTag != null ->
                TextureStorage.getNamedBitmap(context, platform.visualStyle.imageTag!!)

            else -> null
        }
    }

    /**
     * Returns a fallback color for a given platform or element tag.
     */
    private fun getColorForTag(tag: String?): Int {
        return when (tag) {
            "Ground" -> Theme.grassColor
            "Bridge" -> Theme.bridgeColor
            "Water" -> Theme.waterColor
            else -> Color.GRAY
        }
    }

    // endregion
}
