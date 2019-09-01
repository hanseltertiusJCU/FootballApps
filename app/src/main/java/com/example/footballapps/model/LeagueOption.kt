package com.example.footballapps.model


data class LeagueOption(val leagueId: String, val leagueName: String) {
    override fun toString(): String {
        return leagueName
    }

    override fun equals(other: Any?): Boolean {

        if (other is LeagueOption) {
            if (other.leagueName == leagueName && other.leagueId == leagueId) return true
        }

        return false
    }

    override fun hashCode(): Int {
        var result = leagueId.hashCode()
        result = 31 * result + leagueName.hashCode()
        return result
    }
}