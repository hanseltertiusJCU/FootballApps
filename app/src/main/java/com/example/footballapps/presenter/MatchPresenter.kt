package com.example.footballapps.presenter

import android.util.Log
import com.example.footballapps.R
import com.example.footballapps.application.FootballApps
import com.example.footballapps.client.RetrofitClient
import com.example.footballapps.model.MatchItem
import com.example.footballapps.model.MatchResponse
import com.example.footballapps.service.MatchService
import com.example.footballapps.view.MatchView
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class MatchPresenter(private val matchView: MatchView) {

    companion object {
        val noDataText = FootballApps.res.getString(R.string.no_data_to_show)
        val noConnectionText = FootballApps.res.getString(R.string.no_internet_connection)
        val failedToRetrieveText = FootballApps.res.getString(R.string.failed_to_retrieve_data)
    }

    fun getNextMatchInfo(leagueId: String) {
        matchView.dataIsLoading()

        val retrofitClient = RetrofitClient()
        val retrofit = retrofitClient.getClient()
        val matchService = retrofit?.create(MatchService::class.java)

        val nextMatchResponseObservable: Observable<MatchResponse>? = matchService
            ?.getNextMatchResponse(leagueId)
            ?.subscribeOn(Schedulers.newThread())
            ?.observeOn(AndroidSchedulers.mainThread())

        nextMatchResponseObservable?.subscribe(object : Observer<MatchResponse> {
            override fun onComplete() {}

            override fun onSubscribe(disposable: Disposable) {}

            override fun onNext(matchResponse: MatchResponse) {
                val nextMatches = matchResponse.events

                if (nextMatches != null) {
                    matchView.showMatchData(nextMatches)

                    matchView.dataLoadingFinished()
                } else {
                    matchView.dataFailedToLoad(noDataText)
                }


            }

            override fun onError(error: Throwable) {
                Log.d("nextMatchError", error.message!!)

                if (error.message!!.contains("Unable to resolve host")) {
                    matchView.dataFailedToLoad(noConnectionText)
                } else {
                    matchView.dataFailedToLoad(failedToRetrieveText)
                }
            }

        })
    }

    fun getPreviousMatchInfo(leagueId: String) {
        matchView.dataIsLoading()

        val retrofitClient = RetrofitClient()
        val retrofit = retrofitClient.getClient()
        val matchService = retrofit?.create(MatchService::class.java)

        val previousMatchResponseObservable: Observable<MatchResponse>? = matchService
            ?.getPastMatchResponse(leagueId)
            ?.subscribeOn(Schedulers.newThread())
            ?.observeOn(AndroidSchedulers.mainThread())

        previousMatchResponseObservable?.subscribe(object : Observer<MatchResponse> {
            override fun onComplete() {}

            override fun onSubscribe(disposable: Disposable) {}

            override fun onNext(matchResponse: MatchResponse) {
                val previousMatches = matchResponse.events

                if (previousMatches != null) {
                    matchView.showMatchData(previousMatches)

                    matchView.dataLoadingFinished()
                } else {
                    matchView.dataFailedToLoad(noDataText)
                }

            }

            override fun onError(error: Throwable) {
                Log.d("previousMatchError", error.message!!)

                if (error.message!!.contains("Unable to resolve host")) {
                    matchView.dataFailedToLoad(noConnectionText)
                } else {
                    matchView.dataFailedToLoad(failedToRetrieveText)
                }
            }

        })
    }

    fun getSearchMatchInfo(query: String) {
        matchView.dataIsLoading()

        val retrofitClient = RetrofitClient()
        val retrofit = retrofitClient.getClient()
        val matchService = retrofit?.create(MatchService::class.java)

        val searchMatchResponseObservable: Observable<MatchResponse>? = matchService
            ?.getSearchMatchResponse(query)
            ?.subscribeOn(Schedulers.newThread())
            ?.observeOn(AndroidSchedulers.mainThread())

        searchMatchResponseObservable?.subscribe(object : Observer<MatchResponse> {
            override fun onComplete() {}

            override fun onSubscribe(disposable: Disposable) {}

            override fun onNext(matchResponse: MatchResponse) {

                val searchResultMatches = matchResponse.searchResultEvents

                val filteredMatches = mutableListOf<MatchItem>()

                if (searchResultMatches != null) {
                    for (match in searchResultMatches) {
                        if (match.sportType.equals("Soccer")) {
                            filteredMatches.add(match)
                        }
                    }
                }

                if (filteredMatches.size > 0) {
                    matchView.showMatchData(filteredMatches)

                    matchView.dataLoadingFinished()
                } else {
                    matchView.dataFailedToLoad(noDataText)
                }

            }

            override fun onError(error: Throwable) {
                Log.d("matchDetailError", error.message!!)

                if (error.message!!.contains("Unable to resolve host")) {
                    matchView.dataFailedToLoad(noConnectionText)
                } else {
                    matchView.dataFailedToLoad(failedToRetrieveText)
                }
            }

        })
    }
}