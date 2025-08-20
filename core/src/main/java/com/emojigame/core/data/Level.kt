package com.emojigame.core.data

import com.emojigame.core.entities.Emoji
import com.emojigame.core.entities.Player
import com.emojigame.core.ui.InputFieldType

/**
 * Represents a complete level definition in the game.
 *
 * A level contains all the data necessary for initializing gameplay,
 * including its platforms, background visuals, associated emojis, the player,
 * and any logic components (such as triggers or gravity).
 *
 * @property name The unique identifier for this level (used for lookup and loading).
 * @property emojis A list of [Emoji] instances available in this level.
 * @property player The initial [Player] object with starting position and properties.
 * @property platforms A list of [Platform]s that define the interactive or collidable surfaces.
 * @property backgroundElements A list of [BackgroundElement]s that serve visual or decorative purposes.
 * @property components A list of [Component]s defining logic, interactions, and behaviors.
 * @property hints A list of emoji tags representing the correct solution order for this level.
 */
data class Level(
    val name: String,
    val ifOrElse: List<Boolean> = emptyList(),
    val emojis: List<Emoji>,
    val player: Player,
    val platforms: List<Platform>,
    val backgroundElements: List<BackgroundElement>,
    val components: List<Component>,
    val type: String? = null,
    val hints: List<Hint>,
    val inputFieldType: InputFieldType,
    var tutorial: Tutorial? = null,
    val ifElsePhotos: List<String> = emptyList()
)
