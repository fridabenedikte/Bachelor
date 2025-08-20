package com.emojigame.android.activities

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.IBinder
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.emojigame.android.R
import com.emojigame.android.databinding.ActivityLoadingBinding
import com.emojigame.android.ui.TextureStorage
import com.emojigame.android.util.CoreService
import com.emojigame.android.util.GameAction
import com.emojigame.android.util.GameViewModel
import com.emojigame.android.util.GameViewModelFactory
import com.emojigame.android.util.LevelProgressManager
import com.emojigame.core.states.GameStateType
import com.emojigame.core.ui.Theme
import kotlinx.coroutines.launch

/**
 * Responsible for loading game resources and transitioning to the main menu.
 *
 * Binds to [CoreService] to initialize sprites and sets screen data for game UI.
 */
class LoadingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoadingBinding

    private val gameViewModel: GameViewModel by viewModels {
        GameViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoadingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        LevelProgressManager.init(this)

        binding.theme = Theme

        startCoreService()
        bindCoreService()

        lifecycleScope.launch {
            gameViewModel.gameStateFlow.collect { state ->
                if (state == GameStateType.Menu) {
                    goToMainMenu()
                }
            }
        }
    }

    /**
     * Starts the core service as a foreground service
     */
    private fun startCoreService() {
        val serviceIntent = Intent(this, CoreService::class.java)
        startForegroundService(serviceIntent)
    }

    /**
     * Binds itself to the core service when when it has started
     */
    private fun bindCoreService() {
        val intent = Intent(this, CoreService::class.java)
        bindService(intent, serviceConnection, BIND_AUTO_CREATE)
    }

    /**
     * Monitors core service connection to handle state change when core is up and running
     */
    private val serviceConnection =
        object : ServiceConnection {
            override fun onServiceConnected(
                name: ComponentName?,
                service: IBinder?,
            ) {
                binding.loadingText.text = getString(R.string.loading_complete)

                loadSpriteSheet()
                loadCharacterSpriteSheet()

                gameViewModel.handleAction(GameAction.ChangeState(GameStateType.Menu))
            }

            override fun onServiceDisconnected(name: ComponentName?) {}
        }

    /** Loads the emoji sprite sheet into [TextureStorage]. */
    private fun loadSpriteSheet() {
        val options = BitmapFactory.Options().apply { inScaled = false }
        val spriteSheet = BitmapFactory.decodeResource(resources, com.emojigame.reslib.R.drawable.emojis, options)
        TextureStorage.emojiSpriteSheet = spriteSheet
    }

    /** Loads the character sprite sheet into [TextureStorage]. */
    private fun loadCharacterSpriteSheet() {
        val options = BitmapFactory.Options().apply { inScaled = false }
        val characterSheet = BitmapFactory.decodeResource(resources, com.emojigame.reslib.R.drawable.character_spritesheet, options)
        TextureStorage.characterBitmap = characterSheet
    }

    /** Transitions to the main menu after loading is complete. */
    private fun goToMainMenu() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("LEVEL_COUNT", com.emojigame.core.definitions.LevelDefinitions.allLevels.size)
        startActivity(intent)
        finish()
    }
}
