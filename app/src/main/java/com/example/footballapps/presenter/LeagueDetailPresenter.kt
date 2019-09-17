package com.example.footballapps.presenter

import com.example.footballapps.callback.LeagueDetailRepositoryCallback
import com.example.footballapps.model.LeagueDetailResponse
import com.example.footballapps.repository.LeagueDetailRepository
import com.example.footballapps.view.LeagueDetailView

class LeagueDetailPresenter(
    private val leagueDetailView: LeagueDetailView,
    private val leagueDetailRepository: LeagueDetailRepository
) {

    fun getLeagueDetailInfo(leagueId: String) {

        leagueDetailView.dataIsLoading()

        leagueDetailRepository.getLeagueDetail(
            leagueId,
            object : LeagueDetailRepositoryCallback<LeagueDetailResponse?> {
                override fun onDataLoaded(data: LeagueDetailResponse?) {

                    leagueDetailView.showLeagueDetailInfo(data!!)

                    leagueDetailView.dataLoadingFinished()
                }

                override fun onDataError() {
                    leagueDetailView.dataFailedToLoad()
                }

            })
    }
}