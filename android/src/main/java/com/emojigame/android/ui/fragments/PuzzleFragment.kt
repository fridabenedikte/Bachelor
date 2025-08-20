package com.emojigame.android.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.emojigame.android.databinding.FragmentPuzzleBinding
import com.emojigame.android.ui.BitmapAnimationHandler
import com.emojigame.android.ui.TextureStorage
import com.emojigame.android.util.GameViewModel
import com.emojigame.core.entities.Player
import com.emojigame.core.ui.BaseStateFlow
import com.emojigame.core.ui.Theme
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Fragment responsible for rendering the puzzle canvas.
 * Observes both player position and the current UI state to update the canvas.
 */
class PuzzleFragment : Fragment() {
    private var _binding: FragmentPuzzleBinding? = null
    private val binding get() = _binding!!

    private lateinit var animationHandler: BitmapAnimationHandler
    private lateinit var stateFlow: StateFlow<BaseStateFlow>

    private val gameViewModel: GameViewModel
        get() = ViewModelProvider(requireActivity())[GameViewModel::class.java]

    /**
     * Allows external code to override the observed state flow (e.g. for tutorial mode).
     */
    fun setObservedState(flow: StateFlow<BaseStateFlow>) {
        this.stateFlow = flow
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAnimationHandler()
        if (!::stateFlow.isInitialized) {
            stateFlow = gameViewModel.uiState
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPuzzleBinding.inflate(inflater, container, false)
        binding.theme = Theme
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observePlatformAndBackgroundUpdates()
        observePlayerPositionAndAnimate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Initializes the [BitmapAnimationHandler] used for the player animation.
     * Throws an exception if the bitmap hasn't been loaded in [TextureStorage].
     */
    private fun initAnimationHandler() {
        val characterBitmap = TextureStorage.characterBitmap
            ?: throw IllegalStateException("Character bitmap not loaded in LoadingActivity")

        animationHandler = BitmapAnimationHandler(
            bitmap = characterBitmap,
            frameCount = 20,
            frameDurationMs = 100
        )
    }

    /**
     * Observes platform and background changes and updates the canvas accordingly.
     */
    private fun observePlatformAndBackgroundUpdates() {
        val puzzleCanvas = binding.puzzleCanvasView
        viewLifecycleOwner.lifecycleScope.launch {
            stateFlow.collect { uiState ->
                puzzleCanvas.updatePlatforms(uiState.platforms)
                puzzleCanvas.updateBackgroundElements(uiState.backgroundElements)
                puzzleCanvas.redraw()
            }
        }
    }

    /**
     * Observes player position changes and updates the player position and animation frame.
     */
    private fun observePlayerPositionAndAnimate() {
        val puzzleCanvas = binding.puzzleCanvasView
        viewLifecycleOwner.lifecycleScope.launch {
            gameViewModel.playerPosState.collect { position ->
                puzzleCanvas.updatePlayerPosition(position)

                val movementState = stateFlow.value.player?.movementState
                val frame = when (movementState) {
                    Player.MovementState.MOVING -> animationHandler.getCurrentFrame()
                    Player.MovementState.IDLE, null -> animationHandler.getIdleFrame()
                }

                puzzleCanvas.setPlayerBitmap(frame)
                puzzleCanvas.redraw()
            }
        }
    }
}
