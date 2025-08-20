package com.emojigame.android.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.emojigame.android.R
import com.emojigame.android.databinding.ActivityMainMenuBinding
import com.emojigame.android.ui.fragments.dialogs.SettingsDialogFragment
import com.emojigame.android.util.DeviceSizeData
import com.emojigame.android.util.GameAction
import com.emojigame.android.util.GameViewModel
import com.emojigame.android.util.GameViewModelFactory
import com.emojigame.android.util.MusicManager
import com.emojigame.android.util.MusicService
import com.emojigame.core.states.GameStateType
import com.emojigame.core.ui.Theme
import com.emojigame.core.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.max

/**
 * The main entry point of the application.
 * Displays the main menu and handles navigation to the Roadmap screen.
 */
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainMenuBinding
    private var levelCount: Int = 0

    private val gameViewModel: GameViewModel by viewModels {
        GameViewModelFactory(application)
    }

    private var stateObserverJob: Job? = null
    private var isMusicPlaying = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBinding()
        startSizeInitialization()
        setupObservers()
        startBackgroundMusicIfNeeded()
        setupButtons()
        setupBackPress()
        handleIntentData()
    }

    /**
     * Inflates layout and applies theme.
     */
    private fun setupBinding() {
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.theme = Theme
    }

    /**
     * Initializes device size data on a background thread.
     */
    private fun startSizeInitialization() {
        CoroutineScope(Dispatchers.Default).launch {
            initializeDeviceSizeData(this@MainActivity)
        }
    }

    /**
     * Starts observers for game state and music playback.
     */
    private fun setupObservers() {
        observeGameState()
        observeMusicState()
    }

    /**
     * Starts background music service if music is already playing.
     */
    private fun startBackgroundMusicIfNeeded() {
        if (MusicManager.isPlaying) {
            val musicIntent = Intent(this, MusicService::class.java).apply {
                action = "START"
            }
            startService(musicIntent)
        }
    }

    /**
     * Configures click listeners for buttons in the main menu.
     */
    private fun setupButtons() {
        binding.btnMusicToggle.setOnClickListener {
            val turningMusicOn = !MusicManager.isPlaying
            val action = if (turningMusicOn) "START" else "PAUSE"

            val intent = Intent(this, MusicService::class.java).apply {
                this.action = action
            }
            startService(intent)
            MusicManager.isPlaying = turningMusicOn
        }

        binding.btnSettings.setOnClickListener {
            val settingsDialog = SettingsDialogFragment()
            settingsDialog.show(supportFragmentManager, "SettingsDialog")
        }

        binding.btnStartGame.setOnClickListener {
            Logger.i { "Start button clicked, changing state to Roadmap" }
            if (gameViewModel.gameStateFlow.value == GameStateType.Menu) {
                gameViewModel.handleAction(GameAction.ChangeState(GameStateType.Roadmap))
            }
        }
        binding.btnExit.setOnClickListener {
            finishAffinity()
        }
    }

    /**
     * Overrides back button behavior to exit the app.
     */
    private fun setupBackPress() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    finishAffinity()
                }
            }
        )
    }

    /**
     * Retrieves extras from the intent.
     */
    private fun handleIntentData() {
        levelCount = intent.getIntExtra("LEVEL_COUNT", 0)
    }

    /**
     * Initializes the [DeviceSizeData] by calculating insets and screen dimensions.
     */
    private suspend fun initializeDeviceSizeData(context: Context) {
        Logger.d { "Initializing DeviceSizeData" }
        withContext(Dispatchers.Default) {
            Logger.d { "Dispatchers.IO trigger" }
            ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
                val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                val gestureInsets = insets.getInsets(WindowInsetsCompat.Type.systemGestures())

                Logger.d { "Gesture insets bottom: ${gestureInsets.bottom}" }
                Logger.d { "System bar bottom: ${systemBarsInsets.bottom}" }
                Logger.d { "Insets visible: ${insets.isVisible(WindowInsetsCompat.Type.navigationBars())}" }

                val isGestureNav = !insets.isVisible(WindowInsetsCompat.Type.navigationBars()) ||
                    gestureInsets.bottom > systemBarsInsets.bottom
                Logger.d { "Is gesture nav: $isGestureNav" }

                DeviceSizeData.isGestureModeEnabled = isGestureNav

                val leftBarInset = systemBarsInsets.left
                val topBarInset = systemBarsInsets.top
                val rightBarInset = systemBarsInsets.right
                val bottomBarInset = if (isGestureNav) gestureInsets.bottom else systemBarsInsets.bottom
                DeviceSizeData.leftBarSize = leftBarInset
                DeviceSizeData.rightBarSize = rightBarInset
                DeviceSizeData.bottomBarSize = bottomBarInset
                DeviceSizeData.topBarSize = topBarInset
                DeviceSizeData.navBarSize = max(max(leftBarInset, rightBarInset), bottomBarInset)
                Logger.d { "Calculating dimensions" }
                DeviceSizeData.calculateDimensions(
                    width = resources.displayMetrics.widthPixels,
                    height = resources.displayMetrics.heightPixels,
                    insetLeft = leftBarInset,
                    insetTop = topBarInset,
                    insetRight = rightBarInset,
                    insetBottom = bottomBarInset,
                    orientation = resources.configuration.orientation,
                    isTablet = DeviceSizeData.isTablet(context),
                    isGestureNav = isGestureNav
                )
                insets
            }
        }
    }

    /**
     * Observes the music playback state and updates the music toggle button accordingly.
     */
    private fun observeMusicState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                MusicManager.isPlayingFlow.collect { isPlaying ->
                    if (isPlaying) {
                        binding.btnMusicToggle.setImageResource(R.drawable.music_on)
                        binding.btnMusicToggle.contentDescription = getString(R.string.mute_music)
                    } else {
                        binding.btnMusicToggle.setImageResource(R.drawable.music_off)
                        binding.btnMusicToggle.contentDescription = getString(R.string.play_music)
                    }
                }
            }
        }
    }

    /**
     * Observes changes in game state and triggers UI updates or screen transitions.
     */
    private fun observeGameState() {
        stateObserverJob = lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                gameViewModel.gameStateFlow.collect { state ->
                    handleStateChange(state)
                }
            }
        }
    }

    /**
     * Handles logic based on the current [GameStateType], such as switching to the roadmap screen.
     */
    private fun handleStateChange(gameStateType: GameStateType) {
        Logger.i { "MainActivity: Game State Changed to $gameStateType" }

        if (isFinishing) return

        if (gameStateType == GameStateType.Roadmap) {
            Logger.i { "Switching to RoadmapActivity" }
            cleanupAndNavigate(RoadmapActivity::class.java)
        }
    }

    /**
     * Cancels listeners and navigates to the specified target activity.
     */
    private fun cleanupAndNavigate(target: Class<*>) {
        stateObserverJob?.cancel()
        val intent = Intent(this, target)
        val options = ActivityOptionsCompat.makeCustomAnimation(this, 0, 0)
        startActivity(intent, options.toBundle())
        finish()
    }
}
