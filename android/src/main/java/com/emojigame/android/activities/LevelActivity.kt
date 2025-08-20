package com.emojigame.android.activities

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Surface
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.airbnb.lottie.LottieAnimationView
import com.emojigame.android.R
import com.emojigame.android.databinding.ActivityLevelBinding
import com.emojigame.android.databinding.IncludeButtonBarBinding
import com.emojigame.android.ui.fragments.InputIfElseFragment
import com.emojigame.android.ui.fragments.InputLoopFragment
import com.emojigame.android.ui.fragments.InputSequenceFragment
import com.emojigame.android.ui.fragments.PuzzleFragment
import com.emojigame.android.ui.fragments.TutorialOverlayFragment
import com.emojigame.android.ui.fragments.dialogs.GameOverDialogFragment
import com.emojigame.android.ui.fragments.dialogs.GameWonDialogFragment
import com.emojigame.android.ui.fragments.dialogs.PauseMenuDialogFragment
import com.emojigame.android.util.DeviceSizeData
import com.emojigame.android.util.GameAction
import com.emojigame.android.util.GameViewModel
import com.emojigame.android.util.GameViewModelFactory
import com.emojigame.android.util.InputType
import com.emojigame.android.util.LevelProgressManager
import com.emojigame.core.states.GameStateType
import com.emojigame.core.ui.InputFieldType
import com.emojigame.core.ui.Theme
import com.emojigame.core.util.Logger
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.launch

/**
 * Activity that displays the game level screen.
 *
 * This activity initializes the game, loads a level, and listens for game state changes.
 */
class LevelActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLevelBinding
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var buttonBarBinding: IncludeButtonBarBinding
    private var tutorialOverlayFragment: TutorialOverlayFragment? = null
    private lateinit var levelName: String
    private lateinit var inputFragmentContainer: View
    private lateinit var puzzleFragmentContainer: View
    private lateinit var topBarInset: View
    private var currentOrientation: Int = Configuration.ORIENTATION_PORTRAIT
    private var attemptCount: Int = 1
    private var currentInputType: InputType = InputType.SEQUENCE
    private val inputTypeViewMap: Map<InputType, Int> by lazy {
        mapOf(
            InputType.SEQUENCE to R.id.sequence_input_fragment,
            InputType.LOOP to R.id.loop_input_fragment,
            InputType.IFELSE to R.id.if_else_input_fragment
        )
    }

    /**
     * ViewModel for managing game state and actions. Created using a custom factory.
     */
    val gameViewModel: GameViewModel by viewModels {
        GameViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        Logger.i { "Initializing game view model" }
        gameViewModel.initialize()

        levelName = intent.getStringExtra("LEVEL_NAME") ?: "level1"
        gameViewModel.handleAction(GameAction.LoadLevel(levelName))

        setupUI()
    }

    /**
     * This function overwrites Android's native rotation mechanics, as Android by default recreates
     *     the activity when reloading. This means that the current UI-state of the game is not
     *     saved. By including android:configChanges="orientation|screenSize" in the Level Activity
     *     in the Android Manifest, this function will run every time the app rotates. That means
     *     we'll easily be able to save the level's current status (also player position after
     *     clicking play etc.) As we don't have a need to store those already, thus would be tedious
     *     to implement for such a rare case.
     *
     *     In general it is recommended to use Android's native recreation upon rotation, as
     *     manually overwriting the fragments can be tedious and error prone. But this can be up for
     *     future developers to decide. This way of implementing it makes it a lot more flexible.
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        try {
            setupUI()
        } catch (e: Exception) {
            Logger.e { "Error setting up UI:\n$e" }
        }

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.puzzle_fragment_container, PuzzleFragment())
            replace(R.id.sequence_input_fragment, InputSequenceFragment())
            replace(R.id.loop_input_fragment, InputLoopFragment())
            replace(R.id.if_else_input_fragment, InputIfElseFragment())
            commitNow()
        }
        changeInputFragment(currentInputType)
    }

    /**
     * Sets up the UI elements and listeners for the level screen.
     */
    private fun setupUI() {
        binding = ActivityLevelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.theme = Theme
        buttonBarBinding = binding.buttonBar

        currentOrientation = resources.configuration.orientation
        observeGameState()
        observeLevelLoaded()
        initializeFragments()
        setFragmentSizes()
        handleBackPress()

        buttonBarBinding.btnPause.setOnClickListener {
            gameViewModel.handleAction(GameAction.PauseGame)
            PauseMenuDialogFragment().show(supportFragmentManager, "PauseMenu")
        }

        buttonBarBinding.btnHint.setOnClickListener {
            gameViewModel.handleAction(GameAction.ShowHint)
        }
    }

    /**
     * Changes the input fragment based on the selected input type. Internal because the switching
     * fragment needs to be able to call this function. This fragment is currently running even
     * though the fragment may be unchanged, which happens after reload w/o activity recreation
     */
    internal fun changeInputFragment(inputType: InputType) {
        currentInputType = inputType
        inputTypeViewMap.forEach { (type, viewId) ->
            val container = findViewById<View>(viewId)
            container.visibility = if (type == inputType) View.VISIBLE else View.GONE
            Logger.d { "Container: ${container.id}, Visibility: ${container.visibility}" }
        }
        Logger.d { "Current input type: $currentInputType" }
    }

    /**
     * Initializes references to fragment container views.
     */
    private fun initializeFragments() {
        inputFragmentContainer = findViewById(R.id.input_fragment_container)
        puzzleFragmentContainer = findViewById(R.id.puzzle_fragment_container)
        topBarInset = findViewById(R.id.puzzle_top_bar_inset)
    }

    /**
     * Updates current [InputFieldType] that is active
     */
    private fun updateInputFieldType(inputFieldType: InputFieldType) {
        when (inputFieldType) {
            InputFieldType.SEQUENCE -> changeInputFragment(InputType.SEQUENCE)
            InputFieldType.Loop -> changeInputFragment(InputType.LOOP)
            InputFieldType.IfElse -> changeInputFragment(InputType.IFELSE)
        }
    }

    /**
     * Sets the size of the fragments based on the device screen scale data.
     */
    private fun setFragmentSizes() {
        topBarInset.layoutParams.height = DeviceSizeData.topBarSize
        when (currentOrientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                inputFragmentContainer.layoutParams.height = DeviceSizeData.portraitInputFragmentHeight
                puzzleFragmentContainer.layoutParams.height = DeviceSizeData.portraitPuzzleFragmentHeight
                val gestureBarInset = findViewById<View>(R.id.gestureInputBackground)
                gestureBarInset.layoutParams.height = DeviceSizeData.bottomBarSize
            }
            Configuration.ORIENTATION_LANDSCAPE -> {
                val topBarInputInset = findViewById<View>(R.id.input_top_bar_inset)
                topBarInputInset.layoutParams.height = DeviceSizeData.topBarSize
                inputFragmentContainer.layoutParams.width = DeviceSizeData.landscapeInputFragmentWidth
                inputFragmentContainer.layoutParams.height = DeviceSizeData.landscapeInputFragmentHeight
                puzzleFragmentContainer.layoutParams.width = DeviceSizeData.landscapePuzzleFragmentWidth
                puzzleFragmentContainer.layoutParams.height = if (DeviceSizeData.isGestureModeEnabled) DeviceSizeData.minorDimension - DeviceSizeData.topBarSize else DeviceSizeData.landscapePuzzleFragmentHeight

                if (display.rotation == Surface.ROTATION_270 && !DeviceSizeData.isTablet(this) && !DeviceSizeData.isGestureModeEnabled) {
                    val verticalInsetBar = findViewById<View>(R.id.vertical_inset_bar)
                    verticalInsetBar.layoutParams.width = DeviceSizeData.navBarSize
                }
            }
            else -> {
                Logger.e { "Attempted switch to unrecognized orientation" }
            }
        }
    }

    /**
     * Observes game state changes and handles UI updates accordingly.
     */
    private fun observeGameState() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                gameViewModel.gameStateFlow.collect { state ->
                    handleStateChange(state)
                    Logger.i { "State: $state" }
                }
            }
        }
    }

    /**
     * Observes level load state and updates the input field type accordingly.
     */
    private fun observeLevelLoaded() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                gameViewModel.uiState.collect { uiState ->
                    val level = uiState.level ?: return@collect
                    updateInputFieldType(level.inputFieldType)
                }
            }
        }
    }

    /**
     * Handles back button press to switch game state properly.
     */
    private fun handleBackPress() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Logger.i { "Back button pressed, going to Roadmap" }
                    val intent = Intent(this@LevelActivity, RoadmapActivity::class.java)
                    val options = ActivityOptionsCompat.makeCustomAnimation(this@LevelActivity, 0, 0)
                    startActivity(intent, options.toBundle())
                    gameViewModel.handleAction(GameAction.ChangeState(GameStateType.Roadmap))
                    finish()
                }
            }
        )
    }

    /**
     * Displays the tutorial overlay fragment.
     */
    private fun showTutorialOverlay() {
        if (tutorialOverlayFragment == null) {
            tutorialOverlayFragment = TutorialOverlayFragment()
            supportFragmentManager.commit {
                add(R.id.tutorial_overlay_container, tutorialOverlayFragment!!)
                addToBackStack(null)
            }
        }
    }

    /**
     * Removes the tutorial overlay fragment from the view hierarchy.
     */
    private fun removeTutorialOverlay() {
        tutorialOverlayFragment?.let {
            supportFragmentManager.beginTransaction().remove(it).commit()
            tutorialOverlayFragment = null
        }
    }

    /**
     * Handles UI updates and logic transitions based on the current [GameStateType].
     */
    private fun handleStateChange(gameStateType: GameStateType) {
        Logger.i { "UI Updated: Current State = $gameStateType, Level = $levelName" }
        val lottieAnimationView: LottieAnimationView = findViewById(R.id.lottieAnimationView)
        var posted = true

        when (gameStateType) {
            GameStateType.Menu -> {
                if (!isFinishing) {
                    Logger.i { "Switching to Main Menu" }
                    val intent = Intent(this, MainActivity::class.java)
                    val options = ActivityOptionsCompat.makeCustomAnimation(this, 0, 0)
                    startActivity(intent, options.toBundle())
                    finish()
                }
            }

            GameStateType.Roadmap -> {
                if (!isFinishing) {
                    Logger.i { "Switching to RoadmapActivity" }
                    val intent = Intent(this, RoadmapActivity::class.java)
                    val options = ActivityOptionsCompat.makeCustomAnimation(this, 0, 0)
                    startActivity(intent, options.toBundle())
                    finish()
                }
            }

            GameStateType.Play -> {
                removeTutorialOverlay()
            }

            GameStateType.GameWon -> {
                lottieAnimationView.visibility = View.VISIBLE
                lottieAnimationView.playAnimation()
                lifecycleScope.launch {
                    kotlinx.coroutines.delay(1100)
                    showGameWonDialog()
                }
                onLevelCompleted()
            }

            GameStateType.GameOver -> {
                Logger.i { "💀 Game Over triggered" }
                onLevelFailed()
                showGameOverDialog()
            }

            GameStateType.Tutorial -> {
                showTutorialOverlay()
            }

            else -> {
                Logger.i { "Remaining in LevelActivity" }
            }
        }

        lottieAnimationView.addAnimatorListener(object : AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                posted = false
            }

            override fun onAnimationEnd(animation: Animator) {
                lottieAnimationView.visibility = View.GONE
                if (!posted) {
                    posted = true
                }
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
    }

    /**
     * Displays the game won dialog fragment.
     */
    private fun showGameWonDialog() {
        supportFragmentManager.findFragmentByTag("GameWonDialogFragment")?.let {
            (it as? DialogFragment)?.dismissAllowingStateLoss()
        }
        GameWonDialogFragment().show(supportFragmentManager, "GameWonDialogFragment")
    }

    /**
     * Displays the game over dialog fragment.
     */
    private fun showGameOverDialog() {
        val dialog = GameOverDialogFragment()
        dialog.show(supportFragmentManager, "GameOverDialogFragment")
    }

    /**
     * Logs the failed level event and increments the attempt count.
     */
    private fun onLevelFailed() {
        firebaseAnalytics.logEvent(
            "level_failed",
            bundleOf(
                "level_name" to levelName,
                "attempt" to attemptCount
            )
        )
        attemptCount++
    }

    /**
     * Logs the completed level event and resets the attempt count.
     * Also unlocks the next level if applicable.
     */
    private fun onLevelCompleted() {
        firebaseAnalytics.logEvent(
            "level_completed",
            bundleOf(
                "level_name" to levelName,
                "attempts" to attemptCount
            )
        )

        attemptCount = 1

        val currentLevelNumber = levelName.removePrefix("level").toIntOrNull()
        if (currentLevelNumber != null) {
            LevelProgressManager.unlockNextLevel(currentLevelNumber)
        }
    }
}
