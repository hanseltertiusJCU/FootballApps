package com.example.footballapps.model

data class CombinedMatchTeamsResponse(
    val matchDetailResponse : MatchResponse,
    val homeTeamResponse : TeamResponse,
    val awayTeamResponse : TeamResponse
)