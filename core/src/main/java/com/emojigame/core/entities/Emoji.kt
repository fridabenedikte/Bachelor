package com.emojigame.core.entities

import com.emojigame.core.util.Vector2f
import java.util.UUID

/**
 * Represents an emoji entity in the game.
 *
 * @property pos The position of the entity (nullable for now).
 * @property tag The tag of the entity (formerly name).
 * @property category The category of the emoji (nullable).
 * @property description A description of the emoji (nullable).
 */
data class Emoji(
    override val tag: String,
    override val pos: Vector2f = Vector2f(0f, 0f),
    var id: String? = "",
    val category: String? = null,
    val description: String? = null,
    override val width: Float = 32f,
    override val height: Float = 32f,
) : Entity() {
    public fun assignEmojiId(): Emoji {
        val uniqueId = UUID.randomUUID().toString()
        return this.copy(id = uniqueId)
    }

    override fun toString(): String {
        return "Emoji - tag: $tag"
    }
}
