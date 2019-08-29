package com.example.footballapps.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.footballapps.R
import com.example.footballapps.model.MatchItem
import kotlinx.android.synthetic.main.item_match_data.view.*
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MatchRecyclerViewAdapter(private val context : Context, private val matchList : List<MatchItem>, private val clickListener : (MatchItem) -> Unit) :
    RecyclerView.Adapter<MatchRecyclerViewAdapter.MatchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder =
        MatchViewHolder(LayoutInflater.from(context).inflate(R.layout.item_match_data, parent, false))

    override fun getItemCount(): Int = matchList.size

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        holder.bindItem(matchList[position], clickListener)
    }

    @SuppressLint("SimpleDateFormat")
    class MatchViewHolder(view : View) : RecyclerView.ViewHolder(view) {

        fun bindItem(matchItem : MatchItem, clickListener : (MatchItem) -> Unit) {
            itemView.league_item_name.text = matchItem.leagueName ?: Resources.getSystem().getString(R.string.league_unknown)
            itemView.league_item_match_week.text = when {
                matchItem.leagueMatchWeek != null -> StringBuilder("Week ${matchItem.leagueMatchWeek}")
                else -> Resources.getSystem().getString(R.string.match_week_unknown)
            }

            itemView.league_item_event_date.text = formatDate(matchItem.dateEvent)

            itemView.league_item_event_time.text = formatTime(matchItem.timeEvent)

            itemView.league_item_home_team_name.text = matchItem.homeTeamName ?: Resources.getSystem().getString(R.string.home_team_unknown)

            itemView.league_item_home_team_score.text = matchItem.homeTeamScore ?: "-"

            itemView.league_item_away_team_score.text = matchItem.awayTeamScore ?: "-"

            itemView.league_item_away_team_name.text = matchItem.awayTeamName ?: Resources.getSystem().getString(R.string.away_team_unknown)

            itemView.setOnClickListener {
                clickListener(matchItem)
            }
        }

        // todo: mesti di gabungin string date sm time, trus di format abis itu d split

        private fun formatDate(stringValue : String?) : String {
            return if(stringValue != null && stringValue.isNotEmpty()) {
                val oldDateFormat = SimpleDateFormat("yyyy-MM-dd")
                val newDateFormat = SimpleDateFormat("dd MMM yyyy")
                val date : Date = oldDateFormat.parse(stringValue)
                val formattedDateEvent = newDateFormat.format(date)

                formattedDateEvent
            } else {
                Resources.getSystem().getString(R.string.date_unknown)
            }
        }

        private fun formatTime(stringValue: String?) : String {
            return if(stringValue != null && stringValue.isNotEmpty()) {
                val timeFormat = SimpleDateFormat("HH:mm")
                timeFormat.timeZone = TimeZone.getTimeZone("UTC")
                val timeInDate : Date = timeFormat.parse(stringValue)
                timeFormat.timeZone = TimeZone.getDefault()
                val formattedTimeEvent = timeFormat.format(timeInDate)

                formattedTimeEvent
            } else {
                Resources.getSystem().getString(R.string.time_unknown)
            }
        }

        // todo: sebenarnya bisa d bikin date and time abis itu di split into 2 parts, date and time agar bisa di mainin d text value thing
    }


}