package com.example.footballapps.presenter

import android.util.Log
import com.example.footballapps.callback.MatchesRepositoryCallback
import com.example.footballapps.client.RetrofitClient
import com.example.footballapps.model.MatchItem
import com.example.footballapps.model.MatchResponse
import com.example.footballapps.repository.MatchesRepository
import com.example.footballapps.service.MatchService
import com.example.footballapps.view.MatchView

class MatchPresenter(private val matchView: MatchView, private val matchesRepository : MatchesRepository) {

    fun getNextMatchInfo(leagueId: String) {
        matchView.dataIsLoading()

        matchesRepository.getNextMatches(leagueId, object : MatchesRepositoryCallback<MatchResponse?> {
            override fun onDataLoaded(data: MatchResponse?) {
                matchView.showMatchesData(data!!)
                matchView.dataLoadingFinished()
            }

            override fun onDataError() {
                matchView.dataFailedToLoad()
            }

        })

    }

    fun getPreviousMatchInfo(leagueId: String) {
        matchView.dataIsLoading()

        matchesRepository.getLastMatches(leagueId, object : MatchesRepositoryCallback<MatchResponse?> {
            override fun onDataLoaded(data: MatchResponse?) {
                matchView.showMatchesData(data!!)
                matchView.dataLoadingFinished()
            }

            override fun onDataError() {
                matchView.dataFailedToLoad()
            }

        })
    }

    fun getSearchMatchInfo(query: String) {
        matchView.dataIsLoading()

        matchesRepository.getSearchResultMatches(query, object : MatchesRepositoryCallback<MatchResponse?> {
            override fun onDataLoaded(data: MatchResponse?) {

                matchView.showMatchesData(data!!)
                matchView.dataLoadingFinished()
            }

            override fun onDataError() {
                matchView.dataFailedToLoad()
            }

        })
    }
}