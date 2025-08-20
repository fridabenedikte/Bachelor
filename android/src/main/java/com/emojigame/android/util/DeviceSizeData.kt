package com.emojigame.android.util

import android.content.Context
import android.content.res.Configuration
import com.emojigame.core.util.Logger
import kotlin.math.max
import kotlin.math.min

/**
 * Singleton object used to calculate and store all dimension-related values for the game’s UI.
 *
 * These values are calculated based on screen size, orientation, and navigation mode.
 * Only used within the Android module.
 */
object DeviceSizeData {
    // --- General screen dimensions ---
    @JvmStatic var minorDimension: Int = 0
    @JvmStatic var majorDimension: Int = 0
    @JvmStatic var isGestureModeEnabled: Boolean = true

    // --- UI portion definitions ---
    @JvmStatic val inputFragmentHeightPortion: Float = 0.4f
    @JvmStatic val puzzleFragmentHeightPortion: Float = 0.6f
    @JvmStatic val inputFragmentWidthPortion: Float = 0.4f
    @JvmStatic val puzzleFragmentWidthPortion: Float = 0.6f
    @JvmStatic val inputSwitchIconPortionOfFragment: Float = 0.1f

    // --- Padding and bar settings ---
    @JvmStatic var universalPadding: Int = 48
    @JvmStatic val inputBarPadding: Int = 8
    @JvmStatic val emojisToFitInInputBars: Int = 4

    // --- Insets ---
    @JvmStatic var topBarSize: Int = 0
    @JvmStatic var leftBarSize: Int = 0
    @JvmStatic var rightBarSize: Int = 0
    @JvmStatic var bottomBarSize: Int = 0
    @JvmStatic var navBarSize: Int = 0

    // --- Input bar sizes ---
    @JvmStatic var portraitWidthOfInputBars: Int = 0
    @JvmStatic var portraitHeightOfInputBars: Int = 0
    @JvmStatic var landscapeWidthOfInputBars: Int = 0
    @JvmStatic var landscapeHeightOfInputBars: Int = 0

    // --- Input wheel ---
    @JvmStatic val inputWheelCenterPortion: Float = 0.3f
    @JvmStatic val inputWheelFieldPortion: Float = 0.3f

    // --- Portrait fragment sizes ---
    @JvmStatic var portraitInputFragmentWidth: Int = 0
    @JvmStatic var portraitInputFragmentHeight: Int = 0
    @JvmStatic var portraitPuzzleFragmentWidth: Int = 0
    @JvmStatic var portraitPuzzleFragmentHeight: Int = 0
    @JvmStatic var portraitInputSwitchFragmentWidth: Int = 0
    @JvmStatic var portraitInputSwitchFragmentHeight: Int = 0
    @JvmStatic var portraitInputSwitchIconDiameter: Int = 0

    // --- Landscape fragment sizes ---
    @JvmStatic var landscapeInputFragmentWidth: Int = 0
    @JvmStatic var landscapeInputFragmentHeight: Int = 0
    @JvmStatic var landscapePuzzleFragmentWidth: Int = 0
    @JvmStatic var landscapePuzzleFragmentHeight: Int = 0
    @JvmStatic var landscapeInputSwitchFragmentWidth: Int = 0
    @JvmStatic var landscapeInputSwitchFragmentHeight: Int = 0
    @JvmStatic var landscapeInputSwitchIconDiameter: Int = 0

    @JvmStatic var playButtonHeight: Int = 0

    /**
     * Determines whether the current device is a tablet based on smallest width in dp.
     *
     * @param context The application context.
     * @return True if device is a tablet.
     */
    fun isTablet(context: Context): Boolean {
        val configuration = context.resources.configuration
        return configuration.smallestScreenWidthDp >= 600
    }

