package com.example.footballapps.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.footballapps.R
import com.example.footballapps.application.FootballApps
import com.example.footballapps.favorite.FavoriteMatchItem
import kotlinx.android.synthetic.main.item_match_data.view.*
import java.text.SimpleDateFormat
import java.util.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class FavoriteMatchRecyclerViewAdapter(
    private val context: Context,
    private val favoriteMatchItemList: List<FavoriteMatchItem>,
    private val clickListener: (FavoriteMatchItem) -> Unit
) : RecyclerView.Adapter<FavoriteMatchRecyclerViewAdapter.FavoriteMatchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteMatchViewHolder =
        FavoriteMatchViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_match_data, parent, false)
        )


    override fun getItemCount(): Int = favoriteMatchItemList.size

    override fun onBindViewHolder(holder: FavoriteMatchViewHolder, position: Int) {
        holder.bindItem(favoriteMatchItemList[position], clickListener)
    }

    @SuppressLint("SimpleDateFormat")
    class FavoriteMatchViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        companion object {
            val dateUnknown = FootballApps.res.getString(R.string.date_unknown)
            val timeUnknown = FootballApps.res.getString(R.string.time_unknown)
            val valueUnknown = FootballApps.res.getString(R.string.value_unknown)
            val valueNone = FootballApps.res.getString(R.string.value_none)
        }

        fun bindItem(
            favoriteMatchItem: FavoriteMatchItem,
            clickListener: (FavoriteMatchItem) -> Unit
        ) {
            itemView.league_item_name.text = favoriteMatchItem.leagueName ?: StringBuilder(
                "League ${formatValue(favoriteMatchItem.leagueName)}"
            )
            itemView.league_item_match_week.text = StringBuilder("Week ${formatValue(favoriteMatchItem.leagueMatchWeek)}")

            val arrayLocalTimeDt = convertDateTimeToLocalTimeZone(formatDate(favoriteMatchItem.dateEvent), formatTime(favoriteMatchItem.timeEvent))

            itemView.league_item_event_date.text = arrayLocalTimeDt[0]
            itemView.league_item_event_time.text = arrayLocalTimeDt[1]

            itemView.league_item_home_team_name.text = formatValue(favoriteMatchItem.homeTeamName)

            itemView.league_item_home_team_score.text = formatValueData(favoriteMatchItem.homeTeamScore)

            itemView.league_item_away_team_score.text = formatValueData(favoriteMatchItem.awayTeamScore)

            itemView.league_item_away_team_name.text = formatValue(favoriteMatchItem.awayTeamName)

            itemView.setOnClickListener {
                clickListener(favoriteMatchItem)
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

        @SuppressLint("SimpleDateFormat")
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

        @SuppressLint("SimpleDateFormat")
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