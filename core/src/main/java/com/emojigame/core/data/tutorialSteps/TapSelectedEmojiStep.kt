package com.emojigame.core.data.tutorialSteps

import com.emojigame.core.entities.Emoji
import com.emojigame.core.util.GameIntent

/**
 * A tutorial step that requires the user to deselect a specific emoji
 * from their current selection by tapping on it.
 *
 * The step is considered completed when a matching [GameIntent.DeselectEmoji] is received.
 *
 * @property expectedIntent The exact [GameIntent.DeselectEmoji] the step is expecting.
 * @property emoji The emoji the user must deselect to complete the step.
 */
class TapSelectedEmojiStep(
    override val expectedIntent: GameIntent,
    private val emoji: Emoji
) : TutorialStep() {

    /** Guide for the tutorial system to highlight the selected emoji. */
    override val guide: TutorialGuide = TutorialGuide.TapSelectedEmoji(emoji)

    /**
     * Checks if the given [intent] completes this step by matching the expected deselection.
     *
     * @param intent The incoming game intent triggered by user input.
     * @return `true` if the intent matches the expected deselection of the target emoji.
     */
    override fun isCompletedBy(intent: GameIntent): Boolean {
        return intent is GameIntent.DeselectEmoji &&
            expectedIntent is GameIntent.DeselectEmoji &&
            intent.emoji.tag == expectedIntent.emoji.tag
    }

    /**
     * Provides a debug-friendly string representation of the tutorial step.
     */
    override fun toString(): String {
        return "TapSelectedEmojiStep(emoji=${emoji.tag}, expectedIntent=$expectedIntent)"
    }
}
