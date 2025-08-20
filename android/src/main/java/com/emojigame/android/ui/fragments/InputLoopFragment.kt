package com.emojigame.android.ui.fragments

import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.doOnLayout
import androidx.lifecycle.lifecycleScope
import com.emojigame.android.R
import com.emojigame.android.databinding.FragmentInputLoopBinding
import com.emojigame.android.ui.TextureStorage
import com.emojigame.android.ui.ViewCategory
import com.emojigame.android.util.DeviceSizeData
import com.emojigame.android.util.GameAction
import com.emojigame.android.util.InputType
import com.emojigame.core.entities.Emoji
import com.emojigame.core.util.Logger
import kotlinx.coroutines.launch
import kotlin.math.min

/**
 * Fragment for the loop input type.
 * This fragment contains a wheel that allows the user to select emojis in a loop.
 */
class InputLoopFragment : InputFragment<FragmentInputLoopBinding>() {
    private lateinit var emojiWheel: View
    private lateinit var emojiWheelCenter: TextView
    private lateinit var emojiWheelGroup: ConstraintLayout
    private var repeatCount = 1
    private var slotCount = 5
    private var pendingEmojis: List<Emoji>? = null
    private var layoutReady = false

    override val inputType: InputType = InputType.LOOP

    private var numberPickerDialog: AlertDialog? = null
    private var numberPickerConfirmButton: Button? = null
    private var numberPickerCancelButton: Button? = null
    private var currentNumberPicker: NumberPicker? = null

    /**
     * Inflates the layout and returns the view binding for this fragment.
     */
    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentInputLoopBinding {
        return FragmentInputLoopBinding.inflate(inflater, container, false).apply {
            theme = theme
        }
    }

    override fun getPlayButtonFromBinding(binding: FragmentInputLoopBinding): Button =
        binding.playButtonInclusion.playButton

    override fun getLibraryBarFromBinding(binding: FragmentInputLoopBinding): LinearLayout =
        binding.libraryBarInclusion.libraryBar

    override fun getMainLayoutFromBinding(binding: FragmentInputLoopBinding): LinearLayout =
        binding.inputFragmentContent

    override fun getLibraryBarContainerFromBinding(binding: FragmentInputLoopBinding): HorizontalScrollView =
        binding.libraryBarInclusion.libraryBarContainer

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews()
        setupWheelLayoutOnReady()
        setupWheelCenterClickListener()
        observeSelectedEmojis()
        observeRepeatCount()
    }

    /**
     * Binds view references from the layout to class-level variables.
     */
    private fun bindViews() {
        emojiWheel = binding.emojiWheelContainer
        emojiWheelCenter = binding.wheelCenter
        emojiWheelGroup = binding.emojiWheelGroup
    }

    /**
     * Sets up the emoji wheel once the layout has been measured and is ready.
     * Also applies any previously collected emoji input.
     */
    private fun setupWheelLayoutOnReady() {
        emojiWheel.doOnLayout {
            setupEmojiWheel(5, 0.8f)
            layoutReady = true
            pendingEmojis?.let {
                updateWheelInput(it)
                pendingEmojis = null
            }
        }
    }

    /**
     * Observes changes to the selected emojis and updates the emoji wheel.
     * If the layout isn't ready yet, it stores them to be set later.
     */
    private fun observeSelectedEmojis() {
        viewLifecycleOwner.lifecycleScope.launch {
            stateFlow.collect { uiState ->
                val selectedEmojis = uiState.selectedEmojis
                if (layoutReady) updateWheelInput(selectedEmojis)
                else pendingEmojis = selectedEmojis
            }
        }
    }

    /**
     * Observes changes to the repeat count and updates the wheel display text.
     */
    private fun observeRepeatCount() {
        viewLifecycleOwner.lifecycleScope.launch {
            gameViewModel.uiState.collect { uiState ->
                repeatCount = uiState.repeatCount
                updateRepeatCountDisplay()
            }
        }
    }

    override fun setPlayOnClickListener() {
        playButton.setOnClickListener {
            spinWheel(emojiWheel)
            gameViewModel.handleAction(GameAction.PlaySelectedEmojis(stateFlow.value.selectedEmojis))
        }
    }

    /**
     * Configures the emoji wheel layout based on available space and screen orientation.
     *
     * This function sets up the necessary variables needed, and assigns them to the wheel.
     * This cannot be done in the loading activity, as we want the wheel to adjust its size
     * based on the available space, after the library bar and play buttons have been assigned their
     * space. I decided not to use the DeviceSizeData as the variables will not be accessed by any
     * other part of the app, and the extra storage there will therefore be redundant. This function
     * still retrieves some values from the DeviceSizeData, as we want that to be where most of the
     * data that determines the layout is stored.
     *
     * @param slots Number of emoji slots on the wheel.
     * @param maxWidthOfScreen Max width relative to screen size (0.0–1.0).
     */
    private fun setupEmojiWheel(slots: Int, maxWidthOfScreen: Float = 1f) {
        val wheelInitialHeight = emojiWheel.height
        val maxWheelDiameter =
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
                (DeviceSizeData.landscapeWidthOfInputBars * maxWidthOfScreen).toInt()
            else (DeviceSizeData.portraitWidthOfInputBars * maxWidthOfScreen).toInt()
        val wheelDiameter = min(wheelInitialHeight, maxWheelDiameter)
        val wheelCenterDiameter = (wheelDiameter * DeviceSizeData.inputWheelCenterPortion).toInt()
        val wheelFieldDiameter = (wheelDiameter * DeviceSizeData.inputWheelFieldPortion).toInt()

        // If the height is bigger than the diameter, the difference must be added as padding, to
        // ensure the wheel still is circular. This occurs if the vertical space for the wheel is
        // too great for it to fit horizontally, and the diameter is then reduced to the available
        // space. The layout weight given will override this change, and stretch the wheel
        // container vertically, making it an oval.
        if (wheelDiameter < wheelInitialHeight) {
            Logger.i { "Wheel not able to fill all vertical space, adding margins" }
            val difference = emojiWheel.height - wheelDiameter
            val marginLayoutParams = emojiWheel.layoutParams as ViewGroup.MarginLayoutParams
            marginLayoutParams.topMargin += difference / 2
            marginLayoutParams.bottomMargin += difference / 2
            emojiWheel.layoutParams = marginLayoutParams
        }

        val wheelLayoutParams = emojiWheel.layoutParams
        wheelLayoutParams.width = wheelDiameter
        wheelLayoutParams.height = wheelDiameter

        val wheelCenterLayoutParams = emojiWheelCenter.layoutParams
        wheelCenterLayoutParams.width = wheelCenterDiameter
        wheelCenterLayoutParams.height = wheelCenterDiameter

        val fieldDistanceFromCenter =
            wheelCenterDiameter / 2 + wheelFieldDiameter / 2 +
                (wheelDiameter / 2 - (wheelCenterDiameter / 2 + wheelFieldDiameter)) / 2

        createSlots(slots, wheelFieldDiameter, fieldDistanceFromCenter) // ARBITRARY NUMBER OF SLOTS
    }

    /**
     * Dynamically creates the emoji slot views around the wheel center.
     *
     * @param slots Number of slots to create.
     * @param fieldDiameter Diameter of each emoji field.
     * @param distanceFromCenter Distance from center to each emoji slot.
     */
    private fun createSlots(slots: Int, fieldDiameter: Int, distanceFromCenter: Int) {
        val constraintSet = ConstraintSet()
        for (i in 0 until slots) {
            val angle = (360f / slots) * i - 90

            val slot = ImageView(requireContext()).apply {
                id = View.generateViewId()
                layoutParams = ConstraintLayout.LayoutParams(fieldDiameter, fieldDiameter)
                setBackgroundResource(R.drawable.emoji_slot_background)
                scaleType = ImageView.ScaleType.CENTER_INSIDE
            }

            constraintSet.constrainWidth(slot.id, fieldDiameter)
            constraintSet.constrainHeight(slot.id, fieldDiameter)
            constraintSet.constrainCircle(
                slot.id,
                R.id.wheel_center,
                distanceFromCenter,
                angle
            )
            emojiWheelGroup.addView(slot)
        }
        constraintSet.applyTo(emojiWheelGroup)
    }

    /**
     * Sets up the click listener for the wheel center.
     * When clicked, it shows a number picker dialog to set the repeat count.
     */
    private fun setupWheelCenterClickListener() {
        emojiWheelCenter.setOnClickListener {
            gameViewModel.handleAction(GameAction.OpenRepeatCountDialog)
            showNumberPickerDialog()
        }
    }

    /**
     * Updates the wheel input with the selected emojis.
     * @param emojis The list of selected emojis.
     */
    private fun updateWheelInput(emojis: List<Emoji>) {
        for (i in 0 until slotCount) {
            val slotView = emojiWheelGroup.getChildAt(i + 1) as? ImageView

            if (i < emojis.size) {
                val emoji = emojis[i]
                val bitmap = TextureStorage.getEmojiBitmap(
                    context = requireContext(),
                    emoji = emoji
                )
                if (bitmap != null) {
                    slotView?.setImageBitmap(bitmap)
                    slotView?.setOnClickListener {
                        gameViewModel.handleAction(GameAction.DeselectEmoji(emoji))
                    }
                }
            } else {
                slotView?.setImageBitmap(null)
            }
        }
    }

    /**
     * Displays a dialog frame for choosing number that should be in the center of the wheel
     */
    private fun showNumberPickerDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_number_picker, null)
        val numberPicker = dialogView.findViewById<NumberPicker>(R.id.number_picker)

        numberPicker.minValue = 1
        numberPicker.maxValue = 10
        numberPicker.value = repeatCount

        numberPickerDialog = AlertDialog.Builder(requireContext())
            .setTitle("Set Repeat Count")
            .setView(dialogView)
            .setPositiveButton("OK") { _, _ ->
                val selectedValue = numberPicker.value
                gameViewModel.handleAction(GameAction.UpdateRepeatCount(selectedValue))
            }
            .setNeutralButton("Cancel") { _, _ ->
                gameViewModel.handleAction(GameAction.CancelRepeatCountDialog)
            }
            .create()

        numberPickerDialog?.show()

        currentNumberPicker = numberPicker
        numberPickerConfirmButton = numberPickerDialog?.getButton(AlertDialog.BUTTON_POSITIVE)
        numberPickerCancelButton = numberPickerDialog?.getButton(AlertDialog.BUTTON_NEUTRAL)
    }

    /**
     * Updates the repeat count displayed in the center of the wheel
     */
    private fun updateRepeatCountDisplay() {
        val repeatView: TextView = emojiWheelGroup.getChildAt(0) as TextView
        repeatView.text = repeatCount.toString()
    }

    /**
     * Spins the wheel for the specified repeat count.
     * @param wheel The wheel view to spin.
     */
    private fun spinWheel(wheel: View) {
        wheel.rotation = 0f
        val rotation = ObjectAnimator.ofFloat(wheel, View.ROTATION, 0f, 360f * repeatCount)
        rotation.duration = 1000 * repeatCount.toLong() // 1 second per turn
        rotation.interpolator = LinearInterpolator()
        rotation.start()
    }

    override fun resolveViewByIdentifier(id: String, category: ViewCategory): View? {
        return when (category) {
            ViewCategory.Special -> when (id) {
                "play_button" -> playButton
                "emoji_wheel_center" -> emojiWheelCenter
                "number_picker" -> currentNumberPicker
                "number_picker_confirm" -> numberPickerConfirmButton
                "number_picker_cancel" -> numberPickerCancelButton
                else -> super.resolveViewByIdentifier(id, category)
            }
            ViewCategory.SelectedEmoji,
            ViewCategory.AvailableEmoji -> super.resolveViewByIdentifier(id, category)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        numberPickerDialog?.dismiss()
        numberPickerDialog = null
        currentNumberPicker = null
        numberPickerConfirmButton = null
        numberPickerCancelButton = null
    }
}
