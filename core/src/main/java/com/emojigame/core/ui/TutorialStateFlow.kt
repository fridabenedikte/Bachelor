package com.emojigame.core.ui

import com.emojigame.core.data.BackgroundElement
import com.emojigame.core.data.Platform
import com.emojigame.core.data.Tutorial
import com.emojigame.core.data.tutorialSteps.TutorialStep
import com.emojigame.core.entities.Emoji
import com.emojigame.core.entities.Player

/**
 * Centralized tutorial state representation for the UI.
 */
data class TutorialStateFlow(
    val tutorial: Tutorial? = null,
    val steps: List<TutorialStep> = emptyList(),
    val currentStep: Int = 0,
    override val player: Player? = null,
    override val availableEmojis: List<Emoji> = emptyList(),
    override val playedEmojis: List<Emoji> = emptyList(),
    override val selectedEmojis: List<Emoji> = emptyList(),
    override val platforms: List<Platform> = emptyList(),
    override val backgroundElements: List<BackgroundElement> = emptyList(),
) : BaseStateFlow
