package com.example.footballapps.model

import com.google.gson.annotations.SerializedName

data class LeagueDetailResponse(
    @SerializedName("leagues")
    var leagues: List<LeagueDetailItem>? = emptyList()
)

data class LeagueDetailItem(
    @SerializedName("strLeague")
    var leagueName: String?,
    @SerializedName("strDescriptionEN")
    var leagueDescription: String?,
    @SerializedName("strBadge")
    var leagueBadge: String?,
    @SerializedName("intFormedYear")
    var leagueFormedYear: String?,
    @SerializedName("strCountry")
    var leagueCountry: String?,
    @SerializedName("strTrophy")
    var leagueTrophy: String?
)