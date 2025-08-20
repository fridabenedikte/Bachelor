package com.emojigame.core.data.tutorialSteps

import com.emojigame.core.entities.Emoji
import com.emojigame.core.util.Vector2f

/**
 * Represents a visual or interactive guide used during a tutorial step.
 *
 * These guides instruct the player on which UI element to interact with,
 * or what gesture to perform, such as tapping or dragging.
 */
sealed class TutorialGuide {
    data class TapButton(val buttonId: String) : TutorialGuide()
    data class TapSelectedEmoji(val emoji: Emoji) : TutorialGuide()
    data class TapAvailableEmoji(val emoji: Emoji) : TutorialGuide()
    data class DragAndDrop(val from: Vector2f, val to: Vector2f) : TutorialGuide()
}
