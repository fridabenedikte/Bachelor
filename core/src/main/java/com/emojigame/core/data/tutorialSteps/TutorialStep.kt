package com.emojigame.core.data.tutorialSteps

import com.emojigame.core.util.GameIntent

/**
 * Represents a single step in a tutorial sequence.
 *
 * Each [TutorialStep] defines a condition for completion based on a [GameIntent]
 * and provides a [guide] used to visually instruct the player.
 */
abstract class TutorialStep {
    /**
     * The [GameIntent] that is expected for this step to be completed
     */
    abstract val expectedIntent: GameIntent
    /**
     * The visual or interactive guide that should be shown to the player
     * during this tutorial step.
     */
    abstract val guide: TutorialGuide

    /**
     * Determines whether the provided [intent] completes this tutorial step.
     *
     * @param intent The game action performed by the player.
     * @return `true` if the intent satisfies the step's completion condition.
     */
    abstract fun isCompletedBy(intent: GameIntent): Boolean
}
