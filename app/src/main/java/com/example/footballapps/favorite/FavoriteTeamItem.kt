package com.example.footballapps.favorite

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FavoriteTeamItem(
    val id: Long,
    val idTeam: String?,
    val teamName: String?,
    val teamBadgeUrl: String?,
    val teamFormedYear: String?,
    val teamCountry: String?
) : Parcelable {
    companion object {
        const val TABLE_FAVORITE_TEAM : String = "TABLE_FAVORITE_TEAM"
        const val ID : String = "ID_"
        const val TEAM_ID : String = "TEAM_ID"
        const val TEAM_NAME : String = "TEAM_NAME"
        const val TEAM_BADGE_URL : String = "TEAM_BADGE_URL"
        const val TEAM_FORMED_YEAR : String = "TEAM_FORMED_YEAR"
        const val TEAM_COUNTRY : String = "TEAM_COUNTRY"
    }
}