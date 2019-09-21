package com.example.footballapps.presenter

import com.example.footballapps.callback.TeamsRepositoryCallback
import com.example.footballapps.model.TeamResponse
import com.example.footballapps.repository.TeamsRepository
import com.example.footballapps.view.TeamsView

class TeamsPresenter(private val teamsView: TeamsView, private val teamsRepository: TeamsRepository) {
    fun getTeamsInfo(leagueId : String) {
        teamsView.dataIsLoading()

        teamsRepository.getTeams(leagueId, object : TeamsRepositoryCallback<TeamResponse?> {
            override fun onDataLoaded(data: TeamResponse?) {
                teamsView.showTeamsData(data!!)

                teamsView.dataLoadingFinished()
            }

            override fun onDataError() {
                teamsView.dataFailedToLoad()
            }

        })
    }

    fun getSearchTeamsInfo(query : String){
        teamsView.dataIsLoading()

        teamsRepository.getSearchResultTeams(query, object : TeamsRepositoryCallback<TeamResponse?> {
            override fun onDataLoaded(data: TeamResponse?) {
                teamsView.showTeamsData(data!!)
                teamsView.dataLoadingFinished()
            }

            override fun onDataError() {
                teamsView.dataFailedToLoad()
            }

        })
    }
}