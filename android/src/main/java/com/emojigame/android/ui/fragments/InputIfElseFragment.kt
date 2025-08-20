package com.emojigame.android.ui.fragments

import android.annotation.SuppressLint
import android.content.ClipDescription
import android.graphics.Bitmap
import android.os.Bundle
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.emojigame.android.databinding.FragmentInputIfElseBinding
import com.emojigame.android.ui.TextureStorage
import com.emojigame.android.util.GameAction
import com.emojigame.android.util.InputType
import com.emojigame.core.entities.Emoji
import kotlinx.coroutines.launch

/**
 * Fragment representing the "If/Else" input mode.
 *
 * This mode allows players to choose two emojis — one for the "if" condition and one for the "else" branch.
 * It uses drag-and-drop for selecting emojis from the library, and visually displays condition-specific images.
 */
class InputIfElseFragment : InputFragment<FragmentInputIfElseBinding>() {

    override val inputType: InputType = InputType.IFELSE

    private lateinit var ifEmojiSlotContainer: FrameLayout
    private lateinit var elseEmojiSlotContainer: FrameLayout
    private lateinit var ifEmojiSlot: ImageView
    private lateinit var elseEmojiSlot: ImageView
    private lateinit var ifelseBar: ConstraintLayout
    private lateinit var ifImage: ImageView
    private lateinit var elseImage: ImageView

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentInputIfElseBinding {
        return FragmentInputIfElseBinding.inflate(inflater, container, false).apply {
            theme = theme
        }
    }

    override fun getPlayButtonFromBinding(binding: FragmentInputIfElseBinding): Button =
        binding.playButtonInclusion.playButton

    override fun getLibraryBarFromBinding(binding: FragmentInputIfElseBinding): LinearLayout =
        binding.libraryBarInclusion.libraryBar

    override fun getMainLayoutFromBinding(binding: FragmentInputIfElseBinding): LinearLayout =
        binding.inputFragmentContent

    override fun getLibraryBarContainerFromBinding(binding: FragmentInputIfElseBinding): HorizontalScrollView =
        binding.libraryBarInclusion.libraryBarContainer

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ifEmojiSlot = binding.ifEmojiSlot
        elseEmojiSlot = binding.elseEmojiSlot
        ifEmojiSlotContainer = binding.ifEmojiSlotContainer
        elseEmojiSlotContainer = binding.elseEmojiSlotContainer
        ifelseBar = binding.ifElseBar
        ifImage = binding.ifImage
        elseImage = binding.elseImage

        viewLifecycleOwner.lifecycleScope.launch {
            gameViewModel.uiState.collect { uiState ->
                val selectedEmojis = uiState.selectedEmojis
                updatePlayedEmojis(selectedEmojis)
                updatePlayedEmojis(uiState.selectedEmojis)

                if (uiState.ifElsePhotos.size >= 2) {
                    val ifDrawableId = getDrawableIdByName(uiState.ifElsePhotos[0])
                    val elseDrawableId = getDrawableIdByName(uiState.ifElsePhotos[1])
                    if (ifDrawableId != 0) ifImage.setImageResource(ifDrawableId)
                    if (elseDrawableId != 0) elseImage.setImageResource(elseDrawableId)
                }
            }
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

    /**
     * Utility function to update an image view with a drawable resource.
     */
    private fun updateImage(container: ImageView, image: Int) {
        container.setImageResource(image)
    }

    /**
     * Converts a drawable name string into its corresponding resource ID.
     */
    @SuppressLint("DiscouragedApi")
    private fun getDrawableIdByName(name: String): Int {
        return resources.getIdentifier(name, "drawable", requireContext().packageName)
    }

    /**
     * Displays the currently selected emojis in the "if" and "else" slots.
     *
     * @param emojis A list of selected emojis, with index 0 being "if", and 1 being "else".
     */
    private fun updatePlayedEmojis(emojis: List<Emoji>) {
        val context = requireContext()

        ifEmojiSlotContainer.removeAllViews()
        if (emojis.isNotEmpty()) {
            val emoji = emojis[0]
            val bitmap = TextureStorage.getEmojiBitmap(context, emoji = emoji)
            if (bitmap != null) {
                val view = createCommandImageView(emoji, bitmap)
                ifEmojiSlotContainer.addView(view)
            }
        }

        elseEmojiSlotContainer.removeAllViews()
        if (emojis.size > 1) {
            val emoji = emojis[1]
            val bitmap = TextureStorage.getEmojiBitmap(context, emoji = emoji)
            if (bitmap != null) {
                val view = createCommandImageView(emoji, bitmap)
                elseEmojiSlotContainer.addView(view)
            }
        }
    }

    /**
     * Creates an [ImageView] for a played emoji that, when clicked, deselects the emoji.
     */
    private fun createCommandImageView(
        emoji: Emoji,
        bitmap: Bitmap,
    ): ImageView {
        return createEmojiImageView(emoji, bitmap) {
            gameViewModel.handleAction(GameAction.DeselectEmoji(emoji))
        }
    }
}