    /**
     * Calculates and stores layout dimensions for different UI components
     * based on the provided screen and inset measurements.
     *
     * @param width Screen width in pixels
     * @param height Screen height in pixels
     * @param insetLeft System window inset left
     * @param insetTop System window inset top
     * @param insetRight System window inset right
     * @param insetBottom System window inset bottom
     * @param orientation Current orientation from Configuration
     * @param isTablet Whether device is a tablet
     * @param isGestureNav Whether gesture navigation is enabled
     */
    fun calculateDimensions(
        width: Int,
        height: Int,
        insetLeft: Int,
        insetTop: Int,
        insetRight: Int,
        insetBottom: Int,
        orientation: Int,
        isTablet: Boolean = false,
        isGestureNav: Boolean = false
    ) {
        val minorDimension = min(width, height)
        val majorDimension = max(width, height)
        val dsd = DeviceSizeData

        dsd.minorDimension = minorDimension
        dsd.majorDimension = majorDimension

        val (usablePortraitHeight, usablePortraitWidth, usableLandscapeHeight, usableLandscapeWidth) =
            calculateUsableSizes(
                minorDimension, majorDimension, insetLeft, insetTop, insetRight,
                insetBottom, orientation, isTablet, isGestureNav
            )

        assignFragmentSizes(usablePortraitHeight, usablePortraitWidth, usableLandscapeHeight, usableLandscapeWidth, isTablet, insetBottom)
        calculateInputBarSizes()
    }

    /**
     * Calculates the usable height and width for both portrait and landscape modes.
     */
    private fun calculateUsableSizes(
        minorDimension: Int,
        majorDimension: Int,
        insetLeft: Int,
        insetTop: Int,
        insetRight: Int,
        insetBottom: Int,
        orientation: Int,
        isTablet: Boolean,
        isGestureNav: Boolean
    ): Quadruple<Int, Int, Int, Int> {
        val usablePortraitHeight: Int
        val usablePortraitWidth: Int
        val usableLandscapeHeight: Int
        val usableLandscapeWidth: Int

        if (!isTablet) {
            Logger.i { "Phone detected, calculating inset" }
            when (orientation) {
                Configuration.ORIENTATION_PORTRAIT -> {
                    usablePortraitHeight = majorDimension - insetTop - insetBottom
                    usablePortraitWidth = minorDimension
                    if (isGestureNav) {
                        usableLandscapeHeight = minorDimension - insetTop - insetBottom
                        usableLandscapeWidth = majorDimension
                    } else {
                        usableLandscapeHeight = minorDimension - insetTop
                        usableLandscapeWidth = majorDimension - insetBottom
                    }
                }
                Configuration.ORIENTATION_LANDSCAPE -> {
                    if (isGestureNav) {
                        usablePortraitHeight = majorDimension - insetTop - insetBottom
                        usablePortraitWidth = minorDimension
                        usableLandscapeHeight = minorDimension - insetTop - insetBottom
                        usableLandscapeWidth = majorDimension
                    } else {
                        usablePortraitHeight = majorDimension - insetTop - insetRight - insetLeft
                        usablePortraitWidth = minorDimension
                        usableLandscapeHeight = minorDimension - insetTop
                        usableLandscapeWidth = majorDimension - insetLeft - insetRight
                    }
                }
                else -> {
                    return Quadruple(0, 0, 0, 0)
                }
            }
        } else {
            Logger.i { "Tablet detected, calculating inset" }
            usablePortraitHeight = majorDimension - insetTop - insetBottom
            usablePortraitWidth = minorDimension
            usableLandscapeHeight = minorDimension - insetTop - insetBottom
            usableLandscapeWidth = majorDimension
        }

        return Quadruple(usablePortraitHeight, usablePortraitWidth, usableLandscapeHeight, usableLandscapeWidth)
    }

