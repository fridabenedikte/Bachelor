package com.emojigame.core.states

import com.emojigame.core.data.BackgroundElement
import com.emojigame.core.data.Component
import com.emojigame.core.data.Platform
import com.emojigame.core.data.tutorialSteps.TapAvailableEmojiStep
import com.emojigame.core.data.tutorialSteps.TapButtonStep
import com.emojigame.core.data.tutorialSteps.TapSelectedEmojiStep
import com.emojigame.core.data.tutorialSteps.TutorialStep
import com.emojigame.core.entities.Emoji
import com.emojigame.core.util.GameIntent
import com.emojigame.core.util.Logger
import com.emojigame.core.util.Vector2f

class TutorialState(gameStateManager: GameStateManager) : GameState(gameStateManager) {

    private var tutorialCompletionTime: Long? = null
    private var platforms: List<Platform> = emptyList()
    private var backgroundElements: List<BackgroundElement> = emptyList()

    override fun onEnter() {
        Logger.i { "Entering Tutorial state..." }
        loadTutorial()
    }

    override fun update() {
        val player = gameStateManager.tutorialFlow.value.player ?: return
        player.update(gameStateManager.tutorialFlow.value.platforms)
        gameStateManager.updatePlayerPosition(player.pos)
        checkTutorialCompletion()
    }

    /**
     * Checks whether the tutorial is complete.
     *
     * If the player reaches the goal, a short delay is triggered before transitioning
     * to the `PlayState`. This function manages that timing and transition.
     */
    private fun checkTutorialCompletion() {
        if (tutorialCompletionTime != null) {
            if (System.currentTimeMillis() - tutorialCompletionTime!! >= 1500L) {
                Logger.i { "🎉 Tutorial delay complete. Switching to PlayState." }
                gameStateManager.processIntent(GameIntent.ChangeState(GameStateType.Play))
                tutorialCompletionTime = null
            }
            return
        }

        gameStateManager.tutorialFlow.value.player?.takeIf { it.pos.x >= 800f }?.let {
            Logger.i { "🎉 Player reached goal in tutorial. Starting 1.5s delay..." }
            tutorialCompletionTime = System.currentTimeMillis()
        }
    }

    override fun handleIntent(intent: GameIntent) {
        Logger.i { "Tutorial handling intent: $intent" }

        val state = gameStateManager.tutorialFlow.value
        val currentIndex = state.currentStep
        val steps = state.steps

        if (currentIndex in steps.indices) {
            val currentStep = steps[currentIndex]
            Logger.i { "Current step goal: $currentStep" }

            if (currentStep.isCompletedBy(intent)) {
                Logger.i { "✅ Step $currentIndex completed" }
                gameStateManager.updateTutorial { copy(currentStep = currentIndex + 1) }
                handleFinalPlayEmojiStepIfComplete(intent)
            } else {
                Logger.i { "Step $currentIndex NOT completed, inserting correction step." }
                getCorrectionStepFor(intent)?.let { insertCorrectionStep(it) }
            }
        }

        when (intent) {
            is GameIntent.SelectEmoji -> selectEmoji(intent.emoji)
            is GameIntent.DeselectEmoji -> deselectEmoji(intent.emoji)
            is GameIntent.ClearSelection -> clearSelectedEmojis()
            is GameIntent.ResetLevel -> resetTutorial()
            else -> Logger.i { "Intent not implemented for TutorialState" }
        }
    }

    /**
     * Checks if the final step in the tutorial has been completed by the given [intent].
     *
     * If the final step is a [TapButtonStep] that matches the intent, the tutorial triggers
     * the main emoji logic to simulate level behavior (e.g. platform changes, player movement).
     *
     * @param intent The user action that may complete the final step.
     */
    private fun handleFinalPlayEmojiStepIfComplete(intent: GameIntent) {
        val tutorial = gameStateManager.tutorialFlow.value.tutorial ?: return
        val steps = tutorial.steps
        val currentIndex = gameStateManager.tutorialFlow.value.currentStep - 1

        if (currentIndex == steps.lastIndex) {
            val currentStep = steps[currentIndex]
            if (currentStep is TapButtonStep && currentStep.isCompletedBy(intent)) {
                runPlayEmojiLogic()
            }
        }
    }

