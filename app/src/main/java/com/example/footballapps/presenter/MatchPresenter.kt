package com.example.footballapps.presenter

import android.util.Log
import com.example.footballapps.client.RetrofitClient
import com.example.footballapps.model.MatchItem
import com.example.footballapps.model.MatchResponse
import com.example.footballapps.model.TeamResponse
import com.example.footballapps.service.MatchService
import com.example.footballapps.service.TeamService
import com.example.footballapps.view.MatchView
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MatchPresenter(private val matchView: MatchView) {

    fun getNextMatchInfo(leagueId: String) {
        matchView.dataIsLoading()

        val retrofitClient = RetrofitClient()
        val retrofit = retrofitClient.getClient()
        val matchService = retrofit?.create(MatchService::class.java)

        val nextMatchResponseObservable : Observable<MatchResponse>? = matchService
            ?.getNextMatchResponse(leagueId)
            ?.subscribeOn(Schedulers.newThread())
            ?.observeOn(AndroidSchedulers.mainThread())

        nextMatchResponseObservable?.subscribe(object : Observer<MatchResponse> {
            override fun onComplete() {}

            override fun onSubscribe(disposable: Disposable) {}

            override fun onNext(matchResponse: MatchResponse) {
                val nextMatches = matchResponse.events

                if(nextMatches != null){
                    matchView.showMatchData(nextMatches)

                    matchView.dataLoadingFinished()
                } else {
                    matchView.dataFailedToLoad()
                }


            }

            override fun onError(error: Throwable) {
                Log.d("nextMatchError", error.message!!)

                matchView.dataFailedToLoad()
            }

        })
    }

    fun getPreviousMatchInfo(leagueId: String) {
        matchView.dataIsLoading()

        val retrofitClient = RetrofitClient()
        val retrofit = retrofitClient.getClient()
        val matchService = retrofit?.create(MatchService::class.java)

        val previousMatchResponseObservable : Observable<MatchResponse>? = matchService
            ?.getPastMatchResponse(leagueId)
            ?.subscribeOn(Schedulers.newThread())
            ?.observeOn(AndroidSchedulers.mainThread())

        previousMatchResponseObservable?.subscribe(object : Observer<MatchResponse>{
            override fun onComplete() {}

            override fun onSubscribe(disposable: Disposable) {}

            override fun onNext(matchResponse: MatchResponse) {
                val previousMatches = matchResponse.events

                if(previousMatches != null){
                    matchView.showMatchData(previousMatches)

                    matchView.dataLoadingFinished()
                } else {
                    matchView.dataFailedToLoad()
                }

                matchView.dataLoadingFinished()
            }

            override fun onError(error: Throwable) {
                Log.d("previousMatchError", error.message!!)

                matchView.dataFailedToLoad()
            }

        })
    }

    fun getSearchMatchInfo(query: String) {
        matchView.dataIsLoading()

        val retrofitClient = RetrofitClient()
        val retrofit = retrofitClient.getClient()
        val matchService = retrofit?.create(MatchService::class.java)

        val searchMatchResponseObservable : Observable<MatchResponse>? = matchService
            ?.getSearchMatchResponse(query)
            ?.subscribeOn(Schedulers.newThread())
            ?.observeOn(AndroidSchedulers.mainThread())

        searchMatchResponseObservable?.subscribe(object : Observer<MatchResponse> {
            override fun onComplete() {}

            override fun onSubscribe(disposable: Disposable) {}

            override fun onNext(matchResponse: MatchResponse) {

                val searchResultMatches = matchResponse.searchResultEvents

                val filteredMatches = mutableListOf<MatchItem>()

                if(searchResultMatches != null){
                    for (match in searchResultMatches) {
                        if (match.sportType.equals("Soccer")) {
                            filteredMatches.add(match)
                        }
                    }
                }

                matchView.showMatchData(filteredMatches)

                matchView.dataLoadingFinished()
            }

            override fun onError(error: Throwable) {
                Log.d("matchDetailError", error.message!!)

                matchView.dataFailedToLoad()
            }

        })
    }
}