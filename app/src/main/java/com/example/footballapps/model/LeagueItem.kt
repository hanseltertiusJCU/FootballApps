package com.example.footballapps.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LeagueItem (val leagueName : String?, val leagueDesc : String?, val leagueImage : Int?) : Parcelable