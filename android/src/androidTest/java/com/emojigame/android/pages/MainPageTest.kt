package com.emojigame.android.pages

import com.emojigame.android.base.BaseUiTest
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.emojigame.android.R

@RunWith(AndroidJUnit4::class)
class MainPageTest : BaseUiTest() {

    @Test
    fun testMainMenuButtonsDisplayed() {
        onView(withId(R.id.btnStartGame)).check(matches(isDisplayed()))
        onView(withId(R.id.btnSettings)).check(matches(isDisplayed()))
        onView(withId(R.id.btnExit)).check(matches(isDisplayed()))
        onView(withId(R.id.btnMusicToggle)).check(matches(isDisplayed()))
    }

    @Test
    fun testStartGameButtonNavigatesToLevelSelector() {
        onView(withId(R.id.btnStartGame)).perform(click())
        onView(withText("choose level")).check(matches(isDisplayed()))

        relaunchMainActivity()
    }

    @Test
    fun testSettingsOpensSettingsPopup() {
        onView(withId(R.id.btnSettings)).perform(click())
        onView(withText("Settings")).check(matches(isDisplayed()))
    }

    @Test
    fun testSoundButtonToggle() {
        onView(withId(R.id.btnMusicToggle))
            .check(matches(withContentDescription("Mute Music")))

        onView(withId(R.id.btnMusicToggle)).perform(click())
        onView(withId(R.id.btnMusicToggle))
            .check(matches(withContentDescription("Play Music")))

        onView(withId(R.id.btnMusicToggle)).perform(click())
        onView(withId(R.id.btnMusicToggle))
            .check(matches(withContentDescription("Mute Music")))
    }
}
