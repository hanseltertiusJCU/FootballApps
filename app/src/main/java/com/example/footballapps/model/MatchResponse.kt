package com.example.footballapps.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class MatchResponse(
    @SerializedName("events")
    var events: List<MatchItem>? = emptyList(),
    @SerializedName("event")
    var searchResultEvents: List<MatchItem>? = emptyList(),
    @SerializedName("results")
    var results: List<MatchItem>? = emptyList()
)

@Parcelize
data class MatchItem(
    @SerializedName("idEvent")
    var idEvent: String?,
    @SerializedName("strEvent")
    var strEvent: String?,
    @SerializedName("strLeague")
    var leagueName: String?,
    @SerializedName("intRound")
    var leagueMatchWeek: String?,
    @SerializedName("strHomeTeam")
    var homeTeamName: String?,
    @SerializedName("strAwayTeam")
    var awayTeamName: String?,
    @SerializedName("intHomeScore")
    var homeTeamScore: String?,
    @SerializedName("intAwayScore")
    var awayTeamScore: String?,
    @SerializedName("dateEvent")
    var dateEvent: String?,
    @SerializedName("strTime")
    var timeEvent: String?,
    @SerializedName("strSport")
    var sportType: String?,
    @SerializedName("intSpectators")
    var spectators: String?,
    @SerializedName("strHomeGoalDetails")
    var homeTeamGoalDetails: String?,
    @SerializedName("strAwayGoalDetails")
    var awayTeamGoalDetails: String?,
    @SerializedName("strHomeYellowCards")
    var homeTeamYellowCards: String?,
    @SerializedName("strHomeRedCards")
    var homeTeamRedCards: String?,
    @SerializedName("strAwayYellowCards")
    var awayTeamYellowCards: String?,
    @SerializedName("strAwayRedCards")
    var awayTeamRedCards: String?,
    @SerializedName("intHomeShots")
    var homeTeamShots: String?,
    @SerializedName("intAwayShots")
    var awayTeamShots: String?,
    @SerializedName("strHomeFormation")
    var homeTeamFormation: String?,
    @SerializedName("strHomeLineupGoalkeeper")
    var homeTeamGoalkeeper: String?,
    @SerializedName("strHomeLineupDefense")
    var homeTeamDefense: String?,
    @SerializedName("strHomeLineupMidfield")
    var homeTeamMidfield: String?,
    @SerializedName("strHomeLineupForward")
    var homeTeamForward: String?,
    @SerializedName("strHomeLineupSubstitutes")
    var homeTeamSubstitutes: String?,
    @SerializedName("strAwayFormation")
    var awayTeamFormation: String?,
    @SerializedName("strAwayLineupGoalkeeper")
    var awayTeamGoalkeeper: String?,
    @SerializedName("strAwayLineupDefense")
    var awayTeamDefense: String?,
    @SerializedName("strAwayLineupMidfield")
    var awayTeamMidfield: String?,
    @SerializedName("strAwayLineupForward")
    var awayTeamForward: String?,
    @SerializedName("strAwayLineupSubstitutes")
    var awayTeamSubstitutes: String?,
    @SerializedName("idHomeTeam")
    var homeTeamId: String?,
    @SerializedName("idAwayTeam")
    var awayTeamId: String?
) : Parcelable