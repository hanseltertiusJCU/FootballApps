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
            val leagueNameUnknown = FootballApps.res.getString(R.string.league_unknown)
            val matchWeekUnknown = FootballApps.res.getString(R.string.match_week_unknown)
            val homeTeamNameUnknown = FootballApps.res.getString(R.string.home_team_unknown)
            val awayTeamNameUnknown = FootballApps.res.getString(R.string.away_team_unknown)
            val dateUnknown = FootballApps.res.getString(R.string.date_unknown)
            val timeUnknown = FootballApps.res.getString(R.string.time_unknown)
        }

        fun bindItem(
            favoriteMatchItem: FavoriteMatchItem,
            clickListener: (FavoriteMatchItem) -> Unit
        ) {
            itemView.league_item_name.text = favoriteMatchItem.leagueName ?: leagueNameUnknown
            itemView.league_item_match_week.text = when {
                favoriteMatchItem.leagueMatchWeek != null -> StringBuilder("Week ${favoriteMatchItem.leagueMatchWeek}")
                else -> matchWeekUnknown
            }

            itemView.league_item_event_date.text = favoriteMatchItem.dateEvent ?: dateUnknown
            itemView.league_item_event_time.text = favoriteMatchItem.timeEvent ?: timeUnknown

            itemView.league_item_home_team_name.text =
                favoriteMatchItem.homeTeamName ?: homeTeamNameUnknown

            itemView.league_item_home_team_score.text = favoriteMatchItem.homeTeamScore ?: "-"

            itemView.league_item_away_team_score.text = favoriteMatchItem.awayTeamScore ?: "-"

            itemView.league_item_away_team_name.text =
                favoriteMatchItem.awayTeamName ?: awayTeamNameUnknown

            itemView.setOnClickListener {
                clickListener(favoriteMatchItem)
            }
        }
    }
}