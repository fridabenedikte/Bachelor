package com.emojigame.core.data.tutorialSteps

import com.emojigame.core.entities.Emoji
import com.emojigame.core.util.GameIntent

/**
 * Represents a tutorial step where the user must tap a specific emoji
 * from the available emoji library.
 *
 * This step is considered complete when the correct [GameIntent.SelectEmoji]
 * is triggered with a matching emoji tag.
 *
 * @property expectedIntent The expected selection intent the player must trigger.
 * @property emoji The specific emoji that the tutorial is guiding the player to tap.
 */
class TapAvailableEmojiStep(
    override val expectedIntent: GameIntent,
    val emoji: Emoji
) : TutorialStep() {

    /** Visual guide shown in the UI, highlighting the available emoji to tap. */
    override val guide: TutorialGuide = TutorialGuide.TapAvailableEmoji(emoji)

    /**
     * Checks whether the given [intent] matches the expected action.
     *
     * @param intent The [GameIntent] triggered by the user.
     * @return `true` if the intent matches the expected selection, `false` otherwise.
     */
    override fun isCompletedBy(intent: GameIntent): Boolean {
        return intent is GameIntent.SelectEmoji &&
            expectedIntent is GameIntent.SelectEmoji &&
            intent.emoji.tag == expectedIntent.emoji.tag
    }

    /**
     * String representation useful for debugging.
     */
    override fun toString(): String {
        return "TapAvailableEmojiStep(emoji = ${emoji.tag}, expectedIntent = $expectedIntent)"
    }
}
