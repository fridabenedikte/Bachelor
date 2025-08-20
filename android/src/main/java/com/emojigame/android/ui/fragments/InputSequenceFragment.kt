package com.emojigame.android.ui.fragments

import android.content.ClipDescription
import android.graphics.Bitmap
import android.os.Bundle
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.lifecycle.lifecycleScope
import com.emojigame.android.databinding.FragmentInputSequenceBinding
import com.emojigame.android.ui.TextureStorage
import com.emojigame.android.util.GameAction
import com.emojigame.android.util.InputType
import com.emojigame.core.entities.Emoji
import kotlinx.coroutines.launch

/**
 * Fragment for SEQUENCE input type.
 * Shows a linear command bar where the player can drag and drop emojis to form a sequence.
 */
class InputSequenceFragment : InputFragment<FragmentInputSequenceBinding>() {
    private lateinit var commandBar: LinearLayout
    private lateinit var commandBarContainer: HorizontalScrollView

    override val inputType: InputType = InputType.SEQUENCE

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentInputSequenceBinding {
        return FragmentInputSequenceBinding.inflate(inflater, container, false).apply {
            theme = theme
        }
    }

    override fun getPlayButtonFromBinding(binding: FragmentInputSequenceBinding): Button =
        binding.playButtonInclusion.playButton

    override fun getLibraryBarFromBinding(binding: FragmentInputSequenceBinding): LinearLayout =
        binding.libraryBarInclusion.libraryBar

    override fun getMainLayoutFromBinding(binding: FragmentInputSequenceBinding): LinearLayout =
        binding.inputFragmentContent

    override fun getLibraryBarContainerFromBinding(binding: FragmentInputSequenceBinding): HorizontalScrollView =
        binding.libraryBarInclusion.libraryBarContainer

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews()
        makeDroppable(commandBar)
        setInputBarSizes(commandBarContainer)
        observeEmojiSelection()
    }

    /**
     * Binds layout views to class-level variables.
     */
    private fun bindViews() {
        commandBar = binding.commandBar
        commandBarContainer = binding.commandBarContainer
    }

    /**
     * Observes selected emojis and updates the sequence bar accordingly.
     */
    private fun observeEmojiSelection() {
        viewLifecycleOwner.lifecycleScope.launch {
            stateFlow.collect { uiState ->
                updateCommandBar(uiState.selectedEmojis)
            }
        }
    }

    /**
     * Updates the emoji sequence shown in the command bar.
     * Clears and redraws the views using the selected emojis.
     */
    private fun updateCommandBar(emojis: List<Emoji>) {
        commandBar.removeAllViews()
        selectedEmojiViewMap.clear()

        emojis.forEach { emoji ->
            val bitmap = TextureStorage.getEmojiBitmap(requireContext(), emoji = emoji)
            if (bitmap != null) {
                val view = createCommandImageView(emoji, bitmap)
                commandBar.addView(view)
                selectedEmojiViewMap[emoji.tag] = view
            }
        }
    }

    /**
     * Creates an ImageView for the given emoji with a click listener to deselect it.
     *
     * @param emoji The emoji being added.
     * @param bitmap The image representation of the emoji.
     */
    private fun createCommandImageView(
        emoji: Emoji,
        bitmap: Bitmap,
    ): ImageView {
        return createEmojiImageView(emoji, bitmap) {
            gameViewModel.handleAction(GameAction.DeselectEmoji(emoji))
        }
    }

    /**
     * Creates a drag listener for the command bar to allow dropping emojis onto it.
     * The command bar will accept emojis that are dragged from the emoji library.
     */
    private fun makeDroppable(targetView: View) {
        targetView.setOnDragListener { v, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    return@setOnDragListener event.clipDescription?.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) == true
                }
                DragEvent.ACTION_DROP -> {
                    v.alpha = 1.0f
                    val item = event.clipData?.getItemAt(0)
                    val emojiTag = item?.text?.toString()

                    if (emojiTag != null) {
                        val emoji = gameViewModel.uiState.value.availableEmojis.find { it.tag == emojiTag }
                        if (emoji != null) {
                            gameViewModel.handleAction(GameAction.SelectEmoji(emoji))
                            return@setOnDragListener true
                        }
                    }
                    false
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    v.alpha = 1.0f
                    return@setOnDragListener true
                }
                else -> false
            }
        }
    }
}