    /**
     * Executes the main logic when emojis are played during the tutorial.
     *
     * This includes checking which platforms should be removed or activated
     * based on the selected emojis and level components, updating the platform list,
     * and moving the player toward the goal if conditions are met.
     */
    private fun runPlayEmojiLogic() {
        val level = gameStateManager.gameStateFlow.value.level ?: return
        val selectedEmojis = gameStateManager.tutorialFlow.value.selectedEmojis

        val emojisLeft = selectedEmojis.toMutableList()
        val removablePlatforms = collectRemovablePlatforms(level, selectedEmojis, emojisLeft)
        val activatedPlatformIds = collectActivatedPlatforms(level, emojisLeft)

        updatePlatformStates(removablePlatforms, activatedPlatformIds)

        gameStateManager.updateTutorial {
            copy(
                playedEmojis = selectedEmojis,
                platforms = this@TutorialState.platforms,
            )
        }

        gameStateManager.tutorialFlow.value.player?.setTarget(Vector2f(800f, 500f))
    }

    /**
     * Collects platform IDs that should be removed based on emoji matches.
     */
    private fun collectRemovablePlatforms(
        level: com.emojigame.core.data.Level,
        selectedEmojis: List<Emoji>,
        emojisLeft: MutableList<Emoji>
    ): List<String> {
        val removablePlatforms = mutableListOf<String>()
        val usedEmojiIndexes = mutableSetOf<Int>()

        val removableComponents = level.components.filterIsInstance<Component.EmojiRemovesPlatform>()
        val sequenceComponents = level.components.filterIsInstance<Component.EmojiSequenceRemovesPlatform>()

        for (component in removableComponents) {
            val required = component.requiredEmojis.toMutableList()
            val matched = mutableListOf<Emoji>()

            for (emoji in emojisLeft) {
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

        for (component in sequenceComponents) {
            val sequence = component.emojiSequence
            val sequenceLength = sequence.size

            for (start in 0..(selectedEmojis.size - sequenceLength)) {
                val candidateRange = start until (start + sequenceLength)
                if (candidateRange.any { it in usedEmojiIndexes }) continue

                val candidate = selectedEmojis.slice(candidateRange)
                val tagsMatch = candidate.map { it.tag } == sequence.map { it.tag }

                if (tagsMatch) {
                    Logger.i { "[SEQUENCE] Removing platform ${component.platformId}" }
                    removablePlatforms.add(component.platformId)
                    usedEmojiIndexes.addAll(candidateRange)
                    break
                }
            }
        }

        return removablePlatforms
    }

    /**
     * Collects platform IDs to be activated based on remaining emojis.
     */
    private fun collectActivatedPlatforms(
        level: com.emojigame.core.data.Level,
        emojisLeft: MutableList<Emoji>
    ): Set<String> {
        val activatableEmojis = emojisLeft.toMutableList()

        return level.components
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
    }

    /**
     * Applies platform removals and activations to the current platform list.
     */
    private fun updatePlatformStates(
        removablePlatforms: List<String>,
        activatedPlatformIds: Set<String>
    ) {
        platforms = platforms.map { platform ->
            when {
                removablePlatforms.contains(platform.id) -> platform.copy(isEnabled = false)
                activatedPlatformIds.contains(platform.id) -> platform.copy(isEnabled = true)
                else -> platform
            }
        }
    }

    /**
     * Loads and initializes the tutorial state from the currently loaded level.
     *
     * This method extracts the tutorial data (player, platforms, background, steps, and emojis)
     * from the level stored in the main game state, clones all relevant data, and updates
     * the tutorial state accordingly.
     *
     * If the level or its tutorial data is missing, logs an error and exits early.
     */
    private fun loadTutorial() {
        val level = gameStateManager.gameStateFlow.value.level
        if (level == null) {
            Logger.e { "Could not find level for tutorial" }
            return
        }

        if (level.tutorial == null) {
            Logger.e { "Could not find tutorial from level" }
            return
        }

        val tutorial = level.tutorial!!.copy()
        this.platforms = tutorial.platforms.map { it.clone() }
        this.backgroundElements = tutorial.backgroundElements.map { it.clone() }

        gameStateManager.updateTutorial {
            copy(
                tutorial = tutorial,
                player = tutorial.player.clone(),
                steps = tutorial.steps,
                currentStep = 0,
                platforms = this@TutorialState.platforms,
                backgroundElements = tutorial.backgroundElements,
                availableEmojis = tutorial.emojis,
                selectedEmojis = emptyList(),
                playedEmojis = emptyList(),
            )
        }
    }

    /**
     * Generates a corrective tutorial step based on an incorrect user intent.
     * Used to help guide the player back on track.
     *
     * @param intent The incorrect intent issued by the player.
     * @return A corresponding correction step, or null if none is applicable.
     */
    private fun getCorrectionStepFor(intent: GameIntent): TutorialStep? = when (intent) {
        is GameIntent.SelectEmoji -> TapSelectedEmojiStep(GameIntent.DeselectEmoji(intent.emoji), intent.emoji)
        is GameIntent.DeselectEmoji -> TapAvailableEmojiStep(GameIntent.SelectEmoji(intent.emoji), intent.emoji)
        is GameIntent.OpenRepeatCountDialog -> TapButtonStep(
            GameIntent.CancelRepeatCountDialog,
            buttonId = "number_picker_cancel",
        )
        is GameIntent.UpdateRepeatCount -> TapButtonStep(
            GameIntent.OpenRepeatCountDialog,
            buttonId = "emoji_wheel_center",
        )
        else -> null
    }

    /**
     * Clears the list of currently selected emojis in the tutorial state.
     */
    private fun clearSelectedEmojis() = gameStateManager.updateTutorial { copy(selectedEmojis = emptyList()) }

    /**
     * Removes the given emoji from the selected emojis list in the tutorial state.
     *
     * @param emoji The emoji to deselect.
     */
    private fun deselectEmoji(emoji: Emoji) = gameStateManager.updateTutorial {
        copy(selectedEmojis = selectedEmojis.filterNot { it.id == emoji.id })
    }

    /**
     * Adds the given emoji to the selected emojis list in the tutorial state.
     * An ID is assigned to the emoji for tracking.
     *
     * @param emoji The emoji to select.
     */
    private fun selectEmoji(emoji: Emoji) {
        val emojiWithId = emoji.assignEmojiId()
        gameStateManager.updateTutorial {
            copy(selectedEmojis = selectedEmojis + emojiWithId)
        }
    }

    /**
     * Resets the tutorial to its initial state.
     *
     * This method restores the original tutorial platforms, background elements, player,
     * and step sequence as defined in the current level's tutorial. It also clears any
     * previously selected or played emojis and resets the step index to the beginning.
     *
     * If no tutorial or level is loaded, the function exits early.
     */
    private fun resetTutorial() {
        Logger.i { "🔄 Resetting tutorial state" }

        tutorialCompletionTime = null

        val level = gameStateManager.gameStateFlow.value.level ?: return
        val tutorial = level.tutorial?.copy() ?: return

        this.platforms = tutorial.platforms.map { it.clone() }
        this.backgroundElements = tutorial.backgroundElements.map { it.clone() }

        gameStateManager.updateTutorial {
            copy(
                tutorial = tutorial,
                player = tutorial.player.clone(),
                steps = tutorial.steps,
                currentStep = 0,
                platforms = this@TutorialState.platforms,
                backgroundElements = this@TutorialState.backgroundElements,
                availableEmojis = tutorial.emojis,
                selectedEmojis = emptyList(),
                playedEmojis = emptyList(),
            )
        }
    }

    /**
     * Inserts a correction step into the tutorial steps at the current step index.
     * Used when the player performs an incorrect action.
     *
     * @param step The correction step to insert.
     */
    private fun insertCorrectionStep(step: TutorialStep) {
        val gameState = gameStateManager.tutorialFlow.value
        val currentSteps = gameState.steps.toMutableList()
        currentSteps.add(gameState.currentStep, step)
        gameStateManager.updateTutorial { copy(steps = currentSteps) }
    }

    override fun onExit() {
        gameStateManager.processIntent(GameIntent.ClearTutorial)
        gameStateManager.gameStateFlow.value.player?.let { gameStateManager.updatePlayerPosition(it.pos) }
        Logger.i { "Exiting Tutorial state..." }
    }
}
