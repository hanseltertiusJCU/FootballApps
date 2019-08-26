package com.example.footballapps.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.footballapps.R
import com.example.footballapps.model.MatchItem
import kotlinx.android.synthetic.main.item_match_data.view.*
import java.text.SimpleDateFormat
import java.util.*

// todo: bikin class viewholder
class MatchRecyclerViewAdapter(private val context : Context, private val matchList : List<MatchItem>) : RecyclerView.Adapter<MatchRecyclerViewAdapter.MatchViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder =
        MatchViewHolder(LayoutInflater.from(context).inflate(R.layout.item_match_data, parent, false))

    override fun getItemCount(): Int = matchList.size

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        holder.bindItem(matchList[position])
    }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    class MatchViewHolder(view : View) : RecyclerView.ViewHolder(view) {

        // todo: bind item
        @SuppressLint("SimpleDateFormat")
        fun bindItem(matchItem : MatchItem) {
            itemView.league_item_name.text = matchItem.leagueName
            itemView.league_item_match_week.text = matchItem.leagueMatchWeek

            val oldDateFormat = SimpleDateFormat("yyyy-MM-dd")
            val newDateFormat = SimpleDateFormat("dd MMM yyyy")
            val date : Date = oldDateFormat.parse(matchItem.dateEvent)
            val formattedDateEvent = newDateFormat.format(date)

            itemView.league_item_event_date.text = formattedDateEvent

            // todo: time event
//            val timeFormat = SimpleDateFormat("HH:mm:ssZZZZZ")
//            timeFormat.timeZone = TimeZone.getTimeZone("UTC")
//            val timeInDate : Date = timeFormat.parse(matchItem.timeEvent)
//            timeFormat.timeZone = TimeZone.getDefault()
//            val formattedTimeEvent = timeFormat.format(timeInDate)

            itemView.league_item_event_time.text = matchItem.timeEvent // todo: modify this into gmt + 7 / local time

            itemView.league_item_home_team_name.text = matchItem.homeTeamName

            itemView.league_item_home_team_score.text = matchItem.homeTeamScore
            itemView.league_item_away_team_score.text = matchItem.awayTeamScore

            itemView.league_item_away_team_name.text = matchItem.awayTeamName
        }
    }
}