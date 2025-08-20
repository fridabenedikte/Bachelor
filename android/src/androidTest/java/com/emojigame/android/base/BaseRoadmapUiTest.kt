package com.emojigame.android.base

import androidx.test.espresso.Espresso
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.emojigame.android.activities.RoadmapActivity
import org.junit.After
import org.junit.Rule

abstract class BaseRoadmapUiTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(RoadmapActivity::class.java)

/*    @Before
    fun setupBeforeEachTest() {
    }*/

    @After
    fun cleanupAfterEachTest() {
        try {
            Espresso.pressBackUnconditionally()
        } catch (e: Exception) {
            // Ignore if already closed
        }
    }
}
