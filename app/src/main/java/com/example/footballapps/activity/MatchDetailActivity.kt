package com.example.footballapps.activity

import android.annotation.SuppressLint
import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.footballapps.R
import com.example.footballapps.adapter.MatchRecyclerViewAdapter
import com.example.footballapps.favorite.FavoriteMatchItem
import com.example.footballapps.helper.database
import com.example.footballapps.model.MatchItem
import com.example.footballapps.presenter.MatchDetailPresenter
import com.example.footballapps.utils.gone
import com.example.footballapps.utils.invisible
import com.example.footballapps.utils.visible
import com.example.footballapps.view.MatchDetailView
import kotlinx.android.synthetic.main.activity_match_detail.*
import kotlinx.android.synthetic.main.layout_match_detail_event_info.*
import kotlinx.android.synthetic.main.layout_match_detail_teams_info.*
import kotlinx.android.synthetic.main.layout_match_detail_teams_stats.*
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.delete
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select
import org.jetbrains.anko.design.snackbar
import java.text.SimpleDateFormat
import java.util.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
@SuppressLint("SimpleDateFormat")
class MatchDetailActivity : AppCompatActivity(), MatchDetailView {

    private lateinit var eventId: String
    private lateinit var eventName: String
    private lateinit var homeTeamId: String
    private lateinit var awayTeamId: String

    private lateinit var matchDetailPresenter: MatchDetailPresenter

    private var menuItem: Menu? = null
    private var isEventFavorite: Boolean = false

