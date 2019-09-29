package com.example.footballapps.view

import com.example.footballapps.model.LeagueDetailResponse

interface LeagueDetailView {
    fun dataIsLoading()
    fun dataLoadingFinished()
    fun dataFailedToLoad()
    fun showLeagueDetailInfo(leagueDetailResponse: LeagueDetailResponse)
}