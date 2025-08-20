package com.emojigame.android.ui.fragments

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipDescription
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.emojigame.android.ui.TextureStorage
import com.emojigame.android.ui.ViewCategory
import com.emojigame.android.util.DeviceSizeData
import com.emojigame.android.util.GameAction
import com.emojigame.android.util.GameViewModel
import com.emojigame.android.util.InputType
import com.emojigame.android.util.PaddingUtil
import com.emojigame.core.entities.Emoji
import com.emojigame.core.ui.BaseStateFlow
import com.emojigame.core.util.Logger
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * Abstract base class for input fragments such as sequence, loop, and conditional inputs.
 * Manages layout inflation, emoji rendering, input bar sizing, and drag-and-drop interaction.
 *
 * @param VB The type of [ViewBinding] used by the implementing fragment.
 */
abstract class InputFragment<VB : ViewBinding> : Fragment() {

    protected var bindingNullable: VB? = null
    protected val binding get() = bindingNullable!!

    /** The type of input this fragment represents (e.g., loop or sequence mode). */
    abstract val inputType: InputType

    private lateinit var mainLayout: LinearLayout
    private lateinit var libraryBar: LinearLayout
    private lateinit var librarybarContainer: HorizontalScrollView
    protected lateinit var playButton: Button
    private val width = DeviceSizeData.portraitInputFragmentWidth
    private val height = DeviceSizeData.portraitInputFragmentHeight

    /** Shared game view model, provided by the host activity. */
    protected val gameViewModel: GameViewModel
        get() = requireActivity().run {
            ViewModelProvider(this)[GameViewModel::class.java]
        }

    protected val selectedEmojiViewMap: MutableMap<String, View> = mutableMapOf()
    protected val availableEmojiViewMap: MutableMap<String, View> = mutableMapOf()

    private val _stateFlow: StateFlow<BaseStateFlow> by lazy { gameViewModel.uiState }

    /** The observable UI state, overridable for tutorial states, etc. */
    protected var stateFlow: StateFlow<BaseStateFlow>
        get() = _stateFlowOverride ?: _stateFlow
        private set(_) {}

    private var _stateFlowOverride: StateFlow<BaseStateFlow>? = null

    /**
     * Sets an override for the state flow (e.g., tutorial state instead of play state).
     *
     * @param flow The new state flow to observe.
     */
    fun setObservedState(flow: StateFlow<BaseStateFlow>) {
        _stateFlowOverride = flow
    }

    abstract fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): VB
    abstract fun getPlayButtonFromBinding(binding: VB): Button
    abstract fun getLibraryBarFromBinding(binding: VB): LinearLayout
    abstract fun getMainLayoutFromBinding(binding: VB): LinearLayout
    abstract fun getLibraryBarContainerFromBinding(binding: VB): HorizontalScrollView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        bindingNullable = inflateBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = bindingNullable ?: throw IllegalStateException("Binding is null in onViewCreated")

        playButton = getPlayButtonFromBinding(binding)
        libraryBar = getLibraryBarFromBinding(binding)
        mainLayout = getMainLayoutFromBinding(binding)
        librarybarContainer = getLibraryBarContainerFromBinding(binding)

        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            PaddingUtil.applyPaddingToView(
                view,
                touchingLeft = true,
                touchingTop = false,
                touchingRight = true,
                touchingBottom = true
            )
        } else if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            PaddingUtil.applyPaddingToView(
                view,
                touchingLeft = false,
                touchingTop = true,
                touchingRight = true,
                touchingBottom = true
            )
        }
        PaddingUtil.applyUniformPaddingToChildren(mainLayout)
        setInputBarSizes(librarybarContainer)
        setPlayOnClickListener()

        viewLifecycleOwner.lifecycleScope.launch {
            stateFlow.collect { state ->
                updateLibraryBar(state.availableEmojis)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            stateFlow.collect { uiState ->
                uiState.hint?.let { hint ->
                    if (hint.emoji != null) {
                        highlightEmoji(hint.emoji!!)
                    } else {
                        Logger.e { hint.message.toString() }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingNullable = null
    }

    /**
     * Sets the click behavior of the play button.
     * Sends a PlaySelectedEmojis action with current selection.
     */
    protected open fun setPlayOnClickListener() {
        playButton.setOnClickListener {
            gameViewModel.handleAction(GameAction.PlaySelectedEmojis(stateFlow.value.selectedEmojis))
        }
    }

    /**
     * Resolves a UI view by its identifier and category.
     * Used in tutorials or testing to fetch references to views dynamically.
     *
     * @param id The string identifier.
     * @param category The category of view (e.g., available or selected emoji).
     * @return The matching view, or null if not found.
     */
    open fun resolveViewByIdentifier(id: String, category: ViewCategory): View? {
        return when (category) {
            ViewCategory.Special -> when (id) {
                "play_button" -> playButton
                else -> null
            }
            ViewCategory.SelectedEmoji -> selectedEmojiViewMap[id]
            ViewCategory.AvailableEmoji -> availableEmojiViewMap[id]
        }
    }

    protected fun setInputBarSizes(container: HorizontalScrollView) {
        when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                container.layoutParams = container.layoutParams.apply {
                    width = DeviceSizeData.portraitWidthOfInputBars
                    height = DeviceSizeData.portraitHeightOfInputBars
                }
            }
            Configuration.ORIENTATION_LANDSCAPE -> {
                container.layoutParams = container.layoutParams.apply {
                    width = DeviceSizeData.landscapeWidthOfInputBars
                    height = DeviceSizeData.landscapeHeightOfInputBars
                }
            }
            else -> {
                Logger.w { "Unknown orientation: ${resources.configuration.orientation}" }
                container.layoutParams = container.layoutParams.apply {
                    width = DeviceSizeData.portraitWidthOfInputBars
                    height = DeviceSizeData.portraitHeightOfInputBars
                }
            }
        }
    }

    /**
     * Updates the emoji library bar with the given emojis.
     *
     * @param emojis The list of available emojis to display.
     */
    private fun updateLibraryBar(emojis: List<Emoji>) {
        libraryBar.removeAllViews()
        emojis.forEach { emoji ->
            val bitmap = TextureStorage.getEmojiBitmap(context = requireContext(), emoji = emoji)
            if (bitmap != null) {
                val view = createLibraryImageView(emoji, bitmap)
                libraryBar.addView(view)
                availableEmojiViewMap[emoji.tag] = view
            }
        }
    }

    /**
     * Creates an emoji ImageView for the library bar with drag support.
     *
     * @param emoji The emoji to show.
     * @param bitmap The bitmap representing the emoji.
     */
    private fun createLibraryImageView(emoji: Emoji, bitmap: Bitmap): ImageView {
        return createEmojiImageView(emoji, bitmap) {
            gameViewModel.handleAction(GameAction.SelectEmoji(emoji))
        }.apply {
            makeDraggable(this, emoji)
        }
    }

    /**
     * Creates a basic emoji ImageView with styling and click listener.
     *
     * @param emoji The emoji data.
     * @param bitmap The image to show.
     * @param onClick The action when clicked (e.g., select).
     */
    protected fun createEmojiImageView(emoji: Emoji, bitmap: Bitmap, onClick: () -> Unit): ImageView {
        return ImageView(requireContext()).apply {
            layoutParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                ).apply {
                    setMargins(
                        DeviceSizeData.inputBarPadding,
                        DeviceSizeData.inputBarPadding,
                        DeviceSizeData.inputBarPadding,
                        DeviceSizeData.inputBarPadding
                    )
                }
            scaleType = ImageView.ScaleType.CENTER_INSIDE
            adjustViewBounds = true
            setImageBitmap(bitmap)
            setOnClickListener { onClick() }
            tag = emoji.tag
        }
    }

    /**
     * Makes a view blink using an alpha animation.
     *
     * @param view The view to blink.
     */
    private fun blinkView(view: View) {
        val animation = AlphaAnimation(1.0f, 0.0f).apply {
            duration = 300
            repeatMode = Animation.REVERSE
            repeatCount = 3
        }
        view.startAnimation(animation)
    }

    /**
     * Highlights the emoji in the library bar using a blinking effect.
     *
     * @param emoji The emoji to highlight.
     */
    private fun highlightEmoji(emoji: Emoji) {
        availableEmojiViewMap[emoji.tag]?.let { blinkView(it) }
        for (i in 0 until libraryBar.childCount) {
            val child = libraryBar.getChildAt(i)
            if (child.tag == emoji.tag) {
                blinkView(child)
                break
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            kotlinx.coroutines.delay(1000)
            gameViewModel.handleAction(GameAction.ClearHint)
        }
    }

    /**
     * Enables drag-and-drop behavior for the emoji image.
     * Falls back to selection if not dragged.
     *
     * @param view The view to make draggable.
     * @param emoji The emoji associated with the view.
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun makeDraggable(
        view: ImageView,
        emoji: Emoji,
    ) {
        var startX = 0f
        var startY = 0f
        val dragThreshold = 20
        var isDragging = false

        view.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = event.rawX
                    startY = event.rawY
                    isDragging = false
                    return@setOnTouchListener true
                }

                MotionEvent.ACTION_MOVE -> {
                    val deltaX = abs(event.rawX - startX)
                    val deltaY = abs(event.rawY - startY)

                    if (!isDragging && (deltaX > dragThreshold || deltaY > dragThreshold)) {
                        isDragging = true
                        val clipData = ClipData(
                            emoji.tag,
                            arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                            ClipData.Item(emoji.tag),
                        )
                        val shadowBuilder = View.DragShadowBuilder(v)
                        v.startDragAndDrop(clipData, shadowBuilder, v, View.DRAG_FLAG_GLOBAL)
                        v.alpha = 0.5f
                        return@setOnTouchListener true
                    }
                }

                MotionEvent.ACTION_UP -> {
                    if (!isDragging) {
                        gameViewModel.handleAction(GameAction.SelectEmoji(emoji))
                    }
                    v.alpha = 1.0f
                    return@setOnTouchListener true
                }

                MotionEvent.ACTION_CANCEL -> {
                    v.alpha = 1.0f
                }
            }
            false
        }
    }
}
