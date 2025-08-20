package com.emojigame.core.data

import com.emojigame.core.data.tutorialSteps.TutorialStep
import com.emojigame.core.entities.Emoji
import com.emojigame.core.entities.Player
import com.emojigame.core.ui.InputFieldType

/**
 * Represents the full definition of a tutorial level.
 *
 * @property name The internal name or identifier for the tutorial.
 * @property emojis The list of emojis available to the player in this tutorial.
 * @property player The player starting state and properties.
 * @property platforms The platforms included in the tutorial level.
 * @property backgroundElements Background visual elements present in the tutorial.
 * @property components Game components like logic, triggers, and gravity.
 * @property steps Ordered steps that define the tutorial flow and guidance.
 */
data class Tutorial(
    val name: String,
    val emojis: List<Emoji>,
    val player: Player,
    val platforms: List<Platform>,
    val backgroundElements: List<BackgroundElement>,
    val components: List<Component>,
    val steps: List<TutorialStep>,
    val inputFieldType: InputFieldType,
)
