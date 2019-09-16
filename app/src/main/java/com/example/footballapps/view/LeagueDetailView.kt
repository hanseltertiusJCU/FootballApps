package com.example.footballapps.view

import com.example.footballapps.model.LeagueDetailResponse
import com.example.footballapps.model.LeagueItem

interface LeagueDetailView {
    fun dataIsLoading()
    fun dataLoadingFinished()
    fun dataFailedToLoad()
    fun showLeagueDetailTitle(leagueItem: LeagueItem)
    fun showLeagueDetailInfo(leagueDetailResponse: LeagueDetailResponse)
}