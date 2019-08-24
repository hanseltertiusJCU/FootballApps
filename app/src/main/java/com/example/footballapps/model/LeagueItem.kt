package com.example.footballapps.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LeagueItem (val leagueId : String?, val leagueName : String?, val leagueImage : Int?) : Parcelable