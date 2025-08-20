package com.emojigame.core.states

import com.emojigame.core.data.BackgroundElement
import com.emojigame.core.data.Component
import com.emojigame.core.data.Hint
import com.emojigame.core.data.Level
import com.emojigame.core.data.Platform
import com.emojigame.core.data.PlatformTags
import com.emojigame.core.data.VisualStyle
import com.emojigame.core.definitions.LevelDefinitions
import com.emojigame.core.entities.Emoji
import com.emojigame.core.entities.Player
import com.emojigame.core.util.GameIntent
import com.emojigame.core.util.Logger
import com.emojigame.core.util.Vector2f

/**
 * [PlayState] manages the active gameplay state.
 *
 * It handles loading levels, processing emoji-based interactions,
 * updating the player's position, and applying physics like gravity.
 *
 * This state responds to game intents such as selecting emojis, playing them,
 * and dynamically overloading level data from XML or runtime sources.
 */
class PlayState(gameStateManager: GameStateManager) : GameState(gameStateManager) {
    private var player: Player? = null
    private var loadedLevel: Level? = null
    private var platforms: List<Platform> = emptyList()
    private var backgroundElements: List<BackgroundElement> = emptyList()
    private var paused: Boolean = false
    private var lastPlayerPos: Vector2f? = null
    private var stuckTimer: Float = 0f
    private val stuckTimeThreshold = 2f
    private var isPlayerActive = false
    private var ifOrElseList = emptyList<Boolean>()
    private var levelType: String? = null
    private var trueHasRun: Boolean = false

    private var playButtonCounter = 0

    override fun onEnter() {
        Logger.i { "Entering PlayState" }

        val currentState = gameStateManager.gameStateFlow.value
        player = player ?: currentState.player
        loadedLevel = loadedLevel ?: currentState.level
        if (platforms.isEmpty()) platforms = currentState.platforms
        if (backgroundElements.isEmpty()) backgroundElements = currentState.backgroundElements
    }

    /** Called each frame to update the player's state and notify UI of changes. */
    override fun update() {
        if (paused) {
            player?.isPaused = true
            return
        }

        player?.isPaused = false
        when (levelType) {
            "ifElseTLight" -> handleIfElseTLightLogic()
            "ifElse" -> handleIfElseLogic()
        }

        player?.update(platforms)
        updatePlayerPosition()
        checkLevelCompletion()
    }

    /**
     * Handles conditional logic for levels of type "ifElseTLight".
     *
     * If the player is blocked and has been stuck for a short duration,
     * toggles the `ifOrElse` logic and updates platform states accordingly.
     */
    private fun handleIfElseTLightLogic() {
        if (player?.getIsBlocked() == true && stuckTimer == 0.7f) {
            ifOrElseChange(gameStateManager.gameStateFlow.value.playedEmojis)
        }
    }

    /**
     * Handles logic for levels of type "ifElse".
     *
     * Updates either the true or false platform based on the player's state and a timer.
     * This controls branching logic based on emoji input correctness.
     */
    private fun handleIfElseLogic() {
        if (player?.getIsBlocked() == true && stuckTimer >= 0.4f) {
            if (trueHasRun && stuckTimer <= 0.41f) {
                platforms = updateFalsePlatform(gameStateManager.gameStateFlow.value.playedEmojis, platforms)
            } else {
                platforms = updateTruePlatform(gameStateManager.gameStateFlow.value.playedEmojis, platforms)
                trueHasRun = true
            }
        }
    }

    private fun updatePlayerPosition() {
        player?.let {
            gameStateManager.updatePlayerPosition(it.pos)
            val currentPos = it.pos

            if (isPlayerActive) {
                stuckTimer = if (lastPlayerPos == currentPos) stuckTimer + (1f / 60f) else 0f
                if (stuckTimer >= stuckTimeThreshold) {
                    Logger.i { "Player stuck. Game Over!" }
                    gameStateManager.processIntent(GameIntent.ChangeState(GameStateType.GameOver))
                }
                lastPlayerPos = currentPos
            }
        }
    }

    /** Called when the state is exited. */
    override fun onExit() {
        Logger.i { "Exiting PlayState" }
    }

    /**
     * Handles incoming [GameIntent] actions relevant to this state.
     */
    override fun handleIntent(intent: GameIntent) {
        Logger.i { "Intent handled by PlayState: $intent" }
        when (intent) {
            is GameIntent.LoadLevel -> loadLevel(intent.levelName)
            is GameIntent.PlayEmoji -> playEmojis(intent.emojis)
            is GameIntent.SelectEmoji -> selectEmoji(intent.emoji)
            is GameIntent.DeselectEmoji -> deselectEmoji(intent.emoji)
            is GameIntent.ClearSelection -> clearSelectedEmojis()
            is GameIntent.UpdateRepeatCount -> updateRepeatCount(intent.count)
            is GameIntent.ResetLevel -> resetLevel()
            is GameIntent.OverloadLevel -> overloadLevelData(intent)
            is GameIntent.ShowHint -> onHintButtonClicked()
            is GameIntent.ClearHint -> clearHint()
            is GameIntent.PauseGame -> paused = true
            is GameIntent.ResumeGame -> {
                paused = false
                player?.let {
                    val oldTarget = it.getTarget()
                    it.setTarget(oldTarget) // 🔥 Fortell spilleren å fortsette dit
                }
            }
            is GameIntent.UpdateIfOrElse -> updateIfOrElse(intent)
            else -> Logger.i { "Intent not implemented for PlayState" }
        }
    }

    /**
     * Loads a level from [LevelDefinitions] by name.
     * Initializes player, gravity, and platforms.
     *
     * @param levelName The unique name of the level to load.
     */
    private fun loadLevel(levelName: String) {
        Logger.i { "Loading level: $levelName" }

        val level =
            LevelDefinitions.get(levelName) ?: run {
                Logger.e { "Level not found: $levelName" }
                return
            }

        loadedLevel = level
        this.player = level.player.clone()

        val gravity =
            level.components
                .filterIsInstance<Component.Gravity>()
                .firstOrNull()
                ?.gravity ?: Vector2f(0f, 0f)

        this.player?.applyGravity(gravity)

        this.platforms = level.platforms.map { it.clone() }
        this.backgroundElements = level.backgroundElements.map { it.copy() }
        this.ifOrElseList = level.ifOrElse
        this.levelType = level.type

        gameStateManager.updateState {
            copy(
                level = level,
                availableEmojis = level.emojis,
                player = this@PlayState.player,
                playedEmojis = emptyList(),
                selectedEmojis = emptyList(),
                platforms = this@PlayState.platforms,
                backgroundElements = this@PlayState.backgroundElements,
                repeatCount = 1,
                ifOrElse = this@PlayState.ifOrElseList,
                ifElsePhotos = level.ifElsePhotos
            )
        }

        gameStateManager.updatePlayerPosition(level.player.pos)

        if (level.tutorial != null) {
            gameStateManager.processIntent(GameIntent.ChangeState(GameStateType.Tutorial))
        }
    }

    /**
     * Applies emoji logic to the level and activates any matching platforms.
     *
     * @param unrepeatedEmojis list of emojis that have been "played".
     */
    private fun playEmojis(unrepeatedEmojis: List<Emoji>) {
        Logger.i { "Playing emojis: ${unrepeatedEmojis.map { it.tag }}" }

        val emojis = expandRepeatedEmojis(unrepeatedEmojis)
        isPlayerActive = true
        player?.setTarget(Vector2f(800f, 500f))

        val level = loadedLevel ?: return
        val emojisLeft = emojis.toMutableList()

        removeMatchingPlatforms(level, emojis, emojisLeft)
        activateMatchingPlatforms(level, emojisLeft)
        applyIfOrElseLogic(level, emojis)
        applyPlatformChangesFromIfOrElseEmojis(level, emojis)

        gameStateManager.updateState {
            copy(
                playedEmojis = emojis,
                platforms = this@PlayState.platforms,
            )
        }

        handleIncorrectEmojis(emojis)
    }

    /**
     * Expands the emoji list according to the repeat count set in game state.
     */
    private fun expandRepeatedEmojis(unrepeatedEmojis: List<Emoji>): List<Emoji> {
        val repeatCount = gameStateManager.gameStateFlow.value.repeatCount
        return (1..repeatCount).flatMap { unrepeatedEmojis }
    }

