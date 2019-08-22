package com.example.footballapps.model

import com.google.gson.annotations.SerializedName

data class LeagueDetailInfo(
    @SerializedName("strLeague")
    var leagueName : String,
    @SerializedName("strDescriptionEN")
    var leagueDescription : String,
    @SerializedName("strBadge")
    var leagueBadge : String
)