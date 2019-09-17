package com.example.footballapps.activity

import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.example.footballapps.R
import org.hamcrest.Matchers.anything
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @Rule
    @JvmField
    var activityRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun testAppBehaviour() {
        onView(withId(R.id.rv_league_list)).check(matches(isDisplayed()))
        onView(withId(R.id.rv_league_list)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                0,
                click()
            )
        )

        onView(withId(R.id.league_detail_layout)).check(matches(isDisplayed()))
        onView(withId(R.id.action_match_schedule)).check(matches(isDisplayed()))
        onView(withId(R.id.action_match_schedule)).perform(click())

        onView(withId(R.id.rv_last_match)).check(matches(isDisplayed()))
        onView(withId(R.id.last_match_league_spinner)).check(matches(withSpinnerText("English Premier League")))
            .perform(click())
        onData(anything()).atPosition(4).perform(click())
        onView(withId(R.id.last_match_league_spinner)).check(matches(withSpinnerText("Spanish La Liga")))
        onView(withId(R.id.rv_last_match)).check(matches(isDisplayed()))
        onView(withId(R.id.rv_last_match)).perform(
            RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(
                10
            )
        )
        onView(withId(R.id.rv_last_match)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                10,
                click()
            )
        )

        onView(withId(R.id.layout_match_detail_data)).check(matches(isDisplayed()))
        onView(withId(R.id.action_add_to_favorite)).check(matches(isDisplayed()))
        onView(withId(R.id.action_add_to_favorite)).perform(click())
        Espresso.pressBack()

        onView(withId(R.id.view_pager_match_schedule)).perform(swipeLeft())
        onView(withId(R.id.rv_next_match)).check(matches(isDisplayed()))
        onView(withId(R.id.rv_next_match)).perform(
            RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(
                7
            )
        )
        onView(withId(R.id.rv_next_match)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                7,
                click()
            )
        )

        onView(withId(R.id.layout_match_detail_data)).check(matches(isDisplayed()))
        onView(withId(R.id.action_add_to_favorite)).check(matches(isDisplayed()))
        onView(withId(R.id.action_add_to_favorite)).perform(click())
        Espresso.pressBack()

        onView(withId(R.id.action_search)).check(matches(isDisplayed()))
        onView(withId(R.id.action_search)).perform(click())
        onView(isAssignableFrom(EditText::class.java)).perform(
            typeText("man united"),
            pressImeActionButton()
        )
        onView(withId(R.id.rv_next_match)).check(matches(isDisplayed()))
        onView(withId(R.id.rv_next_match)).perform(
            RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(
                5
            )
        )
        onView(withId(R.id.rv_next_match)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                5,
                click()
            )
        )

        onView(withId(R.id.layout_match_detail_data)).check(matches(isDisplayed()))
        onView(withId(R.id.action_add_to_favorite)).check(matches(isDisplayed()))
        onView(withId(R.id.action_add_to_favorite)).perform(click())
        Espresso.pressBack()

        onView(withId(R.id.view_pager_match_schedule)).perform(swipeRight())
        onView(withId(R.id.rv_last_match)).check(matches(isDisplayed()))
        onView(withText("Next Match")).perform(click())
        onView(withId(R.id.next_match_parent_layout)).check(matches(isDisplayed()))
        onView(withId(R.id.rv_next_match)).check(matches(isDisplayed()))
        onView(withText("Last Match")).perform(click())
        onView(withId(R.id.last_match_parent_layout)).check(matches(isDisplayed()))
        onView(withId(R.id.rv_last_match)).check(matches(isDisplayed()))

        onView(withId(R.id.action_search)).check(matches(isDisplayed()))
        onView(withId(R.id.action_search)).perform(click())
        onView(isAssignableFrom(EditText::class.java)).perform(
            typeText("barcelona"),
            pressImeActionButton()
        )
        onView(withId(R.id.rv_last_match)).check(matches(isDisplayed()))
        onView(isAssignableFrom(EditText::class.java)).perform(clearText())
        onView(isAssignableFrom(EditText::class.java)).perform(
            typeText("liverpool"),
            pressImeActionButton()
        )
        onView(withId(R.id.rv_last_match)).check(matches(isDisplayed()))
        Espresso.pressBack()
        onView(withId(R.id.rv_last_match)).check(matches(isDisplayed()))

        onView(withId(R.id.menu_item_favorite)).perform(click())
        onView(withId(R.id.rv_favorite_match)).check(matches(isDisplayed()))
        onView(withId(R.id.action_search)).check(matches(isDisplayed()))
        onView(withId(R.id.action_search)).perform(click())
        onView(isAssignableFrom(EditText::class.java)).perform(
            typeText("man united"),
            pressImeActionButton()
        )
        onView(withId(R.id.rv_favorite_match)).check(matches(isDisplayed()))
        onView(withId(R.id.rv_favorite_match)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                0,
                click()
            )
        )

        onView(withId(R.id.layout_match_detail_data)).check(matches(isDisplayed()))
        onView(withId(R.id.action_add_to_favorite)).check(matches(isDisplayed()))
        onView(withId(R.id.action_add_to_favorite)).perform(click())
        Espresso.pressBack()

        Espresso.pressBack()

        onView(withId(R.id.menu_item_match_schedule)).perform(click())
        onView(withId(R.id.rv_last_match)).check(matches(isDisplayed()))

    }
}