package com.emojigame.android.ui.fragments.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.View
import com.emojigame.android.activities.LevelActivity
import com.emojigame.android.util.GameAction
import com.emojigame.android.util.LevelProgressManager
import com.emojigame.core.definitions.LevelDefinitions
import com.emojigame.core.util.Logger

/**
 * A dialog shown when the player completes a level.
 *
 * Extends [GameDialogFragment] and provides a "Next Level" button
 * that advances to the next unlocked level if available.
 */
class GameWonDialogFragment : GameDialogFragment() {

    /**
     * Builds the dialog UI and sets up the "Next Level" button logic.
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        val viewModel = (activity as? LevelActivity)?.gameViewModel
        val currentLevel = viewModel?.uiState?.value?.level
        val currentIndex = LevelDefinitions.allLevels.indexOf(currentLevel)
        val nextLevel = LevelDefinitions.allLevels.getOrNull(currentIndex + 1)

        binding.btnNextLevel.apply {
            visibility = if (nextLevel != null) View.VISIBLE else View.GONE
            setOnClickListener {
                Logger.i { ":arrow_right: Next level pressed" }
                dismiss()
                if (currentLevel != null) {
                    val currentLevelNumber = currentLevel.name.removePrefix("level").toIntOrNull()
                    if (currentLevelNumber != null) {
                        LevelProgressManager.unlockNextLevel(currentLevelNumber)
                    }
                }
                viewModel?.let {
                    it.handleAction(GameAction.ClearLevel)
                    it.handleAction(GameAction.ChangeState(com.emojigame.core.states.GameStateType.Play))
                    it.handleAction(GameAction.LoadLevel(nextLevel!!.name))
                }
            }
        }

        return dialog
    }
}