    private var favoriteMatchItem : MatchItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match_detail)

        initData()
    }

    private fun initData() {
        val intent = intent

        eventId = intent.getStringExtra("eventId")
        eventName = intent.getStringExtra("eventName")
        homeTeamId = intent.getStringExtra("homeTeamId")
        awayTeamId = intent.getStringExtra("awayTeamId")

        setToolbarBehavior()

        matchDetailPresenter = MatchDetailPresenter(this)

        matchDetailPresenter.getDetailMatchInfo(eventId, homeTeamId, awayTeamId)

        match_detail_swipe_refresh_layout.setOnRefreshListener {
            matchDetailPresenter.getDetailMatchInfo(eventId, homeTeamId, awayTeamId)
        }

        match_detail_swipe_refresh_layout.setColorSchemeColors(
            ContextCompat.getColor(
                this,
                R.color.colorAccent
            )
        )

        checkFavoriteMatchState()
    }

    private fun setToolbarBehavior() {
        setSupportActionBar(toolbar_detail_match)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = eventName
    }

    override fun dataIsLoading() {
        progress_bar_match_detail.visible()
        match_detail_error_data_text.gone()
        layout_match_detail_data.invisible()
    }

    override fun dataLoadingFinished() {
        match_detail_swipe_refresh_layout.isRefreshing = false
        progress_bar_match_detail.gone()
        match_detail_error_data_text.gone()
        layout_match_detail_data.visible()
    }

    override fun dataFailedToLoad(errorText: String) {
        match_detail_swipe_refresh_layout.isRefreshing = false
        favoriteMatchItem = null
        progress_bar_match_detail.gone()
        match_detail_error_data_text.visible()
        layout_match_detail_data.invisible()

        match_detail_error_data_text.text = errorText
    }

    override fun showMatchData(matchItem: MatchItem) {

        favoriteMatchItem = matchItem

        match_detail_league_name.text = matchItem.leagueName ?: "-"
        match_detail_match_week.text = when {
            matchItem.leagueMatchWeek != null -> StringBuilder("Week ${matchItem.leagueMatchWeek}")
            else -> resources.getString(R.string.match_week_unknown)
        }

        val arrayLocalTimeDt = convertDateTimeToLocalTimeZone(
            formatDate(matchItem.dateEvent),
            formatTime(matchItem.timeEvent)
        )

        match_detail_event_date.text = arrayLocalTimeDt[0]
        match_detail_event_time.text = arrayLocalTimeDt[1]

        match_detail_home_team_name.text = matchItem.homeTeamName ?: "-"
        match_detail_home_team_score.text = matchItem.homeTeamScore ?: "-"
        match_detail_away_team_score.text = matchItem.awayTeamScore ?: "-"
        match_detail_away_team_name.text = matchItem.awayTeamName ?: "-"

        tv_match_detail_spectators.text = when {
            matchItem.spectators != null -> StringBuilder("Spectators : ${matchItem.spectators}")
            else -> resources.getString(R.string.spectators_unknown)
        }

        match_detail_home_goal_scorers.text =
            createTextFromStringValue(matchItem.homeTeamGoalDetails)
        match_detail_away_goal_scorers.text =
            createTextFromStringValue(matchItem.awayTeamGoalDetails)

        match_detail_home_yellow_cards.text =
            createTextFromStringValue(matchItem.homeTeamYellowCards)
        match_detail_away_yellow_cards.text =
            createTextFromStringValue(matchItem.awayTeamYellowCards)

        match_detail_home_red_cards.text = createTextFromStringValue(matchItem.homeTeamRedCards)
        match_detail_away_red_cards.text = createTextFromStringValue(matchItem.awayTeamRedCards)

        match_detail_home_total_shots.text = createTextFromStringValue(matchItem.homeTeamShots)
        match_detail_away_total_shots.text = createTextFromStringValue(matchItem.awayTeamShots)

        match_detail_home_formation.text = createTextFromStringValue(matchItem.homeTeamFormation)
        match_detail_away_formation.text = createTextFromStringValue(matchItem.awayTeamFormation)

        match_detail_home_goal_keeper.text = createTextFromStringValue(matchItem.homeTeamGoalkeeper)
        match_detail_away_goal_keeper.text = createTextFromStringValue(matchItem.awayTeamGoalkeeper)

        match_detail_home_defenders.text = createTextFromStringValue(matchItem.homeTeamDefense)
        match_detail_away_defenders.text = createTextFromStringValue(matchItem.awayTeamDefense)

        match_detail_home_midfielders.text = createTextFromStringValue(matchItem.homeTeamMidfield)
        match_detail_away_midfielders.text = createTextFromStringValue(matchItem.awayTeamMidfield)

        match_detail_home_forwards.text = createTextFromStringValue(matchItem.homeTeamForward)
        match_detail_away_forwards.text = createTextFromStringValue(matchItem.awayTeamForward)

        match_detail_home_substitutes.text =
            createTextFromStringValue(matchItem.homeTeamSubstitutes)
        match_detail_away_substitutes.text =
            createTextFromStringValue(matchItem.awayTeamSubstitutes)
    }

    override fun showHomeTeamBadge(homeTeamBadgeUrl: String?) {
        Glide
            .with(this)
            .load(homeTeamBadgeUrl)
            .centerCrop()
            .error(Glide.with(this).load(R.drawable.team_badge_placeholder))
            .into(match_detail_home_team_logo)
    }

    override fun showAwayTeamBadge(awayTeamBadgeUrl: String?) {
        Glide
            .with(this)
            .load(awayTeamBadgeUrl)
            .centerCrop()
            .error(Glide.with(this).load(R.drawable.team_badge_placeholder))
            .into(match_detail_away_team_logo)
    }

    private fun createTextFromStringValue(stringValue: String?): String {
        if (stringValue != null && stringValue.isNotEmpty()) {
            val arrayStringValue = stringValue.split(";")
            val filteredArrayStringValue = arrayStringValue.filter { it.trim().isNotEmpty() }

            val stringBuilder = StringBuilder()

            for (i in filteredArrayStringValue.indices) {
                when (i) {
                    filteredArrayStringValue.size - 1 -> stringBuilder.append(
                        filteredArrayStringValue[i].trim()
                    )
                    else -> stringBuilder.append(filteredArrayStringValue[i].trim() + "\n")
                }
            }

            return stringBuilder.toString()
        } else {
            return "-"
        }

    }

    private fun formatDate(stringValue: String?): String {
        return if (stringValue != null && stringValue.isNotEmpty()) {
            val oldDateFormat = SimpleDateFormat("yyyy-MM-dd")
            val newDateFormat = SimpleDateFormat("dd MMM yyyy")
            val date: Date = oldDateFormat.parse(stringValue)
            val formattedDateEvent = newDateFormat.format(date)

            formattedDateEvent
        } else {
            MatchRecyclerViewAdapter.MatchViewHolder.dateUnknown
        }
    }

    private fun formatTime(stringValue: String?): String {
        return if (stringValue != null && stringValue.isNotEmpty()
            && stringValue != "00:00:00" && stringValue != "23:59:59"
        ) {
            val timeFormat = SimpleDateFormat("HH:mm")
            val timeInDate: Date = timeFormat.parse(stringValue)
            val formattedTimeEvent = timeFormat.format(timeInDate)

            formattedTimeEvent
        } else {
            MatchRecyclerViewAdapter.MatchViewHolder.timeUnknown
        }
    }

    private fun convertDateTimeToLocalTimeZone(date: String, time: String): List<String> {

        lateinit var localTimeArray: List<String>

        if (date != MatchRecyclerViewAdapter.MatchViewHolder.dateUnknown &&
            time != MatchRecyclerViewAdapter.MatchViewHolder.timeUnknown
        ) {
            val dateTimeStr = "$date,$time"
            val dateTimeFormat = SimpleDateFormat("dd MMM yyyy,HH:mm", Locale.ENGLISH)
            dateTimeFormat.timeZone = TimeZone.getTimeZone("UTC")
            val dateTime = dateTimeFormat.parse(dateTimeStr)
            dateTimeFormat.timeZone = TimeZone.getDefault()
            val localTimeDtFormat = dateTimeFormat.format(dateTime)
            Log.d("local date time", localTimeDtFormat)
            localTimeArray = localTimeDtFormat.split(",")

        } else {
            val localTimeDtFormat = "$date,$time"
            Log.d("local date time", localTimeDtFormat)
            localTimeArray = localTimeDtFormat.split(",")
        }

        return localTimeArray
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_favorite, menu)
        menuItem = menu
        setFavoriteMatchIcon()
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_add_to_favorite -> {
                if(favoriteMatchItem != null){
                    if(isEventFavorite) removeFromFavoriteEvent() else addToFavoriteEvent()

                    isEventFavorite = !isEventFavorite
                    setFavoriteMatchIcon()
                }

                true
            }
            else -> super.onOptionsItemSelected(item)

        }
    }

    private fun addToFavoriteEvent(){
        try{
            val arrayLocalTimeDt = convertDateTimeToLocalTimeZone(
                formatDate(favoriteMatchItem?.dateEvent),
                formatTime(favoriteMatchItem?.timeEvent)
            )
            database.use {
                insert(
                    FavoriteMatchItem.TABLE_FAVORITE_MATCH,
                    FavoriteMatchItem.EVENT_ID to favoriteMatchItem?.idEvent,
                    FavoriteMatchItem.EVENT_NAME to favoriteMatchItem?.strEvent,
                    FavoriteMatchItem.LEAGUE_NAME to favoriteMatchItem?.leagueName,
                    FavoriteMatchItem.EVENT_DATE to arrayLocalTimeDt[0],
                    FavoriteMatchItem.LEAGUE_MATCH_WEEK to favoriteMatchItem?.leagueMatchWeek,
                    FavoriteMatchItem.EVENT_TIME to arrayLocalTimeDt[1],
                    FavoriteMatchItem.HOME_TEAM_ID to favoriteMatchItem?.homeTeamId,
                    FavoriteMatchItem.AWAY_TEAM_ID to favoriteMatchItem?.awayTeamId,
                    FavoriteMatchItem.HOME_TEAM_NAME to favoriteMatchItem?.homeTeamName,
                    FavoriteMatchItem.AWAY_TEAM_NAME to favoriteMatchItem?.awayTeamName,
                    FavoriteMatchItem.HOME_TEAM_SCORE to favoriteMatchItem?.homeTeamScore,
                    FavoriteMatchItem.AWAY_TEAM_SCORE to favoriteMatchItem?.awayTeamScore
                )
            }
            match_detail_swipe_refresh_layout.snackbar("Add an event into favorites").show()
        } catch (e : SQLiteConstraintException) {
            match_detail_swipe_refresh_layout.snackbar(e.localizedMessage).show()
        }
    }

    private fun removeFromFavoriteEvent(){
        try {
            database.use {
                delete(
                    FavoriteMatchItem.TABLE_FAVORITE_MATCH,
                    "(EVENT_ID = {eventId})",
                    "eventId" to eventId
                )

            }
            match_detail_swipe_refresh_layout.snackbar("Remove an event from favorites").show()
        } catch (e : SQLiteConstraintException){
            match_detail_swipe_refresh_layout.snackbar(e.localizedMessage).show()
        }
    }

    private fun setFavoriteMatchIcon(){
        if(isEventFavorite){
            menuItem?.getItem(0)?.icon = ContextCompat.getDrawable(this, R.drawable.ic_added_to_favorites)
        } else {
            menuItem?.getItem(0)?.icon = ContextCompat.getDrawable(this, R.drawable.ic_add_to_favorites)
        }
    }

    private fun checkFavoriteMatchState(){
        database.use {
            val result = select(FavoriteMatchItem.TABLE_FAVORITE_MATCH)
                .whereArgs("(EVENT_ID = {eventId})", "eventId" to eventId)
            val favorite = result.parseList(classParser<FavoriteMatchItem>())
            if(favorite.isNotEmpty()) isEventFavorite = true

        }
    }

}
