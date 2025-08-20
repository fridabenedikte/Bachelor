package com.emojigame.android.ui.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.toColorInt
import com.emojigame.android.R
import com.emojigame.core.definitions.LevelDefinitions
import com.emojigame.core.util.Vector2f

/**
 * A custom view that displays a vertical roadmap of levels as clickable nodes.
 * Unlocked levels are highlighted; locked levels are grayed out with a lock icon.
 */
class RoadmapCanvasView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    private val levelCount = LevelDefinitions.allLevels.count()
    var unlockedLevels = LevelDefinitions.allLevels.count()

    private val nodePositions = mutableListOf<Vector2f>()
    private val circleRadius = 70f

    private val unlockedPaint = Paint().apply {
        color = "#FF9800".toColorInt()
        style = Paint.Style.FILL
        isAntiAlias = true
        setShadowLayer(8f, 4f, 4f, Color.BLACK)
    }

    private val lockedPaint = Paint().apply {
        color = Color.LTGRAY
        style = Paint.Style.FILL
        isAntiAlias = true
        alpha = 160
        setShadowLayer(8f, 4f, 4f, Color.BLACK)
    }

    private val linePaint = Paint().apply {
        color = Color.LTGRAY
        strokeWidth = 10f
        isAntiAlias = true
    }

    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 50f
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
        setShadowLayer(4f, 2f, 2f, Color.BLACK)
    }

    /**
     * Callback invoked when a level is clicked.
     */
    var onLevelClicked: ((level: Int) -> Unit)? = null

    /**
     * Calculates height based on number of levels and vertical spacing.
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val spacing = 220
        val desiredHeight = (levelCount * spacing + 200).toInt()
        val width = MeasureSpec.getSize(widthMeasureSpec)
        setMeasuredDimension(width, desiredHeight)
    }

    /**
     * Called when the view size changes; regenerates node positions.
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        generateNodePositions()
    }

    /**
     * Generates vertical, slightly randomized positions for level nodes.
     */
    private fun generateNodePositions() {
        nodePositions.clear()

        val verticalSpacing = 200f
        val horizontalPadding = 100f
        val usableWidth = width - 2 * horizontalPadding

        for (i in 0 until levelCount) {
            val xRandomOffset = (Math.sin(i * 0.7) * 0.5 + 0.5).toFloat() // bølgete
            val x = horizontalPadding + xRandomOffset * usableWidth

            val y = height - 200f - i * verticalSpacing
            nodePositions.add(Vector2f(x, y))
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawConnectionLines(canvas)
        drawLevelNodes(canvas)
    }

    /**
     * Draws lines connecting each level node to the next.
     */
    private fun drawConnectionLines(canvas: Canvas) {
        for (i in 0 until nodePositions.size - 1) {
            val start = nodePositions[i]
            val end = nodePositions[i + 1]
            canvas.drawLine(start.x, start.y, end.x, end.y, linePaint)
        }
    }

    /**
     * Draws each level node as a circle with a number and optional lock icon.
     */
    private fun drawLevelNodes(canvas: Canvas) {
        nodePositions.forEachIndexed { index, pos ->
            drawLevelCircle(canvas, index, pos)
            drawLevelNumber(canvas, index, pos)
            if (index >= unlockedLevels) drawLockIcon(canvas, pos)
        }
    }

    /**
     * Draws a single level circle, using unlocked or locked paint.
     */
    private fun drawLevelCircle(canvas: Canvas, index: Int, pos: Vector2f) {
        val paint = if (index < unlockedLevels) unlockedPaint else lockedPaint
        canvas.drawCircle(pos.x, pos.y, circleRadius, paint)
    }

    /**
     * Draws the level number at the center of the circle.
     */
    private fun drawLevelNumber(canvas: Canvas, index: Int, pos: Vector2f) {
        val levelNumber = (index + 1).toString()
        canvas.drawText(levelNumber, pos.x, pos.y + 15f, textPaint)
    }

    /**
     * Draws a lock icon slightly offset from the center of a locked node.
     */
    private fun drawLockIcon(canvas: Canvas, pos: Vector2f) {
        val iconSize = 90
        val offset = (circleRadius * 0.6f).toInt()

        val left = (pos.x + offset - iconSize / 2).toInt()
        val top = (pos.y + offset - iconSize / 2).toInt()
        val right = left + iconSize
        val bottom = top + iconSize

        lockDrawable?.setBounds(left, top, right, bottom)
        lockDrawable?.draw(canvas)
    }

    private val lockDrawable: Drawable? by lazy {
        AppCompatResources.getDrawable(context, R.drawable.ic_lock_key)
    }

    /**
     * Detects taps on level nodes and invokes the click callback if unlocked.
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            for ((index, pos) in nodePositions.withIndex()) {
                val dx = event.x - pos.x
                val dy = event.y - pos.y
                if (dx * dx + dy * dy <= circleRadius * circleRadius) {
                    if (index < unlockedLevels) {
                        onLevelClicked?.invoke(index + 1)
                    }
                    return true
                }
            }
        }
        return super.onTouchEvent(event)
    }

    fun simulateNodeClick(level: Int) {
        val index = level - 1
        if (index in 0 until nodePositions.size && index < unlockedLevels) {
            onLevelClicked?.invoke(level)
        }
    }
}