    /**
     * Removes platforms based on matching EmojiRemovesPlatform and EmojiSequenceRemovesPlatform components.
     */
    private fun removeMatchingPlatforms(level: Level, emojis: List<Emoji>, emojisLeft: MutableList<Emoji>) {
        val removableComponents = level.components.filterIsInstance<Component.EmojiRemovesPlatform>()
        val sequenceComponents = level.components.filterIsInstance<Component.EmojiSequenceRemovesPlatform>()
        val removablePlatforms = mutableListOf<String>()

        for (component in removableComponents) {
            val required = component.requiredEmojis.toMutableList()
            val matched = mutableListOf<Emoji>()

            for (emoji in emojis) {
                val match = required.find { it.tag == emoji.tag }
                if (match != null) {
                    matched.add(emoji)
                    required.remove(match)
                }
            }

            if (required.isEmpty()) {
                Logger.i { "✅ Removing platform ${component.platformId}" }
                removablePlatforms.add(component.platformId)
                matched.forEach { emojisLeft.remove(it) }
            }
        }

        val usedEmojiIndexes = mutableSetOf<Int>()
        for (component in sequenceComponents) {
            val sequence = component.emojiSequence
            val sequenceLength = sequence.size
            for (start in 0..(emojis.size - sequenceLength)) {
                val candidateRange = start until (start + sequenceLength)
                if (candidateRange.any { it in usedEmojiIndexes }) continue

                val candidate = emojis.slice(candidateRange)
                if (sequence.zip(candidate).all { (a, b) -> a.tag == b.tag }) {
                    Logger.i { "[SEQUENCE] Removing platform ${component.platformId}" }
                    removablePlatforms.add(component.platformId)
                    usedEmojiIndexes.addAll(candidateRange)
                    break
                }
            }
        }

        platforms = platforms.map { platform ->
            if (removablePlatforms.contains(platform.id)) platform.copy(isEnabled = false) else platform
        }
    }

    /**
     * Activates platforms based on matching EmojiActivatesPlatform components.
     */
    private fun activateMatchingPlatforms(level: Level, emojisLeft: MutableList<Emoji>) {
        val activatableEmojis = emojisLeft.toMutableList()
        val activatedPlatformIds =
            level.components
                .filterIsInstance<Component.EmojiActivatesPlatform>()
                .filter { component ->
                    val match = activatableEmojis.find { it.tag == component.emoji.tag }
                    if (match != null) {
                        activatableEmojis.remove(match)
                        true
                    } else false
                }
                .map { it.platformId }
                .toSet()

        platforms = platforms.map { platform ->
            if (activatedPlatformIds.contains(platform.id)) platform.copy(isEnabled = true) else platform
        }
    }

    /**
     * Applies platform activation/deactivation based on if/else components.
     */
    private fun applyIfOrElseLogic(level: Level, emojis: List<Emoji>) {
        val ifOrElseComponents = level.components.filterIsInstance<Component.IfOrElseActivatesPlatform>()
        this.ifOrElseList = gameStateManager.gameStateFlow.value.ifOrElse.toMutableList()

        val ifOrElseActivatedPlatformIds = ifOrElseComponents
            .filter { it.index in ifOrElseList.indices }
            .associate { it.platformId to ifOrElseList[it.index] }

        platforms = platforms.map { platform ->
            if (ifOrElseActivatedPlatformIds.containsKey(platform.id)) {
                platform.copy(isEnabled = ifOrElseActivatedPlatformIds[platform.id] == true)
            } else {
                platform
            }
        }
    }

    /**
     * Applies platform state changes based on IfOrElseEmojiSetsPlatform components.
     */
    private fun applyPlatformChangesFromIfOrElseEmojis(level: Level, emojis: List<Emoji>) {
        val platformsStateChanges = calculatePlatformStateChangesFromIfOrElseEmojis(
            components = level.components,
            emojis = emojis,
            ifOrElseList = ifOrElseList
        )

        platforms = platforms.map { platform ->
            platformsStateChanges[platform.id]?.let { shouldEnable ->
                platform.copy(isEnabled = shouldEnable)
            } ?: platform
        }
    }

