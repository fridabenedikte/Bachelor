package com.emojigame.android.util

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.graphics.toColorInt
import com.emojigame.core.data.BackgroundElement
import com.emojigame.core.data.Platform
import com.emojigame.core.data.PlatformTags
import com.emojigame.core.data.VisualStyle
import com.emojigame.core.util.GameIntent
import com.emojigame.core.util.Vector2f

/**
 * Parses XML layout resources into virtual game data (platforms and background elements).
 *
 * Used to overload levels dynamically by scanning layout tags for known structure.
 */
object XmlLevelParser {

    /**
     * Internal structure to represent parsed view tags.
     *
     * @param type The component type (e.g., platform, background).
     * @param tag Semantic tag (e.g., ground, bridge).
     * @param isEnabled Whether the component is enabled.
     * @param visualHintOrId Optional color hex, image reference, or manual ID.
     */
    private data class ParsedTag(
        val type: String,
        val tag: String,
        val isEnabled: Boolean,
        val visualHintOrId: String? = null,
    )

    /**
     * Parses a tag string into a [ParsedTag] object.
     * Expected format: `type:tag:enabled[:visualHintOrId]`
     */
    private fun parseTag(rawTag: String?): ParsedTag? {
        val parts = rawTag?.split(":") ?: return null
        if (parts.size < 3) return null

        return ParsedTag(
            type = parts[0].trim().lowercase(),
            tag = parts[1].trim().lowercase(),
            isEnabled = parts[2].trim().equals("enabled", ignoreCase = true),
            visualHintOrId = parts.getOrNull(3)?.trim(),
        )
    }

    /**
     * Parses an XML layout and converts it to a [GameIntent.OverloadLevel].
     *
     * @param context Context used to inflate the layout.
     * @param levelName The level name to associate with the overload.
     * @param layoutResId The resource ID of the XML layout to parse.
     * @return An intent that instructs the game engine to overload level elements.
     */
    fun parse(
        context: Context,
        levelName: String,
        layoutResId: Int,
    ): GameIntent.OverloadLevel {
        val inflater = LayoutInflater.from(context)
        val dummyParent = FrameLayout(context)
        val rootView = inflater.inflate(layoutResId, dummyParent, false) as ViewGroup

        rootView.measure(
            View.MeasureSpec.makeMeasureSpec(1000, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(1000, View.MeasureSpec.EXACTLY)
        )
        rootView.layout(0, 0, rootView.measuredWidth, rootView.measuredHeight)

        val screenWidth = rootView.width.toFloat()
        val screenHeight = rootView.height.toFloat()

        val platforms = mutableListOf<Platform>()
        val backgroundElements = mutableListOf<BackgroundElement>()

        for (i in 0 until rootView.childCount) {
            val child = rootView.getChildAt(i)
            val tag = parseTag(child.tag?.toString()) ?: continue

            if (child.width == 0 || child.height == 0) {
                Log.w("XmlLevelParser", "⚠️ View ${child.id} has zero size, skipping.")
                continue
            }

            val pos = Vector2f(
                x = (child.x / screenWidth) * 1000f,
                y = (child.y / screenHeight) * 1000f
            )
            val width = (child.width / screenWidth) * 1000f
            val height = (child.height / screenHeight) * 1000f
            val tagEnum = PlatformTags.from(tag.tag)

            val id = tag.visualHintOrId ?: "${tagEnum.tag}-$i"

            val visualStyle = when {
                tag.visualHintOrId?.startsWith("#") == true ->
                    VisualStyle(color = tag.visualHintOrId.toColorInt())

                tag.visualHintOrId?.startsWith("drawable/") == true ->
                    VisualStyle(imageTag = tag.visualHintOrId.removePrefix("drawable/"))

                else ->
                    VisualStyle(color = (child.background as? ColorDrawable)?.color)
            }

            when (tag.type) {
                "platform" -> platforms.add(
                    Platform(
                        id = id,
                        pos = pos,
                        width = width,
                        height = height,
                        tag = tagEnum,
                        isEnabled = tag.isEnabled,
                        visualStyle = visualStyle
                    )
                )

                "background" -> backgroundElements.add(
                    BackgroundElement(
                        pos = pos,
                        width = width,
                        height = height,
                        tag = tagEnum,
                        isEnabled = tag.isEnabled,
                        visualStyle = visualStyle
                    )
                )
            }
        }

        return GameIntent.OverloadLevel(
            levelName = levelName,
            platforms = platforms,
            backgroundElements = backgroundElements
        )
    }
}
