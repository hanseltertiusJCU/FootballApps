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
        val call: Call<MatchResponse> = matchService?.getNextMatchResponse(leagueId)!!

        call.enqueue(object : Callback<MatchResponse> {

            override fun onResponse(call: Call<MatchResponse>, response: Response<MatchResponse>) {
                if (response.isSuccessful) {

                    val data = response.body()

                    val matches = data?.events

                    matchView.showMatchData(matches!!)

                    matchView.dataLoadingFinished()
                }
            }

            override fun onFailure(call: Call<MatchResponse>, error: Throwable) {

                matchView.dataLoadingFinished()

                Log.e("errorTag", "Error : ${error.message}")
            }

        })
    }

    fun getPreviousMatchInfo(leagueId: String) {
        matchView.dataIsLoading()

        val retrofitClient = RetrofitClient()
        val retrofit = retrofitClient.getClient()
        val matchService = retrofit?.create(MatchService::class.java)
        val call: Call<MatchResponse> = matchService?.getPastMatchResponse(leagueId)!!

        call.enqueue(object : Callback<MatchResponse> {

            override fun onResponse(call: Call<MatchResponse>, response: Response<MatchResponse>) {
                if (response.isSuccessful) {

                    val data = response.body()

                    val matches = data?.events

                    matchView.showMatchData(matches!!)

                    matchView.dataLoadingFinished()
                }
            }

            override fun onFailure(call: Call<MatchResponse>, error: Throwable) {

                matchView.dataLoadingFinished()

                Log.e("errorTag", "Error : ${error.message}")
            }

        })
    }

    fun getSearchMatchInfo(query: String) {
        matchView.dataIsLoading()

        val retrofitClient = RetrofitClient()
        val retrofit = retrofitClient.getClient()
        val matchService = retrofit?.create(MatchService::class.java)
        val call: Call<MatchResponse> = matchService?.getSearchMatchResponse(query)!!

        call.enqueue(object : Callback<MatchResponse> {

            override fun onResponse(call: Call<MatchResponse>, response: Response<MatchResponse>) {
                if (response.isSuccessful) {

                    val data = response.body()

                    val matches = data?.searchResultEvents

                    val filteredMatches = mutableListOf<MatchItem>()

                    if (matches != null) {
                        for (match in matches) {
                            if (match.sportType.equals("Soccer")) {
                                filteredMatches.add(match)
                            }
                        }
                    }

                    matchView.showMatchData(filteredMatches)

                    matchView.dataLoadingFinished()
                }
            }

            override fun onFailure(call: Call<MatchResponse>, error: Throwable) {

                matchView.dataLoadingFinished()

                Log.e("errorTag", "Error : ${error.message}")
            }

        })
    }

    fun getDetailMatchInfo(eventId: String, homeTeamId: String, awayTeamId: String) {
        matchView.dataIsLoading()

        val retrofitClient = RetrofitClient()
        val retrofit = retrofitClient.getClient()
        val matchService = retrofit?.create(MatchService::class.java)
        val teamService = retrofit?.create(TeamService::class.java)
        // todo: ganti jadi observable, intinya bikin 3 observable
        val matchResponseObservable: Observable<MatchResponse> = matchService
            ?.getDetailMatchResponse(eventId)!!
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())

        val homeTeamResponseObservable: Observable<TeamResponse> = teamService
            ?.getTeamResponse(homeTeamId)!!
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())

        val awayTeamResponseObservable: Observable<TeamResponse> = teamService
            .getTeamResponse(awayTeamId)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())

        val combinedObservable: Observable<List<Any>> = Observable.zip(matchResponseObservable, homeTeamResponseObservable, awayTeamResponseObservable, object : Function3<MatchResponse, TeamResponse, TeamResponse, List<Any>>{
            override fun apply(matchResponse: MatchResponse, homeTeamResponse: TeamResponse, awayTeamResponse: TeamResponse): List<Any> {
                val combinedResponseList = mutableListOf<Any>()
                combinedResponseList.add(matchResponse)
                combinedResponseList.add(homeTeamResponse)
                combinedResponseList.add(awayTeamResponse)
                return combinedResponseList
            }

        })

        combinedObservable.subscribe(object : Observer<List<Any>> {
            override fun onComplete() {}

            override fun onSubscribe(disposable: Disposable) {}

            override fun onNext(combinedResponseList: List<Any>) {
                // todo: do the thing
            }

            override fun onError(error: Throwable) {
                // todo: bisa di terapin juga ya yg on error itu, tinggal pake no internet connection or smth
                Log.e("matchDetailError", error.message!!)
            }

        })


//        val call: Call<MatchResponse> = matchService?.getDetailMatchResponse(eventId)!!
//
//        call.enqueue(object : Callback<MatchResponse> {
//
//            override fun onResponse(call: Call<MatchResponse>, response: Response<MatchResponse>) {
//                if (response.isSuccessful) {
//
//                    val data = response.body()
//
//                    val matches = data?.events
//
//                    matchView.showMatchData(matches!!)
//
//                    matchView.dataLoadingFinished()
//                }
//            }
//
//            override fun onFailure(call: Call<MatchResponse>, error: Throwable) {
//
//                matchView.dataLoadingFinished()
//
//                Log.e("errorTag", "Error : ${error.message}")
//            }
//
//        })
    }

    // todo: tinggal pake home and away team badge function thing
}