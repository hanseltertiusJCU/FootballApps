package com.example.footballapps.model

data class SeasonOption(val season : String, val seasonString : String) {

    override fun toString() : String {
        return seasonString
    }

    override fun equals(other: Any?): Boolean {

        if (other is SeasonOption) {
            if (other.seasonString == seasonString && other.season == season) return true
        }

        return false
    }

    override fun hashCode(): Int {
        var result = season.hashCode()
        result = 31 * result + seasonString.hashCode()
        return result
    }
}