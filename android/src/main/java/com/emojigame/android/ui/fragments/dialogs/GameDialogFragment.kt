package com.emojigame.android.ui.fragments.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.emojigame.android.activities.LevelActivity
import com.emojigame.android.databinding.DialogGameBinding
import com.emojigame.android.util.GameAction
import com.emojigame.core.states.GameStateType
import com.emojigame.core.util.Logger
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Base class for game-related dialog fragments such as game over or pause menus.
 *
 * Handles common button setup for retrying levels or returning to the main menu.
 * Automatically dismisses the dialog when the game state returns to [GameStateType.Play].
 */
open class GameDialogFragment : DialogFragment() {

    protected lateinit var binding: DialogGameBinding
    protected open val shouldAutoDismissInPlay = true

    /**
     * Inflates the dialog layout and attaches common button behavior.
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogGameBinding.inflate(layoutInflater)

        setupCommonButtons()

        isCancelable = false

        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
    }

    /**
     * Sets up the Try Again and Return to Menu buttons.
     *
     * These buttons communicate with the [LevelActivity]'s [GameViewModel]
     * to reset or exit the level.
     */
    fun setupCommonButtons() {
        binding.btnTryAgain.setOnClickListener {
            Logger.i { "Retry level" }

            val viewModel = (activity as? LevelActivity)?.gameViewModel

            viewModel?.let {
                val currentState = it.gameStateFlow.value

                if (currentState == GameStateType.Play) {
                    it.handleAction(GameAction.ResetLevel)
                } else {
                    it.handleAction(GameAction.ChangeState(GameStateType.Play))
                    it.handleAction(GameAction.ResetLevel)
                }

                dismissNow()
            }
        }

        binding.btnReturnToMenu.setOnClickListener {
            Logger.i { "Return to menu" }
            (activity as? LevelActivity)?.gameViewModel?.handleAction(
                GameAction.ClearLevel
            )
            (activity as? LevelActivity)?.gameViewModel?.handleAction(
                GameAction.ChangeState(
                    GameStateType.Menu
                )
            )
            dismiss()
        }
    }

    /**
     * Observes game state and automatically dismisses the dialog when the game resumes.
     */
    override fun onStart() {
        super.onStart()

        val viewModel = (activity as? LevelActivity)?.gameViewModel ?: return

        lifecycleScope.launch {
            viewModel.gameStateFlow.collectLatest { state ->
                if (shouldAutoDismissInPlay && state == GameStateType.Play) {
                    dismissAllowingStateLoss()
                }
            }
        }
    }
}
