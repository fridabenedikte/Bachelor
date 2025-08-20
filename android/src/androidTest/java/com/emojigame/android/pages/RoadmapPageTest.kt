package com.emojigame.android.pages

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.emojigame.android.R
import com.emojigame.android.base.BaseRoadmapUiTest
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RoadmapPageTest : BaseRoadmapUiTest() {

    @Test
    fun testRoadmapTitleDisplayed() {
        onView(withId(R.id.roadmapTitle))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.choose_level)))
    }

    @Test
    fun testRoadmapCanvasDisplayed() {
        onView(withId(R.id.roadmapCanvas))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testBackPressReturnsToMainMenu() {
        Espresso.pressBack()
        onView(withId(R.id.btnStartGame))
            .check(matches(isDisplayed()))
    }
}
