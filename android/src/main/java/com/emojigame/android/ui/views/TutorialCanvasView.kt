package com.emojigame.android.ui.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.emojigame.android.ui.ViewCategory
import com.emojigame.core.data.tutorialSteps.TutorialGuide
import com.emojigame.core.data.tutorialSteps.TutorialStep

/**
 * A custom [View] used to visually guide the player during tutorial steps.
 *
 * This view overlays the UI and animates a pointer (either a bitmap or circle)
 * toward a UI target specified by the current [TutorialStep].
 *
 * Features:
 * - Resolves the UI element to highlight based on a provided [TutorialStep].
 * - Smoothly animates a visual pointer toward the target.
 * - Can use a custom pointer bitmap (e.g., a hand icon).
 * - Uses a [ViewCategory]-based resolver function to find matching UI elements.
 *
 * Usage:
 * - Call [setViewResolver] to provide a way to resolve views by ID and category.
 * - Call [updateState] with a new [TutorialStep] to update the pointer target.
 * - Optionally set [pointerBitmap] for a custom image (fallback is a red circle).
 *
 * Example integration is done inside [TutorialOverlayFragment].
 */
class TutorialCanvasView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        style = Paint.Style.FILL
    }

    private val bitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val screenPos = IntArray(2)

    private var viewResolver: ((String, ViewCategory) -> View?)? = null
    private var tutorialStep: TutorialStep? = null

    private var animationStartTime = 0L
    private val animationDuration = 1200L
    private val animationCooldown = 500L
    private val totalCycle = animationDuration + animationCooldown

    var pointerBitmap: Bitmap? = null

    /** Sets the view resolver for finding UI elements from identifiers. */
    fun setViewResolver(resolver: (String, ViewCategory) -> View?) {
        viewResolver = resolver
    }

    /** Updates the current tutorial step and starts the animation. */
    fun updateState(currentStep: TutorialStep? = null) {
        this.tutorialStep = currentStep
        animationStartTime = System.currentTimeMillis()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val view = resolveTargetView() ?: return
        val (canvasX, canvasY) = calculateTargetPositionOnCanvas(view)

        if (!shouldDraw()) {
            postInvalidateOnAnimation()
            return
        }

        val (alpha, offset) = calculateAnimationFrame()
        bitmapPaint.alpha = alpha
        paint.alpha = alpha

        drawPointer(canvas, canvasX + offset, canvasY + offset)
        postInvalidateOnAnimation()
    }

    /**
     * Resolves the view to target based on the current tutorial step.
     */
    private fun resolveTargetView(): View? {
        val guide = tutorialStep?.guide ?: return null
        return when (guide) {
            is TutorialGuide.TapButton -> viewResolver?.invoke(guide.buttonId, ViewCategory.Special)
            is TutorialGuide.TapSelectedEmoji -> viewResolver?.invoke(guide.emoji.tag, ViewCategory.SelectedEmoji)
            is TutorialGuide.TapAvailableEmoji -> viewResolver?.invoke(guide.emoji.tag, ViewCategory.AvailableEmoji)
            else -> null
        }
    }

    /**
     * Calculates the (x, y) position on the canvas where the pointer should appear.
     */
    private fun calculateTargetPositionOnCanvas(view: View): Pair<Float, Float> {
        view.getLocationOnScreen(screenPos)
        val targetX = screenPos[0] + view.width / 2f
        val targetY = screenPos[1] + view.height / 2f

        getLocationOnScreen(screenPos)
        val canvasX = targetX - screenPos[0]
        val canvasY = targetY - screenPos[1]

        return canvasX to canvasY
    }

    /**
     * Determines if the current animation cycle is within the active frame duration.
     */
    private fun shouldDraw(): Boolean {
        val elapsed = System.currentTimeMillis() - animationStartTime
        return (elapsed % totalCycle) <= animationDuration
    }

    /**
     * Calculates the animation frame's alpha and offset based on elapsed time.
     */
    private fun calculateAnimationFrame(): Pair<Int, Float> {
        val elapsed = System.currentTimeMillis() - animationStartTime
        val progress = (elapsed % totalCycle).toFloat() / animationDuration
        val alpha = (255 * progress).toInt()
        val offset = 30f * (1f - progress)
        return alpha to offset
    }

    /**
     * Draws either a pointer bitmap or fallback circle at the given position.
     */
    private fun drawPointer(canvas: Canvas, x: Float, y: Float) {
        pointerBitmap?.let { bitmap ->
            val fingerTipOffsetX = 80f
            val fingerTipOffsetY = 100f
            val left = x - fingerTipOffsetX
            val top = y - fingerTipOffsetY
            canvas.drawBitmap(bitmap, left, top, bitmapPaint)
        } ?: run {
            canvas.drawCircle(x, y, 60f, paint)
        }
    }
}