    /**
     * Assigns sizes for the input, puzzle, and switcher fragments based on screen orientation.
     */
    private fun assignFragmentSizes(
        usablePortraitHeight: Int,
        usablePortraitWidth: Int,
        usableLandscapeHeight: Int,
        usableLandscapeWidth: Int,
        isTablet: Boolean,
        insetBottom: Int
    ) {
        val dsd = DeviceSizeData

        // Portrait layout
        val portraitInputFragmentHeight = (usablePortraitHeight * dsd.inputFragmentHeightPortion).toInt()
        val portraitPuzzleFragmentHeight = (usablePortraitHeight * dsd.puzzleFragmentHeightPortion).toInt()
        val portraitInputSwitchFragmentHeight = (portraitPuzzleFragmentHeight * dsd.inputSwitchIconPortionOfFragment).toInt()

        dsd.portraitInputFragmentWidth = usablePortraitWidth
        dsd.portraitInputFragmentHeight = portraitInputFragmentHeight
        dsd.portraitPuzzleFragmentWidth = usablePortraitWidth
        dsd.portraitPuzzleFragmentHeight = portraitPuzzleFragmentHeight
        dsd.portraitInputSwitchFragmentWidth = usablePortraitWidth
        dsd.portraitInputSwitchFragmentHeight = portraitInputSwitchFragmentHeight

        // Landscape layout
        val landscapeInputFragmentWidth = (usableLandscapeWidth * dsd.inputFragmentWidthPortion).toInt()
        val landscapePuzzleFragmentWidth = (usableLandscapeWidth * dsd.puzzleFragmentWidthPortion).toInt()
        val landscapeInputSwitchFragmentWidth = (landscapePuzzleFragmentWidth * dsd.inputSwitchIconPortionOfFragment).toInt()

        dsd.landscapeInputFragmentWidth = landscapeInputFragmentWidth
        dsd.landscapeInputFragmentHeight = usableLandscapeHeight
        dsd.landscapePuzzleFragmentWidth = landscapePuzzleFragmentWidth
        dsd.landscapePuzzleFragmentHeight = usableLandscapeHeight + if (isTablet) insetBottom else 0
        dsd.landscapeInputSwitchFragmentWidth = landscapeInputSwitchFragmentWidth
        dsd.landscapeInputSwitchFragmentHeight = usableLandscapeHeight

        // Input switch icons
        dsd.portraitInputSwitchIconDiameter = portraitInputSwitchFragmentHeight
        dsd.landscapeInputSwitchIconDiameter = landscapeInputSwitchFragmentWidth

        dsd.universalPadding = min(dsd.universalPadding, PaddingUtil.calculatePadding())
    }

    /**
     * Calculates the input bar height and width based on available width and emoji count.
     */
    private fun calculateInputBarSizes() {
        val dsd = DeviceSizeData
        val emojiCount = dsd.emojisToFitInInputBars
        val portraitBarWidth = dsd.portraitInputFragmentWidth - (2 * dsd.universalPadding)
        val landscapeBarWidth = dsd.landscapeInputFragmentWidth - (2 * dsd.universalPadding)

        dsd.portraitWidthOfInputBars = portraitBarWidth
        dsd.landscapeWidthOfInputBars = landscapeBarWidth

        var portraitMaxEmojiSize = (portraitBarWidth - emojiCount * dsd.inputBarPadding * 2) / emojiCount
        var landscapeMaxEmojiSize = (landscapeBarWidth - emojiCount * dsd.inputBarPadding * 2) / emojiCount

        val maxInputBarPortion = 3.5f
        if (portraitMaxEmojiSize > dsd.portraitInputFragmentHeight / maxInputBarPortion)
            portraitMaxEmojiSize = (dsd.portraitInputFragmentHeight / maxInputBarPortion).toInt()
        if (landscapeMaxEmojiSize > dsd.portraitInputFragmentHeight / maxInputBarPortion)
            landscapeMaxEmojiSize = (dsd.portraitInputFragmentHeight / maxInputBarPortion).toInt()

        dsd.portraitHeightOfInputBars = portraitMaxEmojiSize + 2 * dsd.inputBarPadding
        dsd.landscapeHeightOfInputBars = landscapeMaxEmojiSize + 2 * dsd.inputBarPadding
    }

    /**
     * Return 4 values from a function since Kotlin lacks a native Quadruple.
     */
    private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
}