    /**
     * Tracks incorrect emoji attempts and provides a hint after five failed tries.
     */
    private fun handleIncorrectEmojis(emojis: List<Emoji>) {
        if (isPlayedEmojisCorrect(emojis)) {
            playButtonCounter = 0
            return
        }

        playButtonCounter++
        Logger.i { "Incorrect combo. Attempt #$playButtonCounter" }

        if (playButtonCounter >= 5) {
            val hint = provideHint()
            Logger.i { "💡 Hint: $hint" }
            gameStateManager.updateState {
                copy(hint = hint)
            }
            playButtonCounter = 0
        }
    }

    /**
     * Inverts the current `ifOrElse` state and updates platforms accordingly.
     *
     * This function is used in certain conditional levels to simulate toggle behavior,
     * allowing players to switch the logical flow of a level based on input.
     *
     * @param unrepeatedEmojis List of emojis currently in play.
     */
    private fun ifOrElseChange(unrepeatedEmojis: List<Emoji>) {
        val repeatCount = gameStateManager.gameStateFlow.value.repeatCount
        val emojis: List<Emoji> = (1..repeatCount).flatMap { unrepeatedEmojis }
        val level = loadedLevel ?: return
        val ifOrElseComponents = level.components.filterIsInstance<Component.IfOrElseActivatesPlatform>()
        this.ifOrElseList = invertBooleans(this.ifOrElseList)

        gameStateManager.updateState {
            copy(ifOrElse = this@PlayState.ifOrElseList)
        }

        val ifOrElseActivatedPlatformIds = ifOrElseComponents
            .filter { it.index in ifOrElseList.indices }
            .associate { it.platformId to ifOrElseList[it.index] }

        platforms = platforms.map { platform ->
            if (ifOrElseActivatedPlatformIds.containsKey(platform.id)) {
                platform.copy(isEnabled = ifOrElseActivatedPlatformIds[platform.id] == true)
            } else {
                platform
            }
        }

        val platformsStateChanges = calculatePlatformStateChangesFromIfOrElseEmojis(
            components = level.components,
            emojis = emojis,
            ifOrElseList = gameStateManager.gameStateFlow.value.ifOrElse
        )
        platforms = platforms.map { platform ->
            platformsStateChanges[platform.id]?.let { shouldEnable ->
                platform.copy(isEnabled = shouldEnable)
            } ?: platform
        }

        gameStateManager.updateState {
            copy(
                playedEmojis = emojis,
                platforms = this@PlayState.platforms,
            )
        }
    }

    /**
     * check if the selected emojies are correct in relation to the hint list
     */
    // TODO change this to use a levelcomplete state, when this is made
    private fun isPlayedEmojisCorrect(playedEmojis: List<Emoji>): Boolean {
        Logger.i { playedEmojis.toString() }
        val gameState = gameStateManager.gameStateFlow.value
        val hintList: List<Hint> = gameState.level?.hints ?: return false

        if (playedEmojis.size != hintList.size) return false

        for (i in playedEmojis.indices) {
            if (playedEmojis[i].tag != hintList[i].emoji?.tag) {
                return false
            }
        }

        return true
    }

    /**
     * Updated repeat count for levels with loop
     */
    private fun updateRepeatCount(count: Int) {
        gameStateManager.updateState {
            copy(repeatCount = count)
        }
    }

    /**
     * Adds an emoji to the selected list in the UI state.
     */
    private fun selectEmoji(emoji: Emoji) {
        val emojiWithId = emoji.assignEmojiId()
        gameStateManager.updateState {
            copy(selectedEmojis = selectedEmojis + emojiWithId)
        }
    }

    /**
     * Removes an emoji from the selected list.
     */
    private fun deselectEmoji(emoji: Emoji) {
        gameStateManager.updateState {
            copy(selectedEmojis = selectedEmojis.filterNot { it.id == emoji.id })
        }
    }

    /**
     * Clears all selected emojis.
     */
    private fun clearSelectedEmojis() {
        gameStateManager.updateState {
            copy(selectedEmojis = emptyList())
        }
    }

    /**
     * Resets the level to its original state, clearing all UI and entity data.
     */
    fun resetLevel() {
        val level = loadedLevel ?: return
        Logger.i { "🔄 Resetting level: ${level.name}" }

        player = null

        gameStateManager.updateState {
            copy(
                level = null,
                entities = emptyList(),
                availableEmojis = emptyList(),
                playedEmojis = emptyList(),
                selectedEmojis = emptyList(),
                player = null,
                repeatCount = 1,
                hint = null,
                ifOrElse = level.ifOrElse
            )
        }

        loadLevel(level.name)
    }

