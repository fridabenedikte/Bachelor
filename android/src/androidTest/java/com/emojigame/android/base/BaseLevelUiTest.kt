package com.emojigame.android.base

import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentContainerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.emojigame.android.R
import com.emojigame.android.activities.LoadingActivity
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.After
import org.junit.Before

abstract class BaseLevelUiTest {

    abstract val targetLevel: Int
    private lateinit var scenario: ActivityScenario<*>

    @Before
    fun setupFullAppFlow() {
        unlockTargetLevel(targetLevel)

        scenario = ActivityScenario.launch<LoadingActivity>(Intent(Intent.ACTION_MAIN).apply {
            setClassName("com.emojigame.android", "com.emojigame.android.activities.LoadingActivity")
        })

        onView(withId(R.id.mainMenuPage))
            .check(matches(isDisplayed()))

        onView(withId(R.id.btnStartGame))
            .perform(click())

        onView(withId(R.id.roadmapCanvas))
            .check(matches(isDisplayed()))

        clickOnRoadmapNode(targetLevel)

        onView(allOf(
            withId(R.id.puzzle_fragment_container),
            instanceOf(FragmentContainerView::class.java)
        )).check(matches(isDisplayed()))

    }

    @After
    fun cleanUp() {
        try {
            Espresso.pressBackUnconditionally()
        } catch (_: Exception) {
        }

        resetProgress()
    }

    private fun clickOnRoadmapNode(nodeIndex: Int) {
        onView(withId(R.id.roadmapCanvas)).perform(object : androidx.test.espresso.ViewAction {
            override fun getDescription() = "Click on roadmap node $nodeIndex"

            override fun getConstraints() = isAssignableFrom(com.emojigame.android.ui.fragments.RoadmapCanvasView::class.java)

            override fun perform(uiController: androidx.test.espresso.UiController, view: android.view.View) {
                val roadmapCanvasView = view as com.emojigame.android.ui.fragments.RoadmapCanvasView
                roadmapCanvasView.simulateNodeClick(nodeIndex)
            }
        })
    }

    private fun unlockTargetLevel(level: Int) {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = context.getSharedPreferences("level_progress", Context.MODE_PRIVATE)
        prefs.edit().putInt("unlocked_level", level).apply()
    }

    private fun resetProgress() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = context.getSharedPreferences("level_progress", Context.MODE_PRIVATE)
        prefs.edit().putInt("unlocked_level", 1).apply()
    }

}
