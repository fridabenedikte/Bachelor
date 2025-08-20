package com.emojigame.android.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.emojigame.android.databinding.ActivityRoadmapBinding
import com.emojigame.android.util.GameAction
import com.emojigame.android.util.GameViewModel
import com.emojigame.android.util.GameViewModelFactory
import com.emojigame.android.util.LevelProgressManager
import com.emojigame.core.states.GameStateType
import com.emojigame.core.util.Logger
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.launch

/**
 * Displays the roadmap where the player can choose a level.
 *
 * This activity shows which levels are unlocked and navigates to the selected level.
 */
class RoadmapActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRoadmapBinding
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private val unlockedLevels get() = LevelProgressManager.getUnlockedLevel()

    private val gameViewModel: GameViewModel by viewModels {
        GameViewModelFactory(application)
    }

    private var selectedLevelName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        binding = ActivityRoadmapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.roadmapCanvas.unlockedLevels = unlockedLevels

        observeGameState()
        setupCanvasInteraction()
        handleBackPress()
    }

    /**
     * Observes the current game state and reacts when transitioning to a new screen.
     */
    private fun observeGameState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                gameViewModel.gameStateFlow.collect { state ->
                    handleStateChange(state)
                }
            }
        }
    }

    /**
     * Sets up the roadmap canvas interaction, including click listeners on levels.
     */
    private fun setupCanvasInteraction() {
        binding.roadmapCanvas.apply {
            binding.roadmapCanvas.unlockedLevels = unlockedLevels
            onLevelClicked = { levelNumber ->
                selectedLevelName = "level$levelNumber"
                Logger.i { "Level clicked: $selectedLevelName" }

                firebaseAnalytics.logEvent(
                    "level_started",
                    Bundle().apply {
                        putString("level_name", selectedLevelName)
                    }
                )

                gameViewModel.handleAction(GameAction.ChangeState(GameStateType.Play))
            }
        }
    }

    /**
     * Overrides the default back button behavior to return to the main menu.
     */
    private fun handleBackPress() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Logger.i { "Back button PRESSED by user" }

                    val intent = Intent(this@RoadmapActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    gameViewModel.handleAction(GameAction.ChangeState(GameStateType.Menu))
                    finish()
                }
            }
        )
    }

    /**
     * Handles transitions based on [GameStateType], such as navigating to the level screen.
     */
    private fun handleStateChange(gameStateType: GameStateType) {
        Logger.i { "RoadmapActivity: Game State Changed to $gameStateType" }

        if (isFinishing) return

        when (gameStateType) {
            GameStateType.Play -> {
                val intent = Intent(this, LevelActivity::class.java)
                selectedLevelName?.let { levelName ->
                    intent.putExtra("LEVEL_NAME", levelName)
                }
                val options = ActivityOptionsCompat.makeCustomAnimation(this, 0, 0)
                startActivity(intent, options.toBundle())
                finish()
            }

            GameStateType.Roadmap -> {
                Logger.i { "Already in RoadmapActivity, ignoring state change to Roadmap" }
            }

            else -> {
                Logger.i { "Unhandled state: $gameStateType" }
            }
        }
    }
}
