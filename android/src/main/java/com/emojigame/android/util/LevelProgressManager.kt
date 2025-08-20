package com.emojigame.android.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * Singleton object that manages level progression by storing
 * and retrieving the highest unlocked level.
 */
object LevelProgressManager {
    private const val PREF_NAME = "level_progress"
    private const val KEY_UNLOCKED_LEVEL = "unlocked_level"

    private lateinit var prefs: SharedPreferences

    /**
     * Initializes the [SharedPreferences] used for storing level data.
     *
     * @param context Application context used to get shared preferences.
     */
    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Retrieves the highest unlocked level.
     *
     * @return The unlocked level number (starts at 1).
     */
    fun getUnlockedLevel(): Int {
        return prefs.getInt(KEY_UNLOCKED_LEVEL, 1)
    }

    /**
     * Unlocks the next level if the [currentLevel] is greater
     * than or equal to the last unlocked level.
     *
     * @param currentLevel The most recently completed level.
     */
    fun unlockNextLevel(currentLevel: Int) {
        val maxLevel = getUnlockedLevel()
        if (currentLevel >= maxLevel) {
            prefs.edit {
                putInt(KEY_UNLOCKED_LEVEL, currentLevel + 1)
            }
        }
    }

    /**
     * Resets progress to level 1.
     */
    fun resetProgress() {
        prefs.edit {
            putInt(KEY_UNLOCKED_LEVEL, 1)
        }
    }
}
