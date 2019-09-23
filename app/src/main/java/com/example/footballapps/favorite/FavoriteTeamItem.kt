package com.example.footballapps.favorite

data class FavoriteTeamItem(
    val id: Long,
    val idTeam: String?,
    val teamBadgeUrl: String?,
    val teamName: String?
) {
    companion object {
        const val TABLE_FAVORITE_TEAM: String = "TABLE_FAVORITE_TEAM"
        const val ID: String = "ID_"
        const val TEAM_ID: String = "TEAM_ID"
        const val TEAM_NAME: String = "TEAM_NAME"
        const val TEAM_BADGE_URL: String = "TEAM_BADGE_URL"
    }
}