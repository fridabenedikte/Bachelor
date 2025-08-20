package com.emojigame.core.definitions

import com.emojigame.core.entities.Emoji

/**
 * Static emoji definitions used throughout the game.
 */
interface EmojiDefinitions {
    val Question get() = Emoji(
        tag = "Question",
        category = "Unknown",
        description = "Represents an emoji that failed to load, or a placeholder indicating where an emoji should be placed by default",
    )

    val Fire get() = Emoji(
        tag = "Fire",
        category = "Action",
        description = "Represents fire or something hot.",
    )

    val Laughing get() = Emoji(
        tag = "Laughing",
        category = "Reaction",
        description = "Represents laughter or joy.",
    )

    val Idea get() = Emoji(
        tag = "Idea",
        category = "Action",
        description = "Represents a new idea or inspiration.",
    )

    val Heart get() = Emoji(
        tag = "Heart",
        category = "Emotion",
        description = "Symbolizes love, affection, or care.",
    )

    val ThumbsUp get() = Emoji(
        tag = "ThumbsUp",
        category = "Reaction",
        description = "Represents approval or agreement.",
    )

    val Star get() = Emoji(
        tag = "Star",
        category = "Symbol",
        description = "Used to indicate something special or favorite.",
    )

    val Crying get() = Emoji(
        tag = "Crying",
        category = "Emotion",
        description = "Represents sadness or tears of joy.",
    )

    val Rocket get() = Emoji(
        tag = "Rocket",
        category = "Object",
        description = "Symbolizes speed, growth, or space travel.",
    )

    val Wood get() = Emoji(
        tag = "Wood",
        category = "Object",
        description = "Symbolizes bridge over water",
    )

    val Hand get() = Emoji(
        tag = "Hand",
        category = "Action",
        description = "Represent hand movement",
    )

    val Apple get() = Emoji(
        tag = "Apple",
        category = "Object",
        description = "Represents an apple",
    )

    val Umbrella get() = Emoji(
        tag = "Umbrella",
        category = "Object",
        description = "represent rain",
    )

    val Sunglasses get() = Emoji(
        tag = "Sunglasses",
        category = "Object",
        description = "represent sun",
    )

    val Bone get() = Emoji(
        tag = "Bone",
        category = "Object",
        description = "Bone for dogs to eat",
    )

    val Banana get() = Emoji(
        tag = "Banana",
        category = "Object",
        description = "Bananas for monkey to eat",
    )

    val Walking get() = Emoji(
        tag = "Walking",
        category = "Action",
        description = "Represent person walking",
    )

    val Standing get() = Emoji(
        tag = "Standing",
        category = "Action",
        description = "represent person standing",
    )

    val Fishing get() = Emoji(
        tag = "Fishing",
        category = "Action",
        description = "Represent a person fishing"
    )

    val Rowing get() = Emoji(
        tag = "Rowing",
        category = "Action",
        description = "Representing a person rowing a boat",
    )

    val Ladder get() = Emoji(
        tag = "Ladder",
        category = "Object",
        description = "Ladder fro climbing",
    )

    /**
     * Convenience function to get all defined emojis.
     */
    val all: List<Emoji> get() =
        listOf(
            Question,
            Fire,
            Laughing,
            Idea,
            Heart,
            ThumbsUp,
            Star,
            Crying,
            Rocket,
            Wood,
            Hand,
            Apple,
            Umbrella,
            Sunglasses,
            Bone,
            Banana,
            Walking,
            Standing,
            Fishing,
            Rowing,
            Ladder,

        )

    /**
     * Convenience map for quick lookup by tag.
     */
    val byTag: Map<String, Emoji> get() = all.associateBy { it.tag }
}
