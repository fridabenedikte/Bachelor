package com.emojigame.android.ui.fragments.dialogs

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.emojigame.android.R
import com.emojigame.android.databinding.DialogSettingsBinding
import com.emojigame.android.util.MusicManager
import com.emojigame.android.util.MusicService

/**
 * A dialog that displays game settings.
 *
 * Currently allows toggling background music and closing the dialog.
 */
class SettingsDialogFragment : DialogFragment() {
    private var _binding: DialogSettingsBinding? = null
    private val binding get() = _binding!!

    /**
     * Inflates the settings layout and sets up button actions.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogSettingsBinding.inflate(inflater, container, false)

        updateMusicIcon()

        binding.btnMusicToggle.setOnClickListener {
            val turningMusicOn = !MusicManager.isPlaying
            val action = if (turningMusicOn) "START" else "PAUSE"

            val intent = Intent(requireContext(), MusicService::class.java).apply {
                this.action = action
            }
            requireContext().startService(intent)

            MusicManager.isPlaying = turningMusicOn

            updateMusicIcon()
        }

        binding.btnCloseSettings.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

    /**
     * Updates the music toggle icon to reflect the current music state.
     */
    private fun updateMusicIcon() {
        val resId = if (MusicManager.isPlaying) R.drawable.music_on else R.drawable.music_off
        binding.btnMusicToggle.setImageResource(resId)
    }

    /**
     * Cleans up the binding when the view is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
