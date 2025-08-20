package com.emojigame.android.ui.fragments.dialogs

import android.app.Dialog
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import com.emojigame.android.R

/**
 * A dialog shown when the player loses a level.
 *
 * Extends [GameDialogFragment] to provide shared retry/menu behavior.
 * Plays a fail sound and hides the "Next Level" button.
 */
class GameOverDialogFragment : GameDialogFragment() {
    private var mediaPlayer: MediaPlayer? = null

    /**
     * Builds the dialog UI and starts the fail sound.
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        binding.tvTitle.text = getString(R.string.game_over)
        binding.tvMessage.text = getString(R.string.you_lost_try_again)
        binding.btnNextLevel.visibility = View.GONE

        playFailSound()

        return dialog
    }

    /**
     * Plays the game over fail sound.
     */
    private fun playFailSound() {
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.fail)
        mediaPlayer?.start()
    }

    /**
     * Releases the MediaPlayer when the dialog is destroyed.
     */
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
