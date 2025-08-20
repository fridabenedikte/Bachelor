package com.emojigame.android.util

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.min

/**
 * Utility object for applying consistent padding and margin behavior to views and layouts.
 * Uses values from [DeviceSizeData] to maintain a unified spacing approach across the UI.
 */
object PaddingUtil {
    private val width = DeviceSizeData.portraitInputFragmentWidth
    private val height = DeviceSizeData.portraitInputFragmentHeight

    /**
     * Calculates a device-appropriate padding value based on a percentage of the screen size.
     *
     * @param sizePercentage A percentage of the screen size (default is 5%).
     * @return The calculated padding value in pixels.
     */
    fun calculatePadding(sizePercentage: Float = 0.05f): Int {
        val horizontalPadding: Int = (height * sizePercentage).toInt()
        val verticalPadding: Int = (width * sizePercentage).toInt()
        return min(horizontalPadding, verticalPadding)
    }

    /**
     * Applies padding to a view, optionally considering system bar insets.
     *
     * @param view The view to apply padding to.
     * @param padding The base padding to use (default: [DeviceSizeData.universalPadding]).
     * @param touching* Booleans for edges that touch system bars and should adjust for insets.
     * @param ignore* Booleans for edges where padding should be skipped altogether.
     */
    fun applyPaddingToView(
        view: View,
        padding: Int = DeviceSizeData.universalPadding,
        touchingLeft: Boolean = false,
        touchingTop: Boolean = false,
        touchingRight: Boolean = false,
        touchingBottom: Boolean = false,
        ignoreLeft: Boolean = false,
        ignoreTop: Boolean = false,
        ignoreRight: Boolean = false,
        ignoreBottom: Boolean = false
    ) {

        if (!touchingLeft && !touchingTop && !touchingRight && !touchingBottom) {
            view.setPadding(padding, padding, padding, padding)
            return
        }

        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val leftPadding = if (!ignoreLeft) {
                padding
            } else 0
            val topPadding = if (!ignoreTop) {
                padding
            } else 0
            val rightPadding = if (!ignoreRight) {
                padding
            } else 0
            val bottomPadding = if (!ignoreBottom) {
                padding
            } else 0
            v.setPadding(
                leftPadding,
                topPadding,
                rightPadding,
                bottomPadding
            )
            insets
        }
    }

    /**
     * Applies uniform vertical or horizontal padding between children of a [LinearLayout].
     * Only sets spacing between views, leaving the outermost edges untouched unless first/last.
     *
     * @param linearLayout The container to adjust.
     * @param padding The margin value to apply between children (default: [DeviceSizeData.universalPadding]).
     */
    fun applyUniformPaddingToChildren(
        linearLayout: LinearLayout,
        padding: Int = DeviceSizeData.universalPadding
    ) {
        for (i in 0 until linearLayout.childCount) {
            val child = linearLayout.getChildAt(i)
            val layoutParams = child.layoutParams as ViewGroup.MarginLayoutParams
            if (linearLayout.orientation == LinearLayout.VERTICAL) {
                when (i) {
                    0 -> layoutParams.setMargins(0, 0, 0, padding / 2)
                    linearLayout.childCount - 1 -> layoutParams.setMargins(0, padding / 2, 0, 0)
                    else -> layoutParams.setMargins(0, padding / 2, 0, padding / 2)
                }
            }
            if (linearLayout.orientation == LinearLayout.HORIZONTAL) {
                when (i) {
                    0 -> layoutParams.setMargins(0, 0, padding / 2, 0)
                    linearLayout.childCount - 1 -> layoutParams.setMargins(padding / 2, 0, 0, 0)
                    else -> layoutParams.setMargins(padding / 2, 0, padding / 2, 0)
                }
            }
        }
    }
}
