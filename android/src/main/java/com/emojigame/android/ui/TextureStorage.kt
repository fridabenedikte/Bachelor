package com.emojigame.android.ui

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.createBitmap
import com.emojigame.core.entities.Emoji

/**
 * A singleton utility that stores and provides access to commonly used bitmaps in the game,
 * such as the character sprite, emoji sprite sheet, and texture mapping for emojis and players.
 *
 * It serves as a centralized texture manager to retrieve pre-rendered or dynamically sliced
 * sprite assets used in rendering the game UI.
 */
object TextureStorage {
    /** Bitmap representing the current player sprite sheet, usually loaded at runtime. */
    var characterBitmap: Bitmap? = null

    /** Bitmap sprite sheet containing all emojis. */
    var emojiSpriteSheet: Bitmap? = null

    /** Size (in pixels) of a single emoji sprite in the sheet. */
    private const val SPRITE_SIZE_EMOJIS = 160

    /** Number of emoji columns in the sprite sheet. */
    private const val SPRITE_COLUMNS_EMOJIS = 16

    /**
     * Retrieves the bitmap for a specific [Emoji] from the emoji sprite sheet.
     *
     * @param context Android context used for resource mapping.
     * @param spriteSize Size of each sprite in pixels (default = 160).
     * @param columns Number of columns in the sprite sheet (default = 16).
     * @param emoji The [Emoji] object for which to retrieve the bitmap.
     * @return The bitmap corresponding to the emoji, or null if not found or not initialized.
     */
    fun getEmojiBitmap(
        context: Context,
        spriteSize: Int = SPRITE_SIZE_EMOJIS,
        columns: Int = SPRITE_COLUMNS_EMOJIS,
        emoji: Emoji?
    ): Bitmap? {
        val sheet = emojiSpriteSheet ?: return null
        val mapper = AndroidTextureMapper(context, sheet, spriteSize, columns)
        return mapper.getBitmapByEmoji(emoji)
    }

    /**
     * Retrieves the bitmap for the player from the emoji sprite sheet.
     *
     * @param context Android context used for resource mapping.
     * @param spriteSize Size of each sprite in pixels (default = 160).
     * @param columns Number of columns in the sprite sheet (default = 16).
     * @return The bitmap representing the player, or null if the sprite sheet is not initialized.
     */
    fun getPlayerBitmap(
        context: Context,
        spriteSize: Int = SPRITE_SIZE_EMOJIS,
        columns: Int = SPRITE_COLUMNS_EMOJIS
    ): Bitmap? {
        val sheet = emojiSpriteSheet ?: return null
        val mapper = AndroidTextureMapper(context, sheet, spriteSize, columns)
        return mapper.getBitmapByPlayer()
    }

    /**
     * Retrieves a bitmap based on a string identifier (name).
     * This typically maps to a special image not found in the sprite sheet.
     *
     * @param context Android context used to initialize a dummy mapper.
     * @param name The string name of the resource to retrieve.
     * @return The bitmap associated with the given name, or null if not found.
     */
    fun getNamedBitmap(
        context: Context,
        name: String
    ): Bitmap? {
        val dummy = createBitmap(1, 1)
        val mapper = AndroidTextureMapper(context, dummy, 1, 1)
        return mapper.getBitmapByName(name)
    }
}
