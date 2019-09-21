package com.example.footballapps.presenter

import com.example.footballapps.callback.TeamDetailRepositoryCallback
import com.example.footballapps.model.TeamResponse
import com.example.footballapps.repository.TeamDetailRepository
import com.example.footballapps.view.TeamDetailView

class TeamDetailPresenter(private val teamDetailView: TeamDetailView, private val teamDetailRepository: TeamDetailRepository) {
    fun getTeamDetailInfo(teamId : String) {
        teamDetailView.dataIsLoading()

        teamDetailRepository.getTeamDetail(teamId, object :
            TeamDetailRepositoryCallback<TeamResponse?> {
            override fun onDataLoaded(data: TeamResponse?) {
                teamDetailView.showTeamDetailData(data!!)

                teamDetailView.dataLoadingFinished()
            }

            override fun onDataError() {
                teamDetailView.dataFailedToLoad()
            }

        })
    }
}