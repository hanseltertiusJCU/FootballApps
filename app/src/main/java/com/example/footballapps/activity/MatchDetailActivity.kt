package com.example.footballapps.activity

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.core.content.res.ResourcesCompat
import com.example.footballapps.R
import com.example.footballapps.adapter.MatchRecyclerViewAdapter
import com.example.footballapps.model.MatchItem
import com.example.footballapps.presenter.MatchPresenter
import com.example.footballapps.utils.gone
import com.example.footballapps.utils.invisible
import com.example.footballapps.utils.visible
import com.example.footballapps.view.MatchView
import kotlinx.android.synthetic.main.activity_match_detail.*
import kotlinx.android.synthetic.main.activity_search_match_schedule.*
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
@SuppressLint("SimpleDateFormat")
class MatchDetailActivity : AppCompatActivity(), MatchView {

    lateinit var eventId : String
    lateinit var eventName : String

    lateinit var matchDetailPresenter : MatchPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match_detail)

        initData()
    }

    private fun initData() {
        val intent = intent

        eventId = intent.getStringExtra("eventId")
        eventName = intent.getStringExtra("eventName")

        setSupportActionBar(toolbar_detail_match)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = eventName

        matchDetailPresenter = MatchPresenter(this)

        matchDetailPresenter.getDetailMatchInfo(eventId)

        match_detail_swipe_refresh_layout.setOnRefreshListener {
            matchDetailPresenter.getDetailMatchInfo(eventId)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    // todo: function to check network connectivity


    override fun dataIsLoading() {
        progress_bar_match_detail.visible()
        match_detail_error_data_text.gone()
        layout_match_detail_data.invisible()
    }

    override fun dataLoadingFinished() {
        // todo: check if array list size is equal to 0 or not
        progress_bar_match_detail.gone()
        match_detail_error_data_text.gone()
        layout_match_detail_data.visible()
    }

    override fun showMatchData(matchList: List<MatchItem>) {
        match_detail_swipe_refresh_layout.isRefreshing = false
        for(i in matchList.indices) {
            if(i == 0){
                match_detail_league_name.text = matchList[i].leagueName ?: "-"
                match_detail_match_week.text = when {
                    matchList[i].leagueMatchWeek != null -> StringBuilder("Week ${matchList[i].leagueMatchWeek}")
                    else -> resources.getString(R.string.match_week_unknown)
                }

                val arrayLocalTimeDt = convertDateTimeToLocalTimeZone(formatDate(matchList[i].dateEvent), formatTime(matchList[i].timeEvent))

                match_detail_event_date.text = arrayLocalTimeDt[0]
                match_detail_event_time.text = arrayLocalTimeDt[1]

                match_detail_home_team_name.text = matchList[i].homeTeamName ?: "-"
                match_detail_home_team_score.text = matchList[i].homeTeamScore ?: "-"
                match_detail_away_team_score.text = matchList[i].awayTeamScore ?: "-"
                match_detail_away_team_name.text = matchList[i].awayTeamName ?: "-"

                tv_match_detail_spectators.text = when {
                    matchList[i].spectators != null -> StringBuilder("Spectators : ${matchList[i].spectators}")
                    else -> resources.getString(R.string.spectators_unknown)
                }

                match_detail_home_goal_scorers.text = createTextFromStringValue(matchList[i].homeTeamGoalDetails)
                match_detail_away_goal_scorers.text = createTextFromStringValue(matchList[i].awayTeamGoalDetails)

                match_detail_home_yellow_cards.text = createTextFromStringValue(matchList[i].homeTeamYellowCards)
                match_detail_away_yellow_cards.text = createTextFromStringValue(matchList[i].awayTeamYellowCards)

                match_detail_home_red_cards.text = createTextFromStringValue(matchList[i].homeTeamRedCards)
                match_detail_away_red_cards.text = createTextFromStringValue(matchList[i].awayTeamRedCards)

                match_detail_home_total_shots.text = createTextFromStringValue(matchList[i].homeTeamShots)
                match_detail_away_total_shots.text = createTextFromStringValue(matchList[i].awayTeamShots)

                match_detail_home_formation.text = createTextFromStringValue(matchList[i].homeTeamFormation)
                match_detail_away_formation.text = createTextFromStringValue(matchList[i].awayTeamFormation)

                match_detail_home_goal_keeper.text = createTextFromStringValue(matchList[i].homeTeamGoalkeeper)
                match_detail_away_goal_keeper.text = createTextFromStringValue(matchList[i].awayTeamGoalkeeper)

                match_detail_home_defenders.text = createTextFromStringValue(matchList[i].homeTeamDefense)
                match_detail_away_defenders.text = createTextFromStringValue(matchList[i].awayTeamDefense)

                match_detail_home_midfielders.text = createTextFromStringValue(matchList[i].homeTeamMidfield)
                match_detail_away_midfielders.text = createTextFromStringValue(matchList[i].awayTeamMidfield)

                match_detail_home_forwards.text = createTextFromStringValue(matchList[i].homeTeamForward)
                match_detail_away_forwards.text = createTextFromStringValue(matchList[i].awayTeamForward)

                match_detail_home_substitutes.text = createTextFromStringValue(matchList[i].homeTeamSubstitutes)
                match_detail_away_substitutes.text = createTextFromStringValue(matchList[i].awayTeamSubstitutes)

                break
            }
        }
    }

    private fun createTextFromStringValue(stringValue : String?) : String {
        if(stringValue != null && stringValue.isNotEmpty()) {
            val arrayStringValue = stringValue.split(";")
            val filteredArrayStringValue = arrayStringValue.filter { it.trim().isNotEmpty()}

            val stringBuilder = StringBuilder()

            for(i in filteredArrayStringValue.indices){
                when(i) {
                    filteredArrayStringValue.size - 1 -> stringBuilder.append(filteredArrayStringValue[i].trim())
                    else -> stringBuilder.append(filteredArrayStringValue[i].trim() + "\n")
                }
            }

            return stringBuilder.toString()
        } else {
            return "-"
        }

    }

    private fun formatDate(stringValue : String?) : String {
        return if(stringValue != null && stringValue.isNotEmpty()) {
            val oldDateFormat = SimpleDateFormat("yyyy-MM-dd")
            val newDateFormat = SimpleDateFormat("dd MMM yyyy")
            val date : Date = oldDateFormat.parse(stringValue)
            val formattedDateEvent = newDateFormat.format(date)

            formattedDateEvent
        } else {
            MatchRecyclerViewAdapter.MatchViewHolder.dateUnknown
        }
    }

    private fun formatTime(stringValue: String?) : String {
        // 00:00:00 is the placeholder for unknown time
        return if(stringValue != null && stringValue.isNotEmpty() && stringValue != "00:00:00") {
            val timeFormat = SimpleDateFormat("HH:mm")
            val timeInDate : Date = timeFormat.parse(stringValue)
            val formattedTimeEvent = timeFormat.format(timeInDate)

            formattedTimeEvent
        } else {
            MatchRecyclerViewAdapter.MatchViewHolder.timeUnknown
        }
    }

    private fun convertDateTimeToLocalTimeZone(date : String, time : String) : List<String>{

        lateinit var localTimeArray : List<String>

        if(date != MatchRecyclerViewAdapter.MatchViewHolder.dateUnknown &&
            time != MatchRecyclerViewAdapter.MatchViewHolder.timeUnknown
        ){
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

//    private fun checkDeviceIsConnected() : Boolean {
////        val connectivityManager : ConnectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
////        val networkInfo : Network = connectivityManager.activeNetwork
////
////        return networkInfo != null && networkInfo
//
////        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
////        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
////        val checkDeviceIsConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
//    }
}
