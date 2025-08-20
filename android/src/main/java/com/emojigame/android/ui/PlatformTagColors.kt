package com.emojigame.android.ui

import android.graphics.Color
import androidx.core.graphics.toColorInt
import com.emojigame.core.data.PlatformTags

/**
 * Maps [PlatformTags] to color values used for drawing platforms and background elements.
 */
object PlatformTagColors {
    private val colorMap =
        mapOf(
            PlatformTags.Ground to "#136d15".toColorInt(),
            PlatformTags.Bridge to "#A1662F".toColorInt(),
            PlatformTags.Water to "#184d72".toColorInt(),
            PlatformTags.Unknown to Color.GRAY,
        )

    /**
     * Returns the color associated with a given [PlatformTags].
     *
     * @param tag The tag to look up.
     * @return The color as an [Int] (ARGB).
     */
    fun getColorFor(tag: PlatformTags): Int {
        return colorMap[tag] ?: Color.GRAY
    }
}
