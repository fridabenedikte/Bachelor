package com.emojigame.core.definitions

import com.emojigame.core.data.BackgroundElement
import com.emojigame.core.data.Component
import com.emojigame.core.data.Hint
import com.emojigame.core.data.Level
import com.emojigame.core.data.Platform
import com.emojigame.core.data.PlatformTags
import com.emojigame.core.data.VisualStyle
import com.emojigame.core.entities.Player
import com.emojigame.core.ui.InputFieldType
import com.emojigame.core.util.Vector2f

object LevelDefinitions : EmojiDefinitions {
    const val WIDTH = 1000f
    const val HEIGHT = 1000f

    val Level1 =
        Level(
            name = "level1",
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
            hints = emptyList(),
            inputFieldType = InputFieldType.SEQUENCE,
            tutorial = TutorialDefinition.Tutorial1
        )

    val Level2 =
        Level(
            name = "level2",
            emojis = listOf(
                Wood,
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
            hints = listOf(
                Hint(Wood, "press Wood emoji"),
            ),
            inputFieldType = InputFieldType.SEQUENCE,
            tutorial = TutorialDefinition.Tutorial2
        )

    val Level3 =
        Level(
            name = "level3",
            emojis = listOf(
                Wood,
            ),
            player = Player(
                pos = Vector2f(100f, 500f),
                tag = "Player",
            ),
            platforms = listOf(
                Platform(
                    id = "ground-left",
                    pos = Vector2f(0f, 600f),
                    width = 200f,
                    height = 400f,
                    tag = PlatformTags.Ground,
                    isEnabled = true
                ),
                Platform(
                    id = "bridge-1",
                    pos = Vector2f(200f, 600f),
                    width = 300f,
                    height = 30f,
                    tag = PlatformTags.Bridge,
                    isEnabled = false
                ),
                Platform(
                    id = "ground-middle",
                    pos = Vector2f(500f, 600f),
                    width = 100f,
                    height = 400f,
                    tag = PlatformTags.Ground,
                    isEnabled = true
                ),
                Platform(
                    id = "bridge-2",
                    pos = Vector2f(600f, 600f),
                    width = 300f,
                    height = 30f,
                    tag = PlatformTags.Bridge,
                    isEnabled = false
                ),
                Platform(
                    id = "ground-right",
                    pos = Vector2f(900f, 600f),
                    width = 100f,
                    height = 400f,
                    tag = PlatformTags.Ground,
                    isEnabled = true
                )
            ),
            backgroundElements = listOf(
                BackgroundElement(
                    pos = Vector2f(200f, 700f),
                    width = 300f,
                    height = 300f,
                    tag = PlatformTags.Water,
                    isEnabled = true
                ),
                BackgroundElement(
                    pos = Vector2f(600f, 700f),
                    width = 300f,
                    height = 300f,
                    tag = PlatformTags.Water,
                    isEnabled = true
                )
            ),
            components = listOf(
                Component.Gravity(listOf("Player")),
                Component.EmojiActivatesPlatform(Wood, "bridge-1"),
                Component.EmojiActivatesPlatform(Wood, "bridge-2"),
            ),
            hints = listOf(
                Hint(Wood, "press Wood emoji"),
                Hint(Wood, "press Wood emoji"),
            ),
            inputFieldType = InputFieldType.SEQUENCE,
        )

    val Level4 =
        Level(
            name = "level4",
            emojis = listOf(
                Wood,
            ),
            player = Player(
                pos = Vector2f(50f, 500f),
                tag = "Player",
            ),
            platforms = listOf(
                Platform("ground-1", Vector2f(0f, 600f), 150f, 400f, PlatformTags.Ground, true),
                Platform("bridge-1", Vector2f(150f, 600f), 200f, 30f, PlatformTags.Bridge, false),
                Platform("ground-2", Vector2f(350f, 600f), 100f, 400f, PlatformTags.Ground, true),
                Platform("bridge-2", Vector2f(450f, 600f), 200f, 30f, PlatformTags.Bridge, false),
                Platform("ground-3", Vector2f(650f, 600f), 100f, 400f, PlatformTags.Ground, true),
                Platform("bridge-3", Vector2f(750f, 600f), 200f, 30f, PlatformTags.Bridge, false),
                Platform("ground-4", Vector2f(950f, 600f), 100f, 400f, PlatformTags.Ground, true)
            ),
            backgroundElements = listOf(
                BackgroundElement(Vector2f(150f, 700f), 200f, 300f, PlatformTags.Water, true),
                BackgroundElement(Vector2f(450f, 700f), 200f, 300f, PlatformTags.Water, true),
                BackgroundElement(Vector2f(750f, 700f), 200f, 300f, PlatformTags.Water, true),
            ),
            components = listOf(
                Component.Gravity(listOf("Player")),
                Component.EmojiActivatesPlatform(Wood, "bridge-1"),
                Component.EmojiActivatesPlatform(Wood, "bridge-2"),
                Component.EmojiActivatesPlatform(Wood, "bridge-3"),
            ),
            hints = listOf(
                Hint(Wood, "press Wood emoji"),
                Hint(Wood, "press Wood emoji"),
                Hint(Wood, "press Wood emoji"),
            ),
            inputFieldType = InputFieldType.Loop,
            tutorial = TutorialDefinition.Tutorial4,
        )

    val Level5 =
        Level(
            name = "level5",
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
            hints = listOf(
                Hint(Hand, "press Hand emoji"),
                Hint(Apple, "press Apple emoji"),
            ),
            inputFieldType = InputFieldType.SEQUENCE,
            tutorial = TutorialDefinition.Tutorial5
        )

    val Level6 =
        Level(
            name = "level6",
            emojis = listOf(
                Hand,
                Apple,
            ),
            player = Player(
                pos = Vector2f(70f, 500f),
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
                    id = "box-apple-1",
                    pos = Vector2f(400f, 520f),
                    width = 100f,
                    height = 80f,
                    tag = PlatformTags.Obstacle,
                    isEnabled = true,
                    visualStyle = VisualStyle(emoji = Apple)
                ),
                Platform(
                    id = "box-apple-2",
                    pos = Vector2f(600f, 520f),
                    width = 100f,
                    height = 80f,
                    tag = PlatformTags.Obstacle,
                    isEnabled = true,
                    visualStyle = VisualStyle(emoji = Apple)
                )
            ),
            backgroundElements = emptyList(),
            components = listOf(
                Component.Gravity(listOf("Player")),

                Component.EmojiSequenceRemovesPlatform(
                    emojiSequence = listOf(Hand, Apple),
                    platformId = "box-apple-1"
                ),
                Component.EmojiSequenceRemovesPlatform(
                    emojiSequence = listOf(Hand, Apple),
                    platformId = "box-apple-2"
                )
            ),
            hints = listOf(
                Hint(Hand, "press Hand emoji"),
                Hint(Apple, "press Apple emoji"),
                Hint(Hand, "press Hand emoji"),
                Hint(Apple, "press Apple emoji"),
            ),
            inputFieldType = InputFieldType.SEQUENCE,
        )

