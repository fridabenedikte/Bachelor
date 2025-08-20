package com.emojigame.android.ui.fragments.dialogs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import com.emojigame.android.activities.LevelActivity
import com.emojigame.android.databinding.DialogGameBinding
import com.emojigame.android.util.GameAction
import com.emojigame.core.states.GameStateType
import com.emojigame.core.util.Logger

/**
 * A dialog shown when the game is paused.
 *
 * Provides options to resume, restart the level, or return to the main menu.
 * Does not auto-dismiss when the game state changes to [GameStateType.Play].
 */
class PauseMenuDialogFragment : GameDialogFragment() {
    override val shouldAutoDismissInPlay: Boolean = false

    /**
     * Builds the pause menu dialog and configures button actions.
     */
    @SuppressLint("SetTextI18n")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogGameBinding.inflate(layoutInflater)

        Logger.i { "🟢 PauseMenuDialogFragment vises" }

        binding.tvTitle.text = "Game Paused"
        binding.tvMessage.text = "Take a break 💤"

        binding.btnResume.apply {
            visibility = View.VISIBLE
            setOnClickListener {
                Logger.i { "▶️ Resume pressed" }
                (activity as? LevelActivity)?.gameViewModel?.handleAction(GameAction.ResumeGame)
                dismiss()
            }
        }

        binding.btnTryAgain.apply {
            visibility = View.VISIBLE
            setOnClickListener {
                Logger.i { "🔄 Try Again pressed" }
                val viewModel = (activity as? LevelActivity)?.gameViewModel
                viewModel?.handleAction(GameAction.ResumeGame)
                viewModel?.handleAction(GameAction.ResetLevel)
                dismiss()
            }
        }

        binding.btnReturnToMenu.apply {
            visibility = View.VISIBLE
            setOnClickListener {
                Logger.i { "⬅️ To menu" }
                (activity as? LevelActivity)?.gameViewModel?.handleAction(
                    GameAction.ChangeState(GameStateType.Menu)
                )
                dismiss()
            }
        }

        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
    }
}
