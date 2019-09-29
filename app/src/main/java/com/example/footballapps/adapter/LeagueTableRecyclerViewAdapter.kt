package com.example.footballapps.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.footballapps.R
import com.example.footballapps.application.FootballApps
import com.example.footballapps.model.TeamInTableItem
import kotlinx.android.synthetic.main.item_team_in_league_table.view.*

class LeagueTableRecyclerViewAdapter(
    private val context: Context,
    private val teamsInLeagueTable: List<TeamInTableItem>
) : RecyclerView.Adapter<LeagueTableRecyclerViewAdapter.LeagueTableViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeagueTableViewHolder {
        return LeagueTableViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_team_in_league_table,
                parent,
                false
            )
        )
    }


    override fun getItemCount(): Int = teamsInLeagueTable.size

    override fun onBindViewHolder(holder: LeagueTableViewHolder, position: Int) {
        holder.bindItem(teamsInLeagueTable[position], position)
    }

    class LeagueTableViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        companion object {
            val valueNone = FootballApps.res.getString(R.string.value_none)
        }

        fun bindItem(teamInLeagueTable: TeamInTableItem, position: Int) {
            val teamPosition = position + 1

            itemView.tv_team_position.text = teamPosition.toString()
            itemView.tv_team_name.text = formatValueData(teamInLeagueTable.teamInTableName)
            itemView.tv_team_game_played.text =
                formatValueData(teamInLeagueTable.teamInTableGamesPlayed)
            itemView.tv_team_game_won.text = formatValueData(teamInLeagueTable.teamInTableGamesWon)
            itemView.tv_team_game_tied.text =
                formatValueData(teamInLeagueTable.teamInTableGamesTied)
            itemView.tv_team_game_lost.text =
                formatValueData(teamInLeagueTable.teamInTableGamesLost)
            itemView.tv_team_goals_scored.text =
                formatValueData(teamInLeagueTable.teamInTableGoalsScored)
            itemView.tv_team_goals_conceded.text =
                formatValueData(teamInLeagueTable.teamInTableGoalsConceded)
            itemView.tv_team_goal_difference.text =
                formatValueData(teamInLeagueTable.teamInTableGoalsDifference)
            itemView.tv_team_points.text = formatValueData(teamInLeagueTable.teamInTablePoints)
        }

        private fun formatValueData(valueData: String?): String {
            return if (valueData != null && valueData.trim().isNotEmpty()) {
                valueData
            } else {
                valueNone
            }
        }
    }


}