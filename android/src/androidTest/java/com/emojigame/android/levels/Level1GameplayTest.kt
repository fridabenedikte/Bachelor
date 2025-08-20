package com.emojigame.android.levels
import android.content.Context
import android.view.View
import androidx.fragment.app.FragmentContainerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.emojigame.android.R
import com.emojigame.android.base.BaseLevelUiTest
import org.hamcrest.CoreMatchers
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.AllOf.allOf
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Level1GameplayTest : BaseLevelUiTest() {

    override val targetLevel = 1
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun testCompleteTutorialByPressingPlay() {
        onView(
            withIndex(
                allOf(
                    withId(R.id.play_button_inclusion),
                    withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)
                ),
                0
            )
        ).perform(click())

        // Wait until the tutorial overlay disappears
        onView(isRoot()).perform(waitUntilGone(R.id.tutorial_canvas))
    }

    @Test
    fun testCloseTutorialByPressingExit() {
        onView(
            allOf(
                withId(R.id.close_button),
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)
            )
        ).perform(click())

        onView(isRoot()).perform(waitUntilGone(R.id.tutorial_canvas))
    }

    @Test
    fun testReplayTutorialByPressingRetry() {
        onView(
            allOf(
                withId(R.id.reset_button),
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)

            )
        )
        onView(withId(R.id.tutorial_canvas))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testPauseAndResumeAfterTutorial() {
        testCompleteTutorialByPressingPlay()
        onView(
            allOf(
                withId(R.id.play_button_inclusion),
                withEffectiveVisibility((ViewMatchers.Visibility.VISIBLE))
            )
        ).perform(click())

        onView(
            allOf(
                withId(R.id.btnPause),
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)
            )
        ).perform(click())

        onView(
            allOf(
                withId(R.id.btnResume),
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)
            )
        ).perform(click())

        onView(
            CoreMatchers.allOf(
                withId(R.id.puzzle_fragment_container),
                CoreMatchers.instanceOf(FragmentContainerView::class.java)
            )
        ).check(matches(isDisplayed()))
    }

    @Test
    fun testWinLevelAfterTutorial() {
        testCompleteTutorialByPressingPlay()
        onView(
            allOf(
                withId(R.id.play_button_inclusion),
                withEffectiveVisibility((ViewMatchers.Visibility.VISIBLE))
            )
        ).perform(click())

        Thread.sleep(5000)
        onView(withText(context.getString(R.string.game_ended))).check(matches(isDisplayed()))
        onView(withText(context.getString(R.string.you_won_emoji))).check(matches(isDisplayed()))
    }

    @Test
    fun testRestAfterTryAgain() {
        testCompleteTutorialByPressingPlay()
        onView(
            allOf(
                withId(R.id.play_button_inclusion),
                withEffectiveVisibility((ViewMatchers.Visibility.VISIBLE))
            )
        ).perform(click())

        onView(
            allOf(
                withId(R.id.btnPause),
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)
            )
        ).perform(click())

        onView(
            allOf(
                withId(R.id.btnTryAgain),
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)
            )
        ).perform(click())

        onView(withId(R.id.tutorial_canvas))
            .check(matches(isDisplayed()))
    }

    // -----------------------------
    // Helper for selecting by index
    private fun withIndex(matcher: Matcher<View>, index: Int): Matcher<View> {
        var currentIndex = 0
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("with index $index: ")
                matcher.describeTo(description)
            }

            override fun matchesSafely(view: View): Boolean {
                return matcher.matches(view) && currentIndex++ == index
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
