package com.example.footballapps.presenter

import com.example.footballapps.callback.MatchDetailRepositoryCallback
import com.example.footballapps.model.CombinedMatchTeamsResponse
import com.example.footballapps.repository.MatchDetailRepository
import com.example.footballapps.view.MatchDetailView

class MatchDetailPresenter(
    private val matchDetailView: MatchDetailView,
    private val matchDetailRepository: MatchDetailRepository
) {

    fun getDetailMatchInfo(eventId: String, homeTeamId: String, awayTeamId: String) {
        matchDetailView.dataIsLoading()

        matchDetailRepository.getMatchDetail(
            eventId,
            homeTeamId,
            awayTeamId,
            object : MatchDetailRepositoryCallback<CombinedMatchTeamsResponse?> {
                override fun onDataLoaded(data: CombinedMatchTeamsResponse?) {
                    matchDetailView.showMatchDetailData(data!!)

                    matchDetailView.dataLoadingFinished()
                }

                override fun onDataError() {
                    matchDetailView.dataFailedToLoad()
                }

            })
    }
}