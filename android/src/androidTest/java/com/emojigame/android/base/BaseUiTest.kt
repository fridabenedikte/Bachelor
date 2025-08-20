package com.emojigame.android.base

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.emojigame.android.activities.MainActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule

abstract class BaseUiTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun resetStateBeforeTest() {
        resetMusicState()
    }

    @After
    fun cleanUpAfterTest() {
        try {
            Espresso.pressBackUnconditionally()
        } catch (e: Exception) {
            // Ignore if screen already gone
        }
    }

    protected fun relaunchMainActivity() {
        activityRule.scenario.close()
        ActivityScenario.launch(MainActivity::class.java)
    }

    private fun resetMusicState() {
        com.emojigame.android.util.MusicManager.isPlaying = true
    }
}
