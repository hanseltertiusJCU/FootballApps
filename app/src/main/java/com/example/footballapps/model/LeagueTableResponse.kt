package com.example.footballapps.model

import com.google.gson.annotations.SerializedName

/*
{
    "table": [
        {
            "name": "Perth Glory",
            "teamid": "134481",
            "played": 28,
            "goalsfor": 58,
            "goalsagainst": 25,
            "goalsdifference": 33,
            "win": 18,
            "draw": 7,
            "loss": 3,
            "total": 61
        }
    ]
}
*/

data class LeagueTableResponse(
    @SerializedName("table")
    var leagueTable : List<TeamInTableItem>? = emptyList()
)

data class TeamInTableItem(
    @SerializedName("name")
    var teamInTableName : String?,
    @SerializedName("teamid")
    var teamInTableId : String?,
    @SerializedName("played")
    var teamInTableGamesPlayed : String?,
    @SerializedName("goalsfor")
    var teamInTableGoalsScored : String?,
    @SerializedName("goalsagainst")
    var teamInTableGoalsConceded : String?,
    @SerializedName("goalsdifference")
    var teamInTableGoalsDifference : String?,
    @SerializedName("win")
    var teamInTableGamesWon : String?,
    @SerializedName("draw")
    var teamInTableGamesTied : String?,
    @SerializedName("loss")
    var teamInTableGamesLost : String?,
    @SerializedName("total")
    var teamInTablePoints : String?
)