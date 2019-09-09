package com.example.footballapps.favorite

data class FavoriteMatch(val id : Long, val idEvent : String, val leagueName : String, val dateEvent : String, val leagueMatchWeek : String, val timeEvent : String, val homeTeamName : String, val awayTeamName : String, val homeTeamScore : String, val awayTeamScore : String){
    companion object {
        const val TABLE_FAVORITE_MATCH : String = "TABLE_FAVORITE_MATCH"
        const val ID : String = "ID_"
        const val EVENT_ID : String = "EVENT_ID"
        const val LEAGUE_NAME : String = "LEAGUE_NAME"
        const val LEAGUE_MATCH_WEEK : String = "LEAGUE_MATCH_WEEK"
        const val EVENT_DATE : String = "EVENT_DATE"
        const val EVENT_TIME : String = "EVENT_TIME"
        const val HOME_TEAM_NAME : String = "HOME_TEAM_NAME"
        const val AWAY_TEAM_NAME : String = "AWAY_TEAM_NAME"
        const val HOME_TEAM_SCORE : String = "HOME_TEAM_SCORE"
        const val AWAY_TEAM_SCORE : String = "AWAY_TEAM_SCORE"
    }
}