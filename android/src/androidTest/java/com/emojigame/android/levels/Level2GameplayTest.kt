package com.emojigame.android.levels

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentContainerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.emojigame.android.R
import com.emojigame.android.base.BaseLevelUiTest
import org.hamcrest.CoreMatchers
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.`is`
import org.hamcrest.TypeSafeMatcher
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Level2GameplayTest : BaseLevelUiTest() {

    override val targetLevel = 2

    @Test
    fun testCompleteTutorialByPressingPlay() {
        onView(withIndex(
            allOf(
                withTagValue(`is`("Wood")),
                withEffectiveVisibility(Visibility.VISIBLE)
            ), 0
        )).perform(click())

        onView(withIndex(
            allOf(
                withId(R.id.play_button_inclusion),
                withEffectiveVisibility(Visibility.VISIBLE)
            ), 0
        )).perform(click())

        onView(isRoot()).perform(waitUntilGone(R.id.tutorial_canvas))
    }

    @Test
    fun testCloseTutorialByPressingExit(){
        onView((
                allOf(
                    withId(R.id.close_button),
                    withEffectiveVisibility(Visibility.VISIBLE)
                )
                )).perform(click())

        onView(isRoot()).perform(waitUntilGone(R.id.tutorial_canvas))
    }

    @Test
    fun testEmojiAppearsInCommandBarOnClick() {
        testCloseTutorialByPressingExit()

        onView(
            CoreMatchers.allOf(
                withId(R.id.puzzle_fragment_container),
                CoreMatchers.instanceOf(FragmentContainerView::class.java)
            )
        ).check(matches(isDisplayed()))

        onView(
            allOf(
                withTagValue(`is`("Wood")),
                isDisplayed())).perform(click())
        onView(
            allOf(withTagValue(`is`("Wood")),
                withParent(withId(R.id.command_bar))))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testLogEmojiResultsInWin() {
        testCloseTutorialByPressingExit()

        onView(
            CoreMatchers.allOf(
                withId(R.id.puzzle_fragment_container),
                CoreMatchers.instanceOf(FragmentContainerView::class.java)
            )
        ).check(matches(isDisplayed()))

        onView(allOf(withTagValue(`is`("Wood")), isDisplayed())).perform(click())
        onView(allOf(withId(R.id.play_button_inclusion), withEffectiveVisibility(Visibility.VISIBLE))).perform(click())

        Thread.sleep(2000)
        onView(withText(R.string.game_ended)).check(matches(isDisplayed()))
        onView(withText(R.string.you_won_emoji)).check(matches(isDisplayed()))
    }

    @Test
    fun testRemovingOneOfTwoWoodEmojisInTutorial() {
        // Select Wood emoji twice
        repeat(2) {
            onView(withIndex(
                allOf(
                    withTagValue(`is`("Wood")),
                    withEffectiveVisibility(Visibility.VISIBLE)
                ), 0)
            ).perform(click())
        }

        onView(
            withIndex(withId(R.id.command_bar), 0)
        ).check(matches(hasEmojiWithTagCount("Wood", 2)))

        onView(
            allOf(
                withTagValue(`is`("Wood")),
                isDescendantOfA(withIndex(withId(R.id.command_bar), 0)),
                isDisplayed()
            )
        ).perform(click())

        onView(
            withIndex(withId(R.id.command_bar), 1)
        ).check(matches(hasEmojiWithTagCount("Wood", 1)))
    }


    private fun withIndex(matcher: Matcher<View>, index: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            var currentIndex = 0
            override fun describeTo(description: Description) {
                description.appendText("with index: $index ")
                matcher.describeTo(description)
            }
            public override fun matchesSafely(view: View): Boolean {
                return matcher.matches(view) && currentIndex++ == index
            }
        }
    }


    private fun hasEmojiWithTagCount(tag: String, expectedCount: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("has $expectedCount children with tag=$tag")
            }

            override fun matchesSafely(view: View): Boolean {
                if (view !is ViewGroup) return false
                val count = (0 until view.childCount)
                    .map { view.getChildAt(it) }
                    .count { it.tag == tag }
                return count == expectedCount
            }
        }
    }

    private fun waitUntilGone(viewId: Int, timeout: Long = 5000): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> = isRoot()
            override fun getDescription() = "Wait until view with id $viewId is gone"
            override fun perform(uiController: UiController, view: View) {
                val startTime = System.currentTimeMillis()
                val endTime = startTime + timeout
                do {
                    val viewToCheck = view.findViewById<View>(viewId)
                    if (viewToCheck == null || viewToCheck.visibility != View.VISIBLE) return
                    uiController.loopMainThreadForAtLeast(100)
                } while (System.currentTimeMillis() < endTime)
                throw AssertionError("View with id $viewId is still visible after $timeout ms.")
            }
        }
    }
}
