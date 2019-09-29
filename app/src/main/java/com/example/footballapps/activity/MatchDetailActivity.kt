package com.example.footballapps.activity

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.footballapps.R
import com.example.footballapps.adapter.MatchRecyclerViewAdapter
import com.example.footballapps.espresso.EspressoIdlingResource
import com.example.footballapps.favorite.FavoriteMatchItem
import com.example.footballapps.helper.database
import com.example.footballapps.model.CombinedMatchTeamsResponse
import com.example.footballapps.model.MatchItem
import com.example.footballapps.presenter.MatchDetailPresenter
import com.example.footballapps.repository.MatchDetailRepository
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

    private var matchItem: MatchItem? = null
    private var favMatchItem: FavoriteMatchItem? = null

    private lateinit var matchDetailPresenter: MatchDetailPresenter

    private var menuItem: Menu? = null
    private var isEventFavorite: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match_detail)

        initData()
    }

    // todo : tinggal pake varaiable yang berguna untuk menampung placeholder saja :), tar matchnya itu pake liverpool vs southampton perhaps

    private fun initData() {
        val intent = intent

        matchItem = intent.getParcelableExtra("matchItem")
        favMatchItem = intent.getParcelableExtra("favMatchItem")

        when {
            matchItem != null -> {
                eventId = matchItem?.idEvent ?: ""
                eventName = matchItem?.strEvent ?: ""
                homeTeamId = matchItem?.homeTeamId ?: ""
                awayTeamId = matchItem?.awayTeamId ?: ""
            }
            favMatchItem != null -> {
                eventId = favMatchItem?.idEvent ?: ""
                eventName = favMatchItem?.strEvent ?: ""
                homeTeamId = favMatchItem?.homeTeamId ?: ""
                awayTeamId = favMatchItem?.awayTeamId ?: ""
            }
            else -> {
                eventId = ""
                eventName = ""
                homeTeamId = ""
                awayTeamId = ""
            }
        }

        setToolbarBehavior()

        matchDetailPresenter = MatchDetailPresenter(this, MatchDetailRepository())

        EspressoIdlingResource.increment()
        matchDetailPresenter.getDetailMatchInfo(eventId, homeTeamId, awayTeamId)

        match_detail_swipe_refresh_layout.setOnRefreshListener {
            EspressoIdlingResource.increment()
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
        if (!EspressoIdlingResource.idlingResource.isIdleNow) {
            EspressoIdlingResource.decrement()
        }
        match_detail_swipe_refresh_layout.isRefreshing = false
        progress_bar_match_detail.gone()
        match_detail_error_data_text.gone()
        layout_match_detail_data.visible()
    }

    override fun dataFailedToLoad() {
        if (!EspressoIdlingResource.idlingResource.isIdleNow) {
            EspressoIdlingResource.decrement()
        }
        match_detail_swipe_refresh_layout.isRefreshing = false
        progress_bar_match_detail.gone()
        match_detail_error_data_text.visible()
        layout_match_detail_data.invisible()

        val isNetworkConnected = checkNetworkConnection()
        if (isNetworkConnected) {
            match_detail_error_data_text.text = resources.getString(R.string.no_data_to_show)
        } else {
            match_detail_error_data_text.text = resources.getString(R.string.no_internet_connection)
        }

    }

    override fun showMatchDetailData(combinedMatchTeamsResponse: CombinedMatchTeamsResponse) {
        val matchDetailResponse = combinedMatchTeamsResponse.matchDetailResponse
        val matchDetailItem = matchDetailResponse.events?.first()

        match_detail_league_name.text =
            matchDetailItem?.leagueName ?: resources.getString(R.string.value_none)
        match_detail_match_week.text =
            StringBuilder("Week ${formatValue(matchDetailItem?.leagueMatchWeek)}")

        val localizedDateTime = convertDateTimeToLocalTimeZone(
            formatDate(matchDetailItem?.dateEvent),
            formatTime(matchDetailItem?.timeEvent)
        )

        match_detail_event_date.text = localizedDateTime[0]
        match_detail_event_time.text = localizedDateTime[1]

        match_detail_home_team_name.text =
            matchDetailItem?.homeTeamName ?: resources.getString(R.string.value_none)
        match_detail_home_team_score.text =
            matchDetailItem?.homeTeamScore ?: resources.getString(R.string.value_none)
        match_detail_away_team_score.text =
            matchDetailItem?.awayTeamScore ?: resources.getString(R.string.value_none)
        match_detail_away_team_name.text =
            matchDetailItem?.awayTeamName ?: resources.getString(R.string.value_none)

        tv_match_detail_spectators.text =
            StringBuilder("Spectators : ${formatValue(matchDetailItem?.spectators)}")

        match_detail_home_goal_scorers.text =
            createTextFromStringValue(matchDetailItem?.homeTeamGoalDetails)
        match_detail_away_goal_scorers.text =
            createTextFromStringValue(matchDetailItem?.awayTeamGoalDetails)

        match_detail_home_yellow_cards.text =
            createTextFromStringValue(matchDetailItem?.homeTeamYellowCards)
        match_detail_away_yellow_cards.text =
            createTextFromStringValue(matchDetailItem?.awayTeamYellowCards)

        match_detail_home_red_cards.text =
            createTextFromStringValue(matchDetailItem?.homeTeamRedCards)
        match_detail_away_red_cards.text =
            createTextFromStringValue(matchDetailItem?.awayTeamRedCards)

        match_detail_home_total_shots.text =
            createTextFromStringValue(matchDetailItem?.homeTeamShots)
        match_detail_away_total_shots.text =
            createTextFromStringValue(matchDetailItem?.awayTeamShots)

        match_detail_home_formation.text =
            createTextFromStringValue(matchDetailItem?.homeTeamFormation)
        match_detail_away_formation.text =
            createTextFromStringValue(matchDetailItem?.awayTeamFormation)

        match_detail_home_goal_keeper.text =
            createTextFromStringValue(matchDetailItem?.homeTeamGoalkeeper)
        match_detail_away_goal_keeper.text =
            createTextFromStringValue(matchDetailItem?.awayTeamGoalkeeper)

        match_detail_home_defenders.text =
            createTextFromStringValue(matchDetailItem?.homeTeamDefense)
        match_detail_away_defenders.text =
            createTextFromStringValue(matchDetailItem?.awayTeamDefense)

        match_detail_home_midfielders.text =
            createTextFromStringValue(matchDetailItem?.homeTeamMidfield)
        match_detail_away_midfielders.text =
            createTextFromStringValue(matchDetailItem?.awayTeamMidfield)

        match_detail_home_forwards.text =
            createTextFromStringValue(matchDetailItem?.homeTeamForward)
        match_detail_away_forwards.text =
            createTextFromStringValue(matchDetailItem?.awayTeamForward)

        match_detail_home_substitutes.text =
            createTextFromStringValue(matchDetailItem?.homeTeamSubstitutes)
        match_detail_away_substitutes.text =
            createTextFromStringValue(matchDetailItem?.awayTeamSubstitutes)

        val homeTeamResponse = combinedMatchTeamsResponse.homeTeamResponse
        val homeTeamItem = homeTeamResponse.teams?.first()
        val homeTeamItemBadgeUrl = homeTeamItem?.teamBadge

        Glide
            .with(applicationContext)
            .load(homeTeamItemBadgeUrl)
            .centerCrop()
            .placeholder(R.drawable.team_badge_placeholder)
            .into(match_detail_home_team_logo)

        val awayTeamResponse = combinedMatchTeamsResponse.awayTeamResponse
        val awayTeamItem = awayTeamResponse.teams?.first()
        val awayTeamItemBadgeUrl = awayTeamItem?.teamBadge

        Glide
            .with(applicationContext)
            .load(awayTeamItemBadgeUrl)
            .centerCrop()
            .placeholder(R.drawable.team_badge_placeholder)
            .into(match_detail_away_team_logo)
    }

    private fun formatValue(stringValue: String?): String {
        return if (stringValue != null && stringValue.trim().isNotEmpty()) {
            stringValue
        } else {
            resources.getString(R.string.value_unknown)
        }
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
            return resources.getString(R.string.value_none)
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
            localTimeArray = localTimeDtFormat.split(",")

        } else {
            val localTimeDtFormat = "$date,$time"
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
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_add_to_favorite -> {
                if (favMatchItem != null || matchItem != null) {
                    changeFavoriteMatchState()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)

        }
    }

    private fun addMatchToFavoriteMatches() {
        try {
            when {
                favMatchItem != null -> {
                    database.use {
                        insert(
                            FavoriteMatchItem.TABLE_FAVORITE_MATCH,
                            FavoriteMatchItem.EVENT_ID to favMatchItem?.idEvent,
                            FavoriteMatchItem.EVENT_NAME to favMatchItem?.strEvent,
                            FavoriteMatchItem.EVENT_DATE to favMatchItem?.dateEvent,
                            FavoriteMatchItem.EVENT_TIME to favMatchItem?.timeEvent,
                            FavoriteMatchItem.LEAGUE_NAME to favMatchItem?.leagueName,
                            FavoriteMatchItem.LEAGUE_MATCH_WEEK to favMatchItem?.leagueMatchWeek,
                            FavoriteMatchItem.HOME_TEAM_ID to favMatchItem?.homeTeamId,
                            FavoriteMatchItem.AWAY_TEAM_ID to favMatchItem?.awayTeamId,
                            FavoriteMatchItem.HOME_TEAM_NAME to favMatchItem?.homeTeamName,
                            FavoriteMatchItem.AWAY_TEAM_NAME to favMatchItem?.awayTeamName,
                            FavoriteMatchItem.HOME_TEAM_SCORE to favMatchItem?.homeTeamScore,
                            FavoriteMatchItem.AWAY_TEAM_SCORE to favMatchItem?.awayTeamScore
                        )
                    }
                }
                matchItem != null -> {
                    database.use {
                        insert(
                            FavoriteMatchItem.TABLE_FAVORITE_MATCH,
                            FavoriteMatchItem.EVENT_ID to matchItem?.idEvent,
                            FavoriteMatchItem.EVENT_NAME to matchItem?.strEvent,
                            FavoriteMatchItem.EVENT_DATE to matchItem?.dateEvent,
                            FavoriteMatchItem.EVENT_TIME to matchItem?.timeEvent,
                            FavoriteMatchItem.LEAGUE_NAME to matchItem?.leagueName,
                            FavoriteMatchItem.LEAGUE_MATCH_WEEK to matchItem?.leagueMatchWeek,
                            FavoriteMatchItem.HOME_TEAM_ID to matchItem?.homeTeamId,
                            FavoriteMatchItem.AWAY_TEAM_ID to matchItem?.awayTeamId,
                            FavoriteMatchItem.HOME_TEAM_NAME to matchItem?.homeTeamName,
                            FavoriteMatchItem.AWAY_TEAM_NAME to matchItem?.awayTeamName,
                            FavoriteMatchItem.HOME_TEAM_SCORE to matchItem?.homeTeamScore,
                            FavoriteMatchItem.AWAY_TEAM_SCORE to matchItem?.awayTeamScore
                        )
                    }
                }
            }
            match_detail_swipe_refresh_layout.snackbar("Add an event into favorites").setAction(
                getString(
                    R.string.undo
                )
            ) { changeFavoriteMatchState() }.show()
        } catch (e: SQLiteConstraintException) {
            match_detail_swipe_refresh_layout.snackbar(e.localizedMessage).show()
        }
    }

    private fun removeMatchFromFavoriteMatches() {
        try {
            database.use {
                delete(
                    FavoriteMatchItem.TABLE_FAVORITE_MATCH,
                    "(EVENT_ID = {eventId})",
                    "eventId" to eventId
                )

            }
            match_detail_swipe_refresh_layout.snackbar("Remove an event from favorites").setAction(
                getString(
                    R.string.undo
                )
            ) { changeFavoriteMatchState() }.show()
        } catch (e: SQLiteConstraintException) {
            match_detail_swipe_refresh_layout.snackbar(e.localizedMessage).show()
        }
    }

    private fun changeFavoriteMatchState() {
        if (isEventFavorite) removeMatchFromFavoriteMatches() else addMatchToFavoriteMatches()

        isEventFavorite = !isEventFavorite
        setFavoriteMatchIcon()
    }

    private fun setFavoriteMatchIcon() {
        if (isEventFavorite) {
            menuItem?.getItem(0)?.icon =
                ContextCompat.getDrawable(this, R.drawable.ic_added_to_favorites)
        } else {
            menuItem?.getItem(0)?.icon =
                ContextCompat.getDrawable(this, R.drawable.ic_add_to_favorites)
        }
    }

    private fun checkFavoriteMatchState() {
        database.use {
            val result = select(FavoriteMatchItem.TABLE_FAVORITE_MATCH)
                .whereArgs("(EVENT_ID = {eventId})", "eventId" to eventId)
            val favorite = result.parseList(classParser<FavoriteMatchItem>())
            if (favorite.isNotEmpty()) isEventFavorite = true

        }
    }

    @Suppress("DEPRECATION")
    private fun checkNetworkConnection(): Boolean {

        val connectivityManager: ConnectivityManager? =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT < 23) {
                val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo

                if (networkInfo != null) {
                    return (networkInfo.isConnected && (networkInfo.type == ConnectivityManager.TYPE_WIFI || networkInfo.type == ConnectivityManager.TYPE_MOBILE || networkInfo.type == ConnectivityManager.TYPE_VPN))
                }

            } else {
                val network: Network? = connectivityManager.activeNetwork

                if (network != null) {
                    val networkCapabilities: NetworkCapabilities =
                        connectivityManager.getNetworkCapabilities(network)!!


                    return (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                            || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                            || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN))
                }
            }
        }
        return false
    }


}
