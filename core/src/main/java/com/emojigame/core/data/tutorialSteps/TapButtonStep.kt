package com.emojigame.core.data.tutorialSteps

import com.emojigame.core.entities.Emoji
import com.emojigame.core.util.GameIntent

/**
 * Represents a tutorial step where the user must tap a specific UI button
 * (e.g. play, reset, clear).
 *
 * This step is considered complete when the corresponding [GameIntent] is triggered.
 *
 * @property expectedIntent The exact intent expected from the user.
 * @property buttonId The ID of the button this step highlights and responds to.
 */
class TapButtonStep(
    override val expectedIntent: GameIntent,
    private val buttonId: String,
) : TutorialStep() {

    /** Visual guide instructing the user to tap a button with the given [buttonId]. */
    override val guide: TutorialGuide = TutorialGuide.TapButton(buttonId)

    /**
     * Checks whether the given [intent] satisfies the condition for this step.
     *
     * @param intent The user-triggered game action.
     * @return `true` if the intent matches the [expectedIntent], `false` otherwise.
     */
    override fun isCompletedBy(intent: GameIntent): Boolean {
        return when (expectedIntent) {
            is GameIntent.PlayEmoji -> {
                intent is GameIntent.PlayEmoji &&
                    haveSameEmojis(expectedIntent.emojis, intent.emojis)
            }
            is GameIntent.ResetLevel -> intent is GameIntent.ResetLevel
            is GameIntent.ClearSelection -> intent is GameIntent.ClearSelection
            is GameIntent.ShowHint -> intent is GameIntent.ShowHint
            is GameIntent.ClearHint -> intent is GameIntent.ClearHint
            is GameIntent.BackToMenu -> intent is GameIntent.BackToMenu
            is GameIntent.OpenRepeatCountDialog -> intent is GameIntent.OpenRepeatCountDialog
            is GameIntent.UpdateRepeatCount -> {
                if (intent is GameIntent.UpdateRepeatCount) {
                    return intent.count == expectedIntent.count
                } else false
            }
            is GameIntent.CancelRepeatCountDialog -> intent is GameIntent.CancelRepeatCountDialog
            else -> false
        }
    }

    /**
     * Compares two emoji lists by their tags to determine equality.
     */
    private fun haveSameEmojis(expected: List<Emoji>, actual: List<Emoji>): Boolean {
        if (expected.size != actual.size) return false
        return expected.map { it.tag } == actual.map { it.tag }
    }

    /**
     * Debug-friendly string representation of the step.
     */
    override fun toString(): String {
        return "TapButtonStep(buttonId = $buttonId, expectedIntent = $expectedIntent)"
    }
}
