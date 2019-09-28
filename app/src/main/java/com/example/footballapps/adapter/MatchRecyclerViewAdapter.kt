package com.example.footballapps.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.footballapps.R
import com.example.footballapps.application.FootballApps
import com.example.footballapps.model.MatchItem
import kotlinx.android.synthetic.main.item_match_data.view.*
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MatchRecyclerViewAdapter(
    private val context: Context,
    private val matchItemList: List<MatchItem>,
    private val clickListener: (MatchItem) -> Unit
) :
    RecyclerView.Adapter<MatchRecyclerViewAdapter.MatchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder =
        MatchViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_match_data,
                parent,
                false
            )
        )

    override fun getItemCount(): Int = matchItemList.size

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        holder.bindItem(matchItemList[position], clickListener)
    }

    @SuppressLint("SimpleDateFormat")
    class MatchViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        companion object {
            val dateUnknown = FootballApps.res.getString(R.string.date_unknown)
            val timeUnknown = FootballApps.res.getString(R.string.time_unknown)
            val valueUnknown = FootballApps.res.getString(R.string.value_unknown)
            val valueNone = FootballApps.res.getString(R.string.value_none)
        }

        fun bindItem(matchItem: MatchItem, clickListener: (MatchItem) -> Unit) {
            itemView.league_item_name.text = matchItem.leagueName ?: StringBuilder("League ${formatValue(matchItem.leagueName)}")
            itemView.league_item_match_week.text = StringBuilder("Week ${formatValue(matchItem.leagueMatchWeek)}")

            val arrayLocalTimeDt = convertDateTimeToLocalTimeZone(
                formatDate(matchItem.dateEvent),
                formatTime(matchItem.timeEvent)
            )

            itemView.league_item_event_date.text = arrayLocalTimeDt[0]

            itemView.league_item_event_time.text = arrayLocalTimeDt[1]

            itemView.league_item_home_team_name.text = formatValue(matchItem.homeTeamName)

            itemView.league_item_home_team_score.text = formatValueData(matchItem.homeTeamScore)

            itemView.league_item_away_team_score.text = formatValueData(matchItem.awayTeamScore)

            itemView.league_item_away_team_name.text = formatValue(matchItem.awayTeamName)

            itemView.setOnClickListener {
                clickListener(matchItem)
            }
        }

        private fun formatValueData(valueData : String?) : String{
            return if(valueData != null && valueData.trim().isNotEmpty()){
                valueData
            } else {
                valueNone
            }
        }

        private fun formatValue(stringValue : String?) : String {
            return if(stringValue != null && stringValue.trim().isNotEmpty()) {
                stringValue
            } else {
                valueUnknown
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
                dateUnknown
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
                timeUnknown
            }
        }

        private fun convertDateTimeToLocalTimeZone(date: String, time: String): List<String> {

            lateinit var localTimeArray: List<String>

            if (date != dateUnknown &&
                time != timeUnknown
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


    }


}