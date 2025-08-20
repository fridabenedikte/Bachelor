package com.emojigame.android.util

import com.emojigame.android.ui.fragments.InputFragment
import com.emojigame.android.ui.fragments.InputIfElseFragment
import com.emojigame.android.ui.fragments.InputLoopFragment
import com.emojigame.android.ui.fragments.InputSequenceFragment

/**
 * Enum class representing different input types for the game.
 *
 * @property displayName The display name of the input type.
 * @property createFragment A lambda function that creates the corresponding InputFragment.
 * @property description A brief description of the input type.
 */
enum class InputType(
    val displayName: String,
    val createFragment: () -> InputFragment<*>,
    val description: String
) {
    SEQUENCE("Sequence mode", { InputSequenceFragment() }, "Play emojis in order"),
    LOOP("Loop mode", { InputLoopFragment() }, "Play a loop of emojis"),
    IFELSE("If else mode", { InputIfElseFragment() }, "Play emojis based on presented images")
}
