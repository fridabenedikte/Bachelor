package com.emojigame.android.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages global music playback state and user intent.
 *
 * This object exposes a [StateFlow] for reactive UI observation,
 * as well as a flag to track whether the user explicitly disabled music.
 */
object MusicManager {

    private val _isPlayingFlow = MutableStateFlow(true)
    val isPlayingFlow: StateFlow<Boolean> = _isPlayingFlow.asStateFlow()

    /**
     * Indicates whether music is currently playing.
     * Setting this also updates [userHasDisabledMusic].
     */
    var isPlaying: Boolean
        get() = _isPlayingFlow.value
        set(value) {
            _isPlayingFlow.value = value
            userHasDisabledMusic = !value
        }

    /**
     * Whether the user has explicitly disabled music playback.
     * This is set automatically when [isPlaying] is modified.
     */
    var userHasDisabledMusic: Boolean = false
}
