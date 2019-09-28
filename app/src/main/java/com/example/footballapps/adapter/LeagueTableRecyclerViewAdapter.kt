package com.example.footballapps.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.footballapps.R
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

        // todo : itnggal modify aja

        fun bindItem(teamInLeagueTable: TeamInTableItem, position: Int) {
            val teamPosition = position + 1

            itemView.tv_team_position.text = teamPosition.toString()
            itemView.tv_team_name.text = teamInLeagueTable.teamInTableName ?: "-"
            itemView.tv_team_game_played.text = teamInLeagueTable.teamInTableGamesPlayed ?: "-"
            itemView.tv_team_game_won.text = teamInLeagueTable.teamInTableGamesWon ?: "-"
            itemView.tv_team_game_tied.text = teamInLeagueTable.teamInTableGamesTied ?: "-"
            itemView.tv_team_game_lost.text = teamInLeagueTable.teamInTableGamesLost ?: "-"
            itemView.tv_team_goals_scored.text = teamInLeagueTable.teamInTableGoalsScored ?: "-"
            itemView.tv_team_goals_conceded.text = teamInLeagueTable.teamInTableGoalsConceded ?: "-"
            itemView.tv_team_goal_difference.text = teamInLeagueTable.teamInTableGoalsDifference ?: "-"
            itemView.tv_team_points.text = teamInLeagueTable.teamInTablePoints ?: "-"
        }
    }


}