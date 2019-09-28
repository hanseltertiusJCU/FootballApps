package com.example.footballapps.presenter

import com.example.footballapps.callback.LeagueTableRepositoryCallback
import com.example.footballapps.model.LeagueTableResponse
import com.example.footballapps.repository.LeagueTableRepository
import com.example.footballapps.view.LeagueTableView

class LeagueTablePresenter(
    private val leagueTableView : LeagueTableView,
    private val leagueTableRepository : LeagueTableRepository
) {
    fun getLeagueTableInfo(leagueId : String, leagueSeason : String){
        leagueTableView.dataIsLoading()

        // todo : tinggal modif

        leagueTableRepository.getLeagueTable(
            leagueId,
            leagueSeason,
            object : LeagueTableRepositoryCallback<LeagueTableResponse?> {
            override fun onDataLoaded(data: LeagueTableResponse?) {
                leagueTableView.showLeagueTable(data!!)

                leagueTableView.dataLoadingFinished()
            }

            override fun onDataError() {
                leagueTableView.dataFailedToLoad()
            }

        })
    }
}