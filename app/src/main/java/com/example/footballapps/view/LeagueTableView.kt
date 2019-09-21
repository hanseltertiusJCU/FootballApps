package com.example.footballapps.view

import com.example.footballapps.model.LeagueTableResponse

interface LeagueTableView {
    fun dataIsLoading()
    fun dataLoadingFinished()
    fun dataFailedToLoad()
    fun showLeagueTable(leagueTableResponse: LeagueTableResponse)
}