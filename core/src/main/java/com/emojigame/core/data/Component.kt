package com.emojigame.core.data

import com.emojigame.core.entities.Emoji
import com.emojigame.core.util.Vector2f

/**
 * Represents a logical or physical component attached to a level.
 * Components define dynamic interactions between emojis and platforms,
 * such as activation, removal, gravity, or conditional logic.
 */
sealed class Component {
    /**
     * When [emoji] is used, it activates the platform with the given [platformId].
     */
    data class EmojiActivatesPlatform(
        val emoji: Emoji,
        val platformId: String,
    ) : Component()

    /**
     * When all [requiredEmojis] are present, the platform with [platformId] is removed.
     */
    data class EmojiRemovesPlatform(
        val requiredEmojis: List<Emoji>,
        val platformId: String,
    ) : Component()

    /**
     * When the player inputs the exact [emojiSequence], the platform with [platformId] is removed.
     */
    data class EmojiSequenceRemovesPlatform(
        val emojiSequence: List<Emoji>,
        val platformId: String,
    ) : Component()

    /**
     * Applies gravity to all platforms with matching tags in [affectedTags].
     * The default gravity vector is (0, 9.8).
     */
    data class Gravity(
        val affectedTags: List<String>,
        val gravity: Vector2f = Vector2f(0f, 9.8f),
    ) : Component()

    /**
     * Used for "If/Else" logic: Activates a platform based on whether the specified [index] evaluates true.
     */
    data class IfOrElseActivatesPlatform(
        val index: Int,
        val platformId: String,
    ) : Component()

    /**
     * Evaluates conditional logic based on the emoji selected in an "If/Else" block.
     *
     * If the emoji at [ifTrueEmojiIndex] with [ifTrueEmojiTag] is selected, then
     * the platform [platformId] is [shouldEnable].
     *
     * If the emoji at [ifFalseEmojiIndex] with [ifFalseEmojiTag] is selected, the opposite applies.
     */
    data class IfOrElseEmojiSetsPlatform(
        val ifOrElseIndex: Int,
        val ifTrueEmojiTag: String,
        val ifTrueEmojiIndex: Int,
        val ifFalseEmojiTag: String,
        val ifFalseEmojiIndex: Int,
        val platformId: String,
        val shouldEnable: Boolean
    ) : Component()
}
