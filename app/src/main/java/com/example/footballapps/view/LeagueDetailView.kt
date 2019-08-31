package com.example.footballapps.view

import com.example.footballapps.model.LeagueDetailItem
import com.example.footballapps.model.LeagueItem

interface LeagueDetailView {
    fun dataIsLoading()
    fun dataLoadingFinished()
    fun dataFailedToLoad()
    fun showLeagueDetailTitle(leagueItem : LeagueItem)
    fun showLeagueDetailInfo(leagueDetailItem : LeagueDetailItem)
}