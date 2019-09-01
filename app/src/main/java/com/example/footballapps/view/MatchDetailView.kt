package com.example.footballapps.view

import com.example.footballapps.model.MatchItem

interface MatchDetailView {
    fun dataIsLoading()
    fun dataLoadingFinished()
    fun dataFailedToLoad(errorText : String)
    fun showMatchData(matchItem : MatchItem)
    fun showHomeTeamBadge(homeTeamBadgeUrl : String?)
    fun showAwayTeamBadge(awayTeamBadgeUrl : String?)
}