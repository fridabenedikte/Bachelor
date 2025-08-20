package com.emojigame.android.ui.fragments

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.graphics.scale
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.emojigame.android.R
import com.emojigame.android.databinding.FragmentTutorialOverlayBinding
import com.emojigame.android.util.GameAction
import com.emojigame.android.util.GameViewModel
import com.emojigame.core.states.GameStateType
import com.emojigame.core.ui.InputFieldType
import kotlinx.coroutines.launch
import com.emojigame.reslib.R as res

/**
 * Fragment displaying a tutorial overlay with animated pointer guidance
 * and interactive UI elements like puzzle and input fragments.
 */
class TutorialOverlayFragment : Fragment() {
    private lateinit var binding: FragmentTutorialOverlayBinding
    private val gameViewModel: GameViewModel by activityViewModels()

    private lateinit var inputFragment: InputFragment<*>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTutorialOverlayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTutorialCanvas()
        setupChildFragments()
        observeTutorialState()
        observeGameState()
        setupButtons()
        handleBackPress()
    }

    /**
     * Loads and scales the tutorial pointer bitmap, assigns it to the canvas,
     * and sets up the view resolver for tutorial guidance.
     */
    private fun setupTutorialCanvas() {
        val tutorialCanvas = binding.root.findViewById<com.emojigame.android.ui.views.TutorialCanvasView>(
            R.id.tutorial_canvas
        )

        val originalBitmap = BitmapFactory.decodeResource(resources, res.drawable.hand_pointer)
        val scaleFactor = 0.08f
        val scaledBitmap = originalBitmap.scale(
            (originalBitmap.width * scaleFactor).toInt(),
            (originalBitmap.height * scaleFactor).toInt()
        )
        tutorialCanvas.pointerBitmap = scaledBitmap

        tutorialCanvas.setViewResolver { id, category ->
            inputFragment.resolveViewByIdentifier(id, category)
        }
    }

    /**
     * Initializes the puzzle and input fragments and injects the tutorial flow.
     */
    private fun setupChildFragments() {
        val puzzleFragment = PuzzleFragment().apply {
            setObservedState(gameViewModel.tutorialFlow)
        }

        val tutorialType = gameViewModel.tutorialFlow.value.tutorial?.inputFieldType
        if (tutorialType != null) {
            inputFragment = createInputFragmentForType(tutorialType).apply {
                setObservedState(gameViewModel.tutorialFlow)
            }

            childFragmentManager.beginTransaction()
                .replace(R.id.input_fragment_container, inputFragment)
                .commitNow()
        }

        childFragmentManager.beginTransaction()
            .replace(R.id.puzzle_fragment_container, puzzleFragment)
            .commitNow()
    }

    /**
     * Collects the tutorial state and updates the tutorial canvas accordingly.
     */
    private fun observeTutorialState() {
        val canvas = binding.root.findViewById<com.emojigame.android.ui.views.TutorialCanvasView>(
            R.id.tutorial_canvas
        )

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                gameViewModel.tutorialFlow.collect { state ->
                    canvas.updateState(currentStep = state.steps.getOrNull(state.currentStep))
                }
            }
        }
    }

    /**
     * Observes the main game state and closes the tutorial overlay
     * when the state changes out of tutorial mode.
     */
    private fun observeGameState() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                gameViewModel.gameStateFlow.collect { state ->
                    if (state != GameStateType.Tutorial) {
                        parentFragmentManager.popBackStack()
                    }
                }
            }
        }
    }

    /**
     * Sets up the click listeners for the close and reset buttons.
     */
    private fun setupButtons() {
        binding.closeButton.setOnClickListener {
            parentFragmentManager.popBackStack()
            gameViewModel.handleAction(GameAction.ChangeState(GameStateType.Play))
        }

        binding.resetButton.setOnClickListener {
            gameViewModel.handleAction(GameAction.ResetLevel)
        }
    }

    /**
     * Handles the back press to close the tutorial overlay.
     */
    private fun handleBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    parentFragmentManager.popBackStack()
                }
            }
        )
    }
    /**
     * Factory function to create the correct InputFragment based on the tutorial input field type.
     */
    fun createInputFragmentForType(type: InputFieldType): InputFragment<*> = when (type) {
        InputFieldType.SEQUENCE -> InputSequenceFragment()
        InputFieldType.Loop -> InputLoopFragment()
        InputFieldType.IfElse -> InputIfElseFragment()
    }
}