    private fun checkLevelCompletion() {

        val playerPos = player?.pos ?: return

        if (playerPos.y > 1000f) {
            Logger.i { "💀 Player fell out of screen. Game Over!" }
            gameStateManager.processIntent(GameIntent.ChangeState(GameStateType.GameOver))
            return
        }

        val reachedGoal = playerPos.x >= 800f
        if (!reachedGoal) return

        val level = loadedLevel ?: return
        val requiredEmojis = level.hints

        if (requiredEmojis.isEmpty()) {
            Logger.i { "Player reached goal — no emojis required. Level completed!" }
            gameStateManager.processIntent(GameIntent.ChangeState(GameStateType.GameWon))
            return
        }

        // If correct emojis are used, change state to GameWon
        val playedEmojis = gameStateManager.gameStateFlow.value.playedEmojis
        if (isPlayedEmojisCorrect(playedEmojis)) {
            Logger.i { "Correct emoji combo used. Level completed!" }
            gameStateManager.processIntent(GameIntent.ChangeState(GameStateType.GameWon))
        } else {
            Logger.i { "❌ Incorrect emoji combo at goal. Game Over!" }
            gameStateManager.processIntent(GameIntent.ChangeState(GameStateType.GameOver))
        }
    }

    /**
     * Overwrites the level data in [LevelDefinitions] with runtime-provided layout data.
     *
     * This enables levels to be visually authored in XML and reflected in the core model.
     *
     * @param intent The overload request containing updated platforms and backgrounds.
     */
    private fun overloadLevelData(intent: GameIntent.OverloadLevel) {
        val name = intent.levelName
        val originalLevel = LevelDefinitions.byName[name]

        if (originalLevel != null) {
            val overloaded =
                originalLevel.copy(
                    platforms = intent.platforms.map { it.copy() },
                    backgroundElements = intent.backgroundElements.map { it.copy() },
                )

            LevelDefinitions.byName[name] = overloaded

            val index = LevelDefinitions.allLevels.indexOfFirst { it.name == name }
            if (index != -1) {
                LevelDefinitions.allLevels[index] = overloaded
            }
            Logger.i { "✅ Level '$name' successfully overloaded." }
            loadLevel(name)
        } else {
            Logger.e { "❌ Tried to overload level '$name', but it wasn't found in LevelDefinitions." }
        }
    }

    /**
     * Provides a hint based on the current state and the selected emoji sequence.
     *
     * Compares the selected emojis to the expected hint list and returns the first mismatch,
     * or the next expected emoji.
     *
     * @return A [Hint] object containing either an emoji or a message.
     */
    private fun provideHint(): Hint {
        val gameState = gameStateManager.gameStateFlow.value
        val selectedEmojis = gameState.selectedEmojis

        val hintList: List<Hint> = gameState.level?.hints ?: return Hint(null, "No hints available")

        for (i in selectedEmojis.indices) {
            if (i >= hintList.size) return Hint(null, "No more hints available")

            if (selectedEmojis[i].tag != hintList[i].emoji?.tag) {
                return hintList[i]
            }
        }

        return if (selectedEmojis.size < hintList.size) {
            Hint(hintList[selectedEmojis.size].emoji, null)
        } else {
            Hint(null, "No more hints available")
        }
    }

    /**
     * Called when the hint button is pressed in the UI.
     *
     * Retrieves and logs a hint, and updates the game state with the new hint value.
     */
    private fun onHintButtonClicked() {
        val hint = provideHint()
        Logger.i { "💡 Hint: $hint" }

        gameStateManager.updateState {
            copy(hint = hint)
        }
    }

    /**
     * Clears the current hint from the game state.
     *
     * Called when the user dismisses the hint or performs a new action.
     */
    private fun clearHint() {
        gameStateManager.updateState {
            copy(hint = null)
        }
    }

    /**
     * Inverts all boolean values in a list.
     *
     * Each `true` becomes `false`, and each `false` becomes `true`.
     *
     * @param list The list of booleans to invert.
     * @return A new list with all values inverted.
     */
    private fun invertBooleans(list: List<Boolean>): List<Boolean> {
        return list.map { !it }
    }

