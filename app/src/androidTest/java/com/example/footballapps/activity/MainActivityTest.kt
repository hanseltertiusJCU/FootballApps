package com.example.footballapps.activity

import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.example.footballapps.R
import com.example.footballapps.espresso.EspressoIdlingResource
import org.hamcrest.Matchers.*
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @Rule
    @JvmField
    var activityRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.idlingResource)
    }

    @Test
    fun testCaseALeagueDetail() {
        onView(withId(R.id.rv_league_list)).check(matches(isDisplayed()))
        onView(withId(R.id.rv_league_list)).perform(
            RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(
                4
            )
        )
        onView(withId(R.id.rv_league_list)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                4,
                click()
            )
        )

        onView(withId(R.id.iv_league_detail_logo)).check(matches(withContentDescription(R.string.league_detail_logo_expanded)))
        onView(withId(R.id.league_detail_app_bar_layout)).perform(click(), swipeUp())
        onView(withId(R.id.iv_league_detail_logo)).check(
            matches(
                anyOf(
                    withContentDescription(R.string.league_detail_logo_collapsed),
                    withContentDescription(R.string.league_detail_logo_collapsing)
                )
            )
        )

        onView(withId(R.id.league_detail_layout)).check(matches(isDisplayed()))
        onView(withId(R.id.view_pager_league_detail)).perform(swipeLeft())
        onView(withId(R.id.league_table_layout)).check(matches(isDisplayed()))
        Thread.sleep(500)
        onView(withId(R.id.view_pager_league_detail)).perform(swipeLeft())
        onView(withId(R.id.rv_league_teams)).check(matches(isDisplayed()))
        Thread.sleep(500)
        onView(withId(R.id.view_pager_league_detail)).perform(swipeLeft())
        onView(withId(R.id.rv_league_match)).check(matches(isDisplayed()))
        Thread.sleep(500)

        onView(withId(R.id.action_search)).check(matches(isDisplayed()))
        onView(withId(R.id.action_search)).perform(click())
        onView(isAssignableFrom(EditText::class.java)).perform(
            typeText("barcelona"),
            pressImeActionButton()
        )
        onView(withId(R.id.rv_league_match)).check(matches(isDisplayed()))
        Thread.sleep(500)
        onView(isAssignableFrom(EditText::class.java)).perform(clearText())
        onView(isAssignableFrom(EditText::class.java)).perform(
            typeText("liverpool"),
            pressImeActionButton()
        )
        Espresso.pressBack()
        onView(withId(R.id.rv_league_match)).check(matches(isDisplayed()))
        Thread.sleep(500)
        onView(withId(R.id.rv_league_match)).perform(
            RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(
                4
            )
        )
        onView(withId(R.id.rv_league_match)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                4,
                click()
            )
        )

        onView(withId(R.id.layout_match_detail_data)).check(matches(isDisplayed()))
        Thread.sleep(500)
        onView(withId(R.id.action_add_to_favorite)).check(matches(isDisplayed()))
        onView(withId(R.id.action_add_to_favorite)).perform(click())
        Espresso.pressBack()

        Espresso.pressBack()
        Espresso.pressBack()
        onView(withId(R.id.rv_league_match)).check(matches(isDisplayed()))
        Thread.sleep(500)
        onView(withId(R.id.league_match_spinner)).check(matches(isDisplayed()))
    }

    @Test
    fun testCaseBTeamAndPlayerDetail() {
        onView(withId(R.id.rv_league_list)).check(matches(isDisplayed()))
        onView(withId(R.id.rv_league_list)).perform(
            RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(
                4
            )
        )
        onView(withId(R.id.rv_league_list)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                4,
                click()
            )
        )

        onView(withId(R.id.iv_league_detail_logo)).check(matches(withContentDescription(R.string.league_detail_logo_expanded)))
        onView(withId(R.id.league_detail_app_bar_layout)).perform(click(), swipeUp())
        onView(withId(R.id.iv_league_detail_logo)).check(
            matches(
                anyOf(
                    withContentDescription(R.string.league_detail_logo_collapsed),
                    withContentDescription(R.string.league_detail_logo_collapsing)
                )
            )
        )

        onView(withId(R.id.league_detail_layout)).check(matches(isDisplayed()))
        onView(
            allOf(
                withText("Matches"),
                isDescendantOfA(withId(R.id.tab_layout_league_detail))
            )
        ).perform(
            click()
        )
        onView(withId(R.id.rv_league_match)).check(matches(isDisplayed()))
        Thread.sleep(500)
        onView(
            allOf(
                withText("Teams"),
                isDescendantOfA(withId(R.id.tab_layout_league_detail))
            )
        ).perform(
            click()
        )
        onView(withId(R.id.rv_league_teams)).check(matches(isDisplayed()))
        Thread.sleep(500)

        onView(withId(R.id.rv_league_teams)).perform(
            RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(
                14
            )
        )
        onView(withId(R.id.rv_league_teams)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                14,
                click()
            )
        )

        onView(withId(R.id.team_detail_layout)).check(matches(isDisplayed()))
        Thread.sleep(500)
        onView(withId(R.id.action_add_to_favorite)).check(matches(isDisplayed()))
        onView(withId(R.id.action_add_to_favorite)).perform(click())
        Espresso.pressBack()

        onView(withId(R.id.action_search)).check(matches(isDisplayed()))
        onView(withId(R.id.action_search)).perform(click())
        onView(isAssignableFrom(EditText::class.java)).perform(
            typeText("man united"),
            pressImeActionButton()
        )
        onView(withId(R.id.rv_league_teams)).check(matches(isDisplayed()))
        onView(withId(R.id.rv_league_teams)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                0,
                click()
            )
        )

        onView(withId(R.id.iv_team_detail_logo)).check(matches(withContentDescription(R.string.team_detail_logo_expanded)))
        onView(withId(R.id.team_detail_app_bar_layout)).perform(click(), swipeUp())
        onView(withId(R.id.iv_team_detail_logo)).check(
            matches(
                anyOf(
                    withContentDescription(R.string.team_detail_logo_collapsed),
                    withContentDescription(R.string.team_detail_logo_collapsing)
                )
            )
        )

        onView(withId(R.id.team_detail_layout)).check(matches(isDisplayed()))
        Thread.sleep(500)
        onView(withId(R.id.action_add_to_favorite)).check(matches(isDisplayed()))
        onView(withId(R.id.action_add_to_favorite)).perform(click())

        onView(
            allOf(
                withText("Matches"),
                isDescendantOfA(withId(R.id.tab_layout_team_detail))
            )
        ).perform(
            click()
        )
        onView(withId(R.id.rv_team_match)).check(matches(isDisplayed()))
        Thread.sleep(500)
        onView(withId(R.id.team_match_spinner)).check(matches(withSpinnerText("Last Matches")))
            .perform(click())
        onData(anything()).atPosition(1).perform(click())
        onView(withId(R.id.team_match_spinner)).check(matches(withSpinnerText("Next Matches")))
        onView(withId(R.id.rv_team_match)).check(matches(isDisplayed()))
        onView(withId(R.id.rv_team_match)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                0,
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
            typeText("juventus"),
            pressImeActionButton()
        )
        Espresso.pressBack()
        onView(withId(R.id.rv_team_match)).check(matches(isDisplayed()))
        onView(withId(R.id.rv_team_match)).perform(
            RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(
                4
            )
        )
        onView(withId(R.id.rv_team_match)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                4,
                click()
            )
        )

        onView(withId(R.id.layout_match_detail_data)).check(matches(isDisplayed()))
        onView(withId(R.id.action_add_to_favorite)).check(matches(isDisplayed()))
        onView(withId(R.id.action_add_to_favorite)).perform(click())
        Espresso.pressBack()

        Espresso.pressBack()
        Espresso.pressBack()
        onView(withId(R.id.rv_team_match)).check(matches(isDisplayed()))
        Thread.sleep(500)
        onView(withId(R.id.team_match_spinner)).check(matches(isDisplayed()))

        onView(
            allOf(
                withText("Players"),
                isDescendantOfA(withId(R.id.tab_layout_team_detail))
            )
        ).perform(
            click()
        )
        onView(withId(R.id.rv_team_players)).check(matches(isDisplayed()))
        Thread.sleep(500)
        onView(withId(R.id.rv_team_players)).perform(
            RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(
                20
            )
        )
        onView(withId(R.id.rv_team_players)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                20,
                click()
            )
        )

        onView(withId(R.id.iv_player_fanart)).check(matches(withContentDescription(R.string.player_fanart_expanded)))
        onView(withId(R.id.player_detail_app_bar_layout)).perform(click(), swipeUp())
        onView(withId(R.id.iv_player_fanart)).check(
            matches(
                anyOf(
                    withContentDescription(R.string.player_fanart_collapsed),
                    withContentDescription(R.string.player_fanart_collapsing)
                )
            )
        )

        onView(withId(R.id.layout_player_detail_data)).check(matches(isDisplayed()))
        Espresso.pressBack()
    }

    @Test
    fun testCaseCFavoriteItem() {

        onView(withId(R.id.menu_item_favorite)).perform(click())
        onView(withId(R.id.rv_favorite_match)).check(matches(isDisplayed()))
        onView(withId(R.id.action_search)).check(matches(isDisplayed()))
        onView(withId(R.id.action_search)).perform(click())
        onView(isAssignableFrom(EditText::class.java)).perform(
            typeText("liverpool"),
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
        Espresso.pressBack()
        onView(
            allOf(
                withText("Teams"),
                isDescendantOfA(withId(R.id.tab_layout_favorite_fragment))
            )
        ).perform(
            click()
        )
        onView(withId(R.id.rv_favorite_team)).check(matches(isDisplayed()))
        Thread.sleep(500)
        onView(withId(R.id.action_search)).check(matches(isDisplayed()))
        onView(withId(R.id.action_search)).perform(click())
        onView(isAssignableFrom(EditText::class.java)).perform(
            typeText("real madrid"),
            pressImeActionButton()
        )
        onView(withId(R.id.rv_favorite_team)).check(matches(isDisplayed()))
        onView(withId(R.id.rv_favorite_team)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                0,
                click()
            )
        )

        onView(withId(R.id.team_detail_layout)).check(matches(isDisplayed()))
        onView(withId(R.id.action_add_to_favorite)).check(matches(isDisplayed()))
        onView(withId(R.id.action_add_to_favorite)).perform(click())
        Espresso.pressBack()

        Espresso.pressBack()
        Espresso.pressBack()
        onView(withId(R.id.menu_item_leagues)).perform(click())
        onView(withId(R.id.rv_league_list)).check(matches(isDisplayed()))

    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.idlingResource)
    }
}