package com.emojigame.android.levels

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.emojigame.android.R
import com.emojigame.android.base.BaseLevelUiTest
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Level3GameplayTest : BaseLevelUiTest() {

    override val targetLevel = 3

    @Test
    fun testAllEmojisClickable() {
        clickEmojiInLibrary("Wood")
        clickEmojiInLibrary("Fishing")
        clickEmojiInLibrary("Walking")
    }

    @Test
    fun testTwoWoodLogsWin() {
        clickEmojiInLibrary("Wood")
        clickEmojiInLibrary("Wood")
        onView(allOf(withId(R.id.play_button_inclusion), withEffectiveVisibility(Visibility.VISIBLE))).perform(click())
        Thread.sleep(2000)
        onView(withText(R.string.you_won_emoji)).check(matches(isDisplayed()))
    }

    @Test
    fun testOtherEmojiBeforeLogsResultsInLoss() {
        clickEmojiInLibrary("Walking")
        clickEmojiInLibrary("Wood")
        clickEmojiInLibrary("Wood")
        onView(allOf(withId(R.id.play_button_inclusion), withEffectiveVisibility(Visibility.VISIBLE))).perform(click())
        Thread.sleep(2000)
        onView(withText(R.string.you_lost_try_again)).check(matches(isDisplayed()))
    }

    @Test
    fun testLogsWithExtraAfterFails() {
        clickEmojiInLibrary("Wood")
        clickEmojiInLibrary("Wood")
        clickEmojiInLibrary("Walking")
        onView(allOf(withId(R.id.play_button_inclusion), withEffectiveVisibility(Visibility.VISIBLE))).perform(click())
        Thread.sleep(2000)
        onView(withText(R.string.you_lost_try_again)).check(matches(isDisplayed()))
    }

    // -------------------
    // Helper functions
    // -------------------

    private fun clickEmojiInLibrary(tag: String) {
        onView(
            withIndex(
                allOf(
                    withTagValue(`is`(tag)),
                    withParent(withId(R.id.library_bar)),
                    withEffectiveVisibility(Visibility.VISIBLE)
                ),0
            )
        ).perform(click())
    }

    private fun withIndex(matcher: Matcher<View>, index: Int): Matcher<View> {
        var currentIndex = 0
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("with index $index: ")
                matcher.describeTo(description)
            }

            override fun matchesSafely(view: View): Boolean {
                if (matcher.matches(view) && view.visibility == View.VISIBLE) {
                    if (currentIndex == index) {
                        return true
                    }
                    currentIndex++
                }
                return false
            }
        }
    }

}