    /**
     * Updates the `ifOrElse` state inside the GameState based on a provided intent.
     *
     * This is used to modify conditional logic dynamically during gameplay.
     *
     * @param intent Contains the new `ifOrElse` list to update the game state with.
     */
    private fun updateIfOrElse(intent: GameIntent.UpdateIfOrElse) {
        gameStateManager.updateState {
            copy(ifOrElse = intent.newIfOrElse)
        }
    }

    /**
     * Calculates platform state changes based on if/else conditions and active emojis.
     *
     * Checks which emojis are active depending on the corresponding `ifOrElse` condition,
     * and determines which platforms should be enabled or disabled.
     *
     * @param components List of all components in the level.
     * @param emojis List of active emojis currently in play.
     * @param ifOrElseList Current evaluation of if/else conditions (true/false per condition).
     * @return A map of platform IDs to a boolean indicating if the platform should be enabled.
     */
    private fun calculatePlatformStateChangesFromIfOrElseEmojis(
        components: List<Component>,
        emojis: List<Emoji>,
        ifOrElseList: List<Boolean>
    ): Map<String, Boolean> {
        val ifOrElseEmojiComponents = components.filterIsInstance<Component.IfOrElseEmojiSetsPlatform>()
        val platformsStateChanges = mutableMapOf<String, Boolean>()

        for (component in ifOrElseEmojiComponents) {
            val ifOrElseValue = ifOrElseList.getOrNull(component.ifOrElseIndex) ?: false
            val emojisInPlay = emojis

            val matches = if (ifOrElseValue) {
                component.ifTrueEmojiIndex < emojisInPlay.size &&
                    emojisInPlay[component.ifTrueEmojiIndex].tag == component.ifTrueEmojiTag
            } else {
                component.ifFalseEmojiIndex < emojisInPlay.size &&
                    emojisInPlay[component.ifFalseEmojiIndex].tag == component.ifFalseEmojiTag
            }

            if (matches) {
                platformsStateChanges[component.platformId] = component.shouldEnable
            }
        }

        return platformsStateChanges
    }

    private fun updateTruePlatform(input: List<Emoji>, platforms: List<Platform>): List<Platform> {
        val levelEmojis = gameStateManager.gameStateFlow.value.availableEmojis
        if (input.isEmpty() || levelEmojis.isEmpty()) return platforms

        val updatedPlatforms = platforms.map { platform ->
            if (platform.id == "truePlatform") {
                val inputEmoji = input[0]
                val expectedEmoji = levelEmojis[0]

                if (inputEmoji.tag == expectedEmoji.tag) {
                    platform.copy(
                        width = 100f,
                        tag = PlatformTags.Unknown,
                        visualStyle = VisualStyle(emoji = expectedEmoji),
                        isEnabled = true
                    )
                } else {
                    platform.copy(
                        width = 100f,
                        tag = PlatformTags.Obstacle,
                        visualStyle = VisualStyle(emoji = inputEmoji),
                        isEnabled = true
                    )
                }
            } else {
                platform
            }
        }

        gameStateManager.updateState {
            copy(platforms = updatedPlatforms)
        }

        return updatedPlatforms
    }

    private fun updateFalsePlatform(input: List<Emoji>, platforms: List<Platform>): List<Platform> {
        val levelEmojis = gameStateManager.gameStateFlow.value.availableEmojis
        if (input.size < 2 || levelEmojis.size < 2) return platforms

        val updatedPlatforms = platforms.map { platform ->
            if (platform.id == "falsePlatform") {
                val inputEmoji = input[1]
                val expectedEmoji = levelEmojis[1]

                if (inputEmoji.tag == expectedEmoji.tag) {
                    platform.copy(
                        width = 100f,
                        tag = PlatformTags.Unknown,
                        visualStyle = VisualStyle(emoji = expectedEmoji),
                        isEnabled = true
                    )
                } else {
                    platform.copy(
                        width = 100f,
                        tag = PlatformTags.Obstacle,
                        visualStyle = VisualStyle(emoji = inputEmoji),
                        isEnabled = true
                    )
                }
            } else {
                platform
            }
        }

        gameStateManager.updateState {
            copy(platforms = updatedPlatforms)
        }

        return updatedPlatforms
    }
}
