package com.example.footballapps.model

import com.google.gson.annotations.SerializedName

data class LeagueTableResponse(
    @SerializedName("table")
    var leagueTable: List<TeamInTableItem>? = emptyList()
)

data class TeamInTableItem(
    @SerializedName("name")
    var teamInTableName: String?,
    @SerializedName("teamid")
    var teamInTableId: String?,
    @SerializedName("played")
    var teamInTableGamesPlayed: String?,
    @SerializedName("goalsfor")
    var teamInTableGoalsScored: String?,
    @SerializedName("goalsagainst")
    var teamInTableGoalsConceded: String?,
    @SerializedName("goalsdifference")
    var teamInTableGoalsDifference: String?,
    @SerializedName("win")
    var teamInTableGamesWon: String?,
    @SerializedName("draw")
    var teamInTableGamesTied: String?,
    @SerializedName("loss")
    var teamInTableGamesLost: String?,
    @SerializedName("total")
    var teamInTablePoints: String?
)