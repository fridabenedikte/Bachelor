package com.emojigame.android.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.emojigame.core.entities.Emoji

/**
 * Responsible for mapping game entities (emojis, player, or named assets)
 * to their corresponding bitmap graphics.
 *
 * This class supports both sprite-sheet-based lookup and named resource lookup.
 *
 * @param context Android context for accessing resources.
 * @param spriteSheet Bitmap representing the full sprite sheet.
 * @param spriteSize Size (width and height) of each square sprite in pixels.
 * @param columns Number of columns in the sprite sheet.
 */
class AndroidTextureMapper(
    private val context: Context,
    private val spriteSheet: Bitmap,
    private val spriteSize: Int,
    private val columns: Int,
) {
    private val spriteMap =
        mapOf(
            "Question" to 0,
            "Fire" to 1,
            "Crying" to 2,
            "Heart" to 3,
            "Wood" to 4,
            "Hand" to 5,
            "Apple" to 6,
            "Umbrella" to 7,
            "Sunglasses" to 8,
            "Bone" to 9,
            "Banana" to 10,
            "Walking" to 11,
            "Standing" to 12,
            "Fishing" to 13,
            "Rowing" to 14,
            "Ladder" to 15
        )

    /**
     * Returns the sprite bitmap for a given emoji.
     * Falls back to the "Question" sprite if unknown.
     *
     * @param emoji The emoji to retrieve.
     * @return A square [Bitmap] representing the emoji.
     */
    fun getBitmapByEmoji(emoji: Emoji?): Bitmap {
        val index = spriteMap[emoji?.tag] ?: 0
        val x = (index % columns) * spriteSize
        val y = (index / columns) * spriteSize
        return Bitmap.createBitmap(spriteSheet, x, y, spriteSize, spriteSize)
    }

    /**
     * Returns the default player sprite.
     *
     * @return The player’s [Bitmap] graphic.
     */
    fun getBitmapByPlayer(): Bitmap {
        return Bitmap.createBitmap(spriteSheet, 0, 0, spriteSize, spriteSize)
    }

    /**
     * Returns a bitmap based on its drawable name.
     * Should eventually be replaced with explicit mapping for performance and stability.
     *
     * @param name The drawable name (without extension or prefix).
     * @return A decoded [Bitmap] if found, or null if not.
     */
    @Suppress("DiscouragedAPI") // TODO: Switch to explicit mapping to avoid reflection
    fun getBitmapByName(name: String): Bitmap? {
        val resId = context.resources.getIdentifier(name, "drawable", context.packageName)
        return if (resId != 0) {
            BitmapFactory.decodeResource(context.resources, resId)
        } else {
            null
        }
    }
}
