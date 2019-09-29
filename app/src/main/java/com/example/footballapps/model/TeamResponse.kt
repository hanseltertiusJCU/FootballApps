package com.example.footballapps.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class TeamResponse(
    @SerializedName("teams")
    var teams: List<TeamItem>? = emptyList()
)

@Parcelize
data class TeamItem(
    @SerializedName("idTeam")
    var teamId: String?,
    @SerializedName("strTeam")
    var teamName: String?,
    @SerializedName("intFormedYear")
    var teamFormedYear: String?,
    @SerializedName("strLeague")
    var teamLeague: String?,
    @SerializedName("strStadium")
    var teamStadium: String?,
    @SerializedName("strStadiumLocation")
    var teamStadiumLocation: String?,
    @SerializedName("intStadiumCapacity")
    var teamStadiumCapacity: String?,
    @SerializedName("strStadiumDescription")
    var teamStadiumDescription: String?,
    @SerializedName("strDescriptionEN")
    var teamDesc: String?,
    @SerializedName("strCountry")
    var teamCountry: String?,
    @SerializedName("strTeamBadge")
    var teamBadge: String?,
    @SerializedName("strSport")
    var sportType: String?,
    @SerializedName("strTeamJersey")
    var teamJersey: String?
) : Parcelable