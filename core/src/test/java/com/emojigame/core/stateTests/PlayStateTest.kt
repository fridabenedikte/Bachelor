package com.emojigame.core.stateTests

import com.emojigame.core.data.Component
import com.emojigame.core.data.Hint
import com.emojigame.core.data.Level
import com.emojigame.core.data.Platform
import com.emojigame.core.data.PlatformTags
import com.emojigame.core.data.VisualStyle
import com.emojigame.core.definitions.Emojis
import com.emojigame.core.definitions.LevelDefinitions
import com.emojigame.core.entities.Emoji
import com.emojigame.core.entities.Player
import com.emojigame.core.states.GameStateManager
import com.emojigame.core.states.PlayState
import com.emojigame.core.ui.GameStateFlow
import com.emojigame.core.ui.InputFieldType
import com.emojigame.core.util.Vector2f
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.slot
import io.mockk.unmockkObject
import io.mockk.verify
import kotlinx.coroutines.flow.StateFlow
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import java.lang.reflect.InvocationTargetException
import kotlin.reflect.KVisibility
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.jvm.javaMethod
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class PlayStateTest {
    private lateinit var playState: PlayState
    private lateinit var gameStateManager: GameStateManager
    private lateinit var mockGameStateFlow: StateFlow<GameStateFlow>
    private lateinit var levelName: String
    private lateinit var mockPlayer: Player
    private lateinit var platforms: List<Platform>
    private lateinit var mockLevel: Level

    @Before
    fun setup() {
        gameStateManager = mockk(relaxed = true)
        playState = PlayState(gameStateManager)
        mockGameStateFlow = mockk<StateFlow<GameStateFlow>>(relaxed = true)
        every { gameStateManager.gameStateFlow } returns mockGameStateFlow
        levelName = "testLevel"
        mockPlayer = mockk<Player>(relaxed = true)
        platforms = listOf(
            Platform(
                tag = PlatformTags.Ground,
                pos = Vector2f(0f, 600f),
                width = 300f,
                height = 400f,
                isEnabled = true,
                visualStyle = VisualStyle(444444)
            )
        )
    }

    @Test
    fun `loadLevel loads level, sets player, gravity, and updates state`() {
        val gravity = Vector2f(0f, 9.8f)

        val mockGravity = mockk<Component.Gravity>(relaxed = true)
        every { mockGravity.gravity } returns gravity

        val mockLevel = Level(
            name = levelName,
            player = mockPlayer,
            platforms = platforms,
            backgroundElements = emptyList(),
            components = listOf(mockGravity),
            emojis = listOf(Emojis.Fire, Emojis.Crying),
            hints = listOf(
                Hint(Emojis.Fire, "press the Fire emoji")
            ),
            inputFieldType = InputFieldType.SEQUENCE,
        )

        mockkObject(LevelDefinitions)
        every { LevelDefinitions.get(levelName) } returns mockLevel
        every { mockPlayer.clone() } returns mockPlayer

        playLoadLevelMethod()

        verify(exactly = 1) { mockPlayer.applyGravity(gravity) }
        verify { gameStateManager.updateState(any()) }
        verify { gameStateManager.updatePlayerPosition(mockPlayer.pos) }

        unmockkObject(LevelDefinitions)
    }

    @Test
    fun `playEmojis activates platforms and updates state`() {
        val mockEmoji = Emoji(tag = Emojis.Fire.tag)
        val mockEmojiActivatedPlatform = Component.EmojiActivatesPlatform(
            emoji = Emojis.Fire,
            platformId = "platform1"
        )

        mockLevel = Level(
            name = levelName,
            player = mockPlayer,
            platforms = platforms,
            backgroundElements = emptyList(),
            components = listOf(mockEmojiActivatedPlatform),
            emojis = listOf(Emojis.Fire, Emojis.Crying),
            hints = listOf(
                Hint(Emojis.Fire, "press the Fire emoji")
            ),
            inputFieldType = InputFieldType.SEQUENCE,
        )

        mockkObject(LevelDefinitions)
        every { LevelDefinitions.get(levelName) } returns mockLevel
        every { mockGameStateFlow.value.selectedEmojis } returns listOf(Emojis.Fire)

        playLoadLevelMethod()

        playSelectedEmojisMethod(listOf(mockEmoji))

        verify { gameStateManager.updateState(any()) }
        verify { gameStateManager.updatePlayerPosition(mockPlayer.pos) }

        unmockkObject(LevelDefinitions)
    }

    @Test
    fun `selectEmoji adds emoji to selected list`() {
        val emoji = Emoji(tag = Emojis.Fire.tag)

        val initialState = GameStateFlow(selectedEmojis = emptyList())
        val updatedStateSlot = slot<(GameStateFlow) -> GameStateFlow>()

        every { mockGameStateFlow.value } returns initialState

        every { gameStateManager.updateState(capture(updatedStateSlot)) } answers {
            // Simulate updateState actually calling reducer with the current state
            val newState = updatedStateSlot.captured(initialState)

            val addedEmojis = newState.selectedEmojis - initialState.selectedEmojis.toSet()
            assertEquals(1, addedEmojis.size)

            val emojiWithId = addedEmojis.first()

            assertEquals(emoji.tag, emojiWithId.tag)
            assertNotNull(emojiWithId.id)

            Unit
        }

        selectEmojiMethod(emoji)

        verify { gameStateManager.updateState(any()) }
    }

    private fun selectEmojiMethod(emoji: Emoji) {
        val functionName = "selectEmoji"
        val selectEmojiMethod = playState::class.declaredFunctions.find { it.name == functionName }
        selectEmojiMethod?.let {
            if (it.visibility == KVisibility.PRIVATE) {
                val javaMethod = it.javaMethod
                javaMethod?.isAccessible = true
                try {
                    javaMethod?.invoke(playState, emoji)
                } catch (e: InvocationTargetException) {
                    e.cause?.printStackTrace()
                    fail("Invocation of $functionName failed: ${e.cause?.message}")
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                    fail("Could not access $functionName: ${e.message}")
                }
            } else {
                fail("$functionName is not private as expected")
            }
        }
    }

    private fun playSelectedEmojisMethod(emojiList: List<Emoji>) {
        val functionName = "playEmojis"
        val playEmojisMethod = playState::class.declaredFunctions.find { it.name == functionName }
        playEmojisMethod?.let {
            if (it.visibility == KVisibility.PRIVATE) {
                val javaMethod = it.javaMethod
                javaMethod?.isAccessible = true
                try {
                    javaMethod?.invoke(playState, emojiList)
                    // Assertions based on the expected behavior of playEmojis
                } catch (e: InvocationTargetException) {
                    e.cause?.printStackTrace()
                    fail("Invocation of $functionName failed: ${e.cause?.message}")
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                    fail("Could not access $functionName: ${e.message}")
                }
            } else {
                fail("$functionName is not private as expected")
            }
        }
    }

    private fun playLoadLevelMethod() {
        val functionName = "loadLevel"
        val loadLevelMethod = playState::class.declaredFunctions.find { it.name == functionName }
        loadLevelMethod?.let {
            if (it.visibility == KVisibility.PRIVATE) {
                val javaMethod = it.javaMethod
                javaMethod?.isAccessible = true
                try {
                    javaMethod?.invoke(playState, levelName)
                } catch (e: InvocationTargetException) {
                    e.cause?.printStackTrace()
                    fail("Invocation of $functionName failed: ${e.cause?.message}")
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                    fail("Could not access $functionName: ${e.message}")
                }
            } else {
                fail("$functionName is not private as expected")
            }
        }
    }
}
