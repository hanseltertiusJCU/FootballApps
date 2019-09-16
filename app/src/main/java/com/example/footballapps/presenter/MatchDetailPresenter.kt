package com.example.footballapps.presenter

import android.util.Log
import com.example.footballapps.callback.MatchDetailRepositoryCallback
import com.example.footballapps.client.RetrofitClient
import com.example.footballapps.model.CombinedMatchTeamsResponse
import com.example.footballapps.model.MatchResponse
import com.example.footballapps.model.TeamResponse
import com.example.footballapps.repository.MatchDetailRepository
import com.example.footballapps.service.MatchService
import com.example.footballapps.service.TeamService
import com.example.footballapps.view.MatchDetailView
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers

class MatchDetailPresenter(private val matchDetailView: MatchDetailView, private val matchDetailRepository: MatchDetailRepository) {

//    companion object {
//        val noDataText = FootballApps.res.getString(R.string.no_data_to_show)
//        val noConnectionText = FootballApps.res.getString(R.string.no_internet_connection)
//        val failedToRetrieveText = FootballApps.res.getString(R.string.failed_to_retrieve_data)
//    }

    val noDataText = "No data to show"

    fun getDetailMatchInfo(eventId: String, homeTeamId: String, awayTeamId: String) {
        matchDetailView.dataIsLoading()

        matchDetailRepository.getMatchDetail(eventId, homeTeamId, awayTeamId, object : MatchDetailRepositoryCallback<CombinedMatchTeamsResponse?> {
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