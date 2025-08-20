package com.emojigame.core.definitions

import com.emojigame.core.data.BackgroundElement
import com.emojigame.core.data.Component
import com.emojigame.core.data.Platform
import com.emojigame.core.data.PlatformTags
import com.emojigame.core.data.Tutorial
import com.emojigame.core.data.VisualStyle
import com.emojigame.core.data.tutorialSteps.TapAvailableEmojiStep
import com.emojigame.core.data.tutorialSteps.TapButtonStep
import com.emojigame.core.entities.Player
import com.emojigame.core.ui.InputFieldType
import com.emojigame.core.util.GameIntent
import com.emojigame.core.util.Vector2f

object TutorialDefinition : EmojiDefinitions {
    val Tutorial1 =
        Tutorial(
            name = "tutorial_to_level_1",
            emojis = emptyList(),
            player = Player(
                pos = Vector2f(100f, 500f),
                tag = "Player",
            ),
            platforms = listOf(
                Platform(
                    id = "ground-full",
                    pos = Vector2f(0f, 600f),
                    width = 1000f,
                    height = 400f,
                    tag = PlatformTags.Ground,
                    isEnabled = true
                )
            ),
            backgroundElements = emptyList(),
            components = listOf(
                Component.Gravity(listOf("Player")),
            ),
            steps = listOf(
                TapButtonStep(
                    GameIntent.PlayEmoji(emptyList()),
                    buttonId = "play_button",
                )
            ),
            InputFieldType.SEQUENCE
        )

    val Tutorial2 =
        Tutorial(
            name = "tutorial_to_level_2",
            emojis = listOf(
                Wood
            ),
            player = Player(
                pos = Vector2f(100f, 500f),
                tag = "Player",
            ),
            platforms = listOf(
                Platform(
                    id = "ground-left",
                    pos = Vector2f(0f, 600f),
                    width = 300f,
                    height = 400f,
                    tag = PlatformTags.Ground,
                    isEnabled = true
                ),
                Platform(
                    id = "bridge-1",
                    pos = Vector2f(300f, 600f),
                    width = 400f,
                    height = 30f,
                    tag = PlatformTags.Bridge,
                    isEnabled = false
                ),
                Platform(
                    id = "ground-right",
                    pos = Vector2f(700f, 600f),
                    width = 300f,
                    height = 400f,
                    tag = PlatformTags.Ground,
                    isEnabled = true
                )
            ),
            backgroundElements = listOf(
                BackgroundElement(
                    pos = Vector2f(300f, 700f),
                    width = 400f,
                    height = 300f,
                    tag = PlatformTags.Water,
                    isEnabled = true
                )
            ),
            components = listOf(
                Component.Gravity(listOf("Player")),
                Component.EmojiActivatesPlatform(Wood, "bridge-1"),
            ),
            steps = listOf(
                TapAvailableEmojiStep(
                    GameIntent.SelectEmoji(Wood),
                    emoji = Wood
                ),
                TapButtonStep(
                    GameIntent.PlayEmoji(listOf(Wood)),
                    buttonId = "play_button"
                )
            ),
            inputFieldType = InputFieldType.SEQUENCE
        )

    val Tutorial4 =
        Tutorial(
            name = "tutorial_to_level_4",
            emojis = listOf(
                Wood
            ),
            player = Player(
                pos = Vector2f(70f, 500f),
                tag = "Player",
            ),
            platforms = listOf(
                Platform(
                    id = "ground-left",
                    pos = Vector2f(0f, 600f),
                    width = 300f,
                    height = 400f,
                    tag = PlatformTags.Ground,
                    isEnabled = true
                ),
                Platform(
                    id = "bridge-1",
                    pos = Vector2f(300f, 600f),
                    width = 400f,
                    height = 30f,
                    tag = PlatformTags.Bridge,
                    isEnabled = false
                ),
                Platform(
                    id = "ground-right",
                    pos = Vector2f(700f, 600f),
                    width = 300f,
                    height = 400f,
                    tag = PlatformTags.Ground,
                    isEnabled = true
                )
            ),
            backgroundElements = listOf(
                BackgroundElement(
                    pos = Vector2f(300f, 700f),
                    width = 400f,
                    height = 300f,
                    tag = PlatformTags.Water,
                    isEnabled = true
                )
            ),
            components = listOf(
                Component.Gravity(listOf("Player")),
                Component.EmojiActivatesPlatform(Wood, "bridge-1"),
            ),
            steps = listOf(
                TapAvailableEmojiStep(
                    GameIntent.SelectEmoji(Wood),
                    emoji = Wood
                ),
                TapButtonStep(
                    GameIntent.OpenRepeatCountDialog,
                    buttonId = "emoji_wheel_center"
                ),
                TapButtonStep(
                    GameIntent.UpdateRepeatCount(1),
                    buttonId = "number_picker_confirm"
                ),
                TapButtonStep(
                    GameIntent.PlayEmoji(listOf(Wood)),
                    buttonId = "play_button"
                )
            ),
            inputFieldType = InputFieldType.Loop
        )

    val Tutorial5 =
        Tutorial(
            name = "tutorial_to_level_5",
            emojis = listOf(
                Hand,
                Apple,
            ),
            player = Player(
                pos = Vector2f(100f, 500f),
                tag = "Player",
            ),
            platforms = listOf(
                Platform(
                    id = "ground",
                    pos = Vector2f(0f, 600f),
                    width = 1000f,
                    height = 400f,
                    tag = PlatformTags.Ground,
                    isEnabled = true
                ),
                Platform(
                    id = "box-apple",
                    pos = Vector2f(500f, 520f),
                    width = 100f,
                    height = 80f,
                    tag = PlatformTags.Obstacle,
                    isEnabled = true,
                    visualStyle = VisualStyle(
                        emoji = Apple
                    )
                )
            ),
            backgroundElements = emptyList(),
            components = listOf(
                Component.Gravity(listOf("Player")),

                Component.EmojiSequenceRemovesPlatform(
                    emojiSequence = listOf(Hand, Apple),
                    platformId = "box-apple"
                )
            ),
            steps = listOf(
                TapAvailableEmojiStep(
                    GameIntent.SelectEmoji(Hand),
                    emoji = Hand
                ),
                TapAvailableEmojiStep(
                    GameIntent.SelectEmoji(Apple),
                    emoji = Apple
                ),
                TapButtonStep(
                    GameIntent.PlayEmoji(listOf(Hand, Apple)),
                    buttonId = "play_button"
                )
            ),
            inputFieldType = InputFieldType.SEQUENCE
        )

    val allTutorial: MutableList<Tutorial> = mutableListOf(Tutorial1, Tutorial2, Tutorial5)
    val byName: MutableMap<String, Tutorial> = allTutorial.associateBy { it.name }.toMutableMap()

    fun get(name: String): Tutorial? = byName[name]
}