    val Level7 =
        Level(
            name = "level7",
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
                    id = "box-apple-1",
                    pos = Vector2f(300f, 520f),
                    width = 100f,
                    height = 80f,
                    tag = PlatformTags.Obstacle,
                    isEnabled = true,
                    visualStyle = VisualStyle(emoji = Apple)
                ),
                Platform(
                    id = "box-apple-2",
                    pos = Vector2f(500f, 520f),
                    width = 100f,
                    height = 80f,
                    tag = PlatformTags.Obstacle,
                    isEnabled = true,
                    visualStyle = VisualStyle(emoji = Apple)
                ),
                Platform(
                    id = "box-apple-3",
                    pos = Vector2f(700f, 520f),
                    width = 100f,
                    height = 80f,
                    tag = PlatformTags.Obstacle,
                    isEnabled = true,
                    visualStyle = VisualStyle(emoji = Apple)
                ),
            ),
            backgroundElements = emptyList(),
            components = listOf(
                Component.Gravity(listOf("Player")),

                Component.EmojiSequenceRemovesPlatform(
                    emojiSequence = listOf(Hand, Apple),
                    platformId = "box-apple-1"
                ),
                Component.EmojiSequenceRemovesPlatform(
                    emojiSequence = listOf(Hand, Apple),
                    platformId = "box-apple-2"
                ),
                Component.EmojiSequenceRemovesPlatform(
                    emojiSequence = listOf(Hand, Apple),
                    platformId = "box-apple-3"
                )
            ),
            hints = listOf(
                Hint(Hand, "press Hand emoji"),
                Hint(Apple, "press Apple emoji"),
                Hint(Hand, "press Hand emoji"),
                Hint(Apple, "press Apple emoji"),
                Hint(Hand, "press Hand emoji"),
                Hint(Apple, "press Apple emoji"),
            ),
            inputFieldType = InputFieldType.Loop,
        )

    val Level8 =
        Level(
            name = "level8",
            ifOrElse = listOf(false),

            emojis = listOf(
                Walking,
                Standing,
            ),
            player = Player(
                pos = Vector2f(100f, 500f),
                tag = "Player",
            ),
            platforms = listOf(
                Platform("ground-1", Vector2f(0f, 600f), 450f, 400f, PlatformTags.Ground, true),
                Platform("bridge", Vector2f(450f, 600f), 300f, 30f, PlatformTags.Bridge, false),
                Platform("ground-2", Vector2f(750f, 600f), 350f, 400f, PlatformTags.Ground, true),
                Platform("block", Vector2f(350f, 570f), 0f, 30f, PlatformTags.Obstacle, false),
                Platform(
                    "greenLight",
                    Vector2f(380f, 450f),
                    90f,
                    120f,
                    PlatformTags.Unknown,
                    false,
                    visualStyle = VisualStyle(imageTag = "traffic_light_green")
                )
            ),

            backgroundElements = listOf(
                BackgroundElement(
                    Vector2f(380f, 450f),
                    90f,
                    120f,
                    PlatformTags.Unknown,
                    true,
                    visualStyle = VisualStyle(imageTag = "traffic_light_red")
                ),
            ),

            components = listOf(
                Component.Gravity(listOf("Player")),
                Component.IfOrElseActivatesPlatform(0, "bridge"),
                Component.IfOrElseActivatesPlatform(0, "greenLight"),
                Component.IfOrElseEmojiSetsPlatform(
                    ifOrElseIndex = 0,
                    ifTrueEmojiTag = Standing.tag,
                    ifTrueEmojiIndex = 0,
                    ifFalseEmojiTag = Standing.tag,
                    ifFalseEmojiIndex = 1,
                    platformId = "block",
                    shouldEnable = true
                ),
                Component.IfOrElseEmojiSetsPlatform(
                    0,
                    Walking.tag,
                    0,
                    Walking.tag,
                    1,
                    "block",
                    false
                ),
            ),
            hints = listOf(
                Hint(Walking, ""),
                Hint(Standing, ""),
            ),
            type = "ifElseTLight",
            inputFieldType = InputFieldType.IfElse,
            ifElsePhotos = listOf("traffic_light_green", "traffic_light_red")
        )

    val Level9 =
        Level(
            name = "level9",
            ifOrElse = listOf(false, false),
            emojis = listOf(
                Walking,
                Standing,
            ),
            player = Player(
                pos = Vector2f(100f, 500f),
                tag = "Player",
            ),
            platforms = listOf(
                Platform("ground-1", Vector2f(0f, 600f), 350f, 400f, PlatformTags.Ground, true),
                Platform("bridge-1", Vector2f(350f, 600f), 150f, 30f, PlatformTags.Bridge, false),
                Platform("ground-2", Vector2f(500f, 600f), 200f, 400f, PlatformTags.Ground, true),
                Platform("bridge-2", Vector2f(700f, 600f), 150f, 30f, PlatformTags.Bridge, false),
                Platform("ground-3", Vector2f(850f, 600f), 200f, 400f, PlatformTags.Ground, true),
                Platform("block-1", Vector2f(300f, 570f), 0f, 30f, PlatformTags.Obstacle, false),
                Platform("block-2", Vector2f(600f, 570f), 0f, 30f, PlatformTags.Obstacle, false),
                Platform(
                    "greenLight-1",
                    Vector2f(300f, 450f),
                    90f,
                    120f,
                    PlatformTags.Unknown,
                    false,
                    visualStyle = VisualStyle(imageTag = "traffic_light_green")
                ),
                Platform(
                    "greenLight-2",
                    Vector2f(600f, 450f),
                    90f,
                    120f,
                    PlatformTags.Unknown,
                    false,
                    visualStyle = VisualStyle(imageTag = "traffic_light_green")
                )
            ),

            backgroundElements = listOf(
                BackgroundElement(
                    Vector2f(300f, 450f),
                    90f,
                    120f,
                    PlatformTags.Unknown,
                    true,
                    visualStyle = VisualStyle(imageTag = "traffic_light_red")
                ),
                BackgroundElement(
                    Vector2f(600f, 450f),
                    90f,
                    120f,
                    PlatformTags.Unknown,
                    true,
                    visualStyle = VisualStyle(imageTag = "traffic_light_red")
                ),
            ),

            components = listOf(
                Component.Gravity(listOf("Player")),
                Component.IfOrElseActivatesPlatform(0, "bridge-1"),
                Component.IfOrElseActivatesPlatform(0, "greenLight-1"),
                Component.IfOrElseActivatesPlatform(1, "bridge-2"),
                Component.IfOrElseActivatesPlatform(1, "greenLight-2"),
                Component.IfOrElseEmojiSetsPlatform(0, Standing.tag, 0, Standing.tag, 1, "block-1", false),
                Component.IfOrElseEmojiSetsPlatform(0, Walking.tag, 0, Walking.tag, 1, "block-1", true),
                Component.IfOrElseEmojiSetsPlatform(1, Standing.tag, 0, Standing.tag, 1, "block-2", false),
                Component.IfOrElseEmojiSetsPlatform(1, Walking.tag, 0, Walking.tag, 1, "block-2", true),
            ),
            hints = listOf(
                Hint(Standing, ""),
                Hint(Walking, ""),
            ),
            type = "ifElseTLight",
            inputFieldType = InputFieldType.IfElse,
            ifElsePhotos = listOf("traffic_light_red", "traffic_light_green"),
        )

    val Level10 =
        Level(
            name = "level10",
            ifOrElse = listOf(true, false),

            emojis = listOf(
                Bone,
                Banana,
            ),
            player = Player(
                pos = Vector2f(50f, 500f),
                tag = "Player",
            ),
            platforms = listOf(
                Platform("ground-1", Vector2f(0f, 600f), 1000f, 400f, PlatformTags.Ground, true),
                Platform("truePlatform", Vector2f(280f, 500f), 0f, 100f, PlatformTags.Obstacle, true),
                Platform("falsePlatform", Vector2f(550f, 500f), 0f, 100f, PlatformTags.Obstacle, true),
            ),

            backgroundElements = listOf(
                BackgroundElement(Vector2f(300f, 450f), 160f, 160f, PlatformTags.Unknown, true, visualStyle = VisualStyle(imageTag = "dog")),
                BackgroundElement(Vector2f(600f, 450f), 160f, 160f, PlatformTags.Unknown, true, visualStyle = VisualStyle(imageTag = "orangutan")),
            ),

            components = listOf(
                Component.Gravity(listOf("Player")),
            ),
            hints = listOf(
                Hint(Bone, ""),
                Hint(Banana, ""),
            ),
            type = "ifElse",
            inputFieldType = InputFieldType.IfElse,
            ifElsePhotos = listOf("dog", "orangutan"),
        )

    val allLevels: MutableList<Level> = mutableListOf(Level1, Level2, Level3, Level4, Level5, Level6, Level7, Level8, Level9, Level10)
    val byName: MutableMap<String, Level> = allLevels.associateBy { it.name }.toMutableMap()

    fun get(name: String): Level? = byName[name]
}
