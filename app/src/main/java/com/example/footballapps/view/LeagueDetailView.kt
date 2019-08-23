package com.example.footballapps.view

import com.example.footballapps.model.LeagueDetailItem
import com.example.footballapps.model.LeagueItem

interface LeagueDetailView {
    fun dataIsLoading()
    fun dataLoadingFinished()
    fun showLeagueDetailTitle(leagueItem : LeagueItem)
    fun showLeagueDetailInfo(leagueDetailItemList : List<LeagueDetailItem>)
}