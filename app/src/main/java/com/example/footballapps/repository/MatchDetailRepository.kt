package com.example.footballapps.repository

import com.example.footballapps.callback.MatchDetailRepositoryCallback
import com.example.footballapps.client.RetrofitClient
import com.example.footballapps.model.CombinedMatchTeamsResponse
import com.example.footballapps.model.MatchResponse
import com.example.footballapps.model.TeamResponse
import com.example.footballapps.service.MatchService
import com.example.footballapps.service.TeamService
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers

class MatchDetailRepository {

    fun getMatchDetail(eventId : String, homeTeamId : String, awayTeamId : String, callback: MatchDetailRepositoryCallback<CombinedMatchTeamsResponse?>) {
        val matchDetailResponseObservable : Observable<MatchResponse>? =
            RetrofitClient
                .createService(MatchService::class.java)
                .getMatchDetailResponse(eventId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())

        val homeTeamResponseObservable : Observable<TeamResponse>? =
            RetrofitClient
                .createService(TeamService::class.java)
                .getTeamDetailResponse(homeTeamId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
        val awayTeamResponseObservable : Observable<TeamResponse>? =
            RetrofitClient
                .createService(TeamService::class.java)
                .getTeamDetailResponse(awayTeamId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())

        Observable.zip(
            matchDetailResponseObservable,
            homeTeamResponseObservable,
            awayTeamResponseObservable,
            Function3<MatchResponse, TeamResponse, TeamResponse, CombinedMatchTeamsResponse> { matchDetailResponse, homeTeamResponse, awayTeamResponse ->
            CombinedMatchTeamsResponse(matchDetailResponse, homeTeamResponse, awayTeamResponse)
        }).subscribe(object : Observer<CombinedMatchTeamsResponse> {
            override fun onComplete() {}

            override fun onSubscribe(d: Disposable) {}

            override fun onNext(combinedMatchTeamsResponse: CombinedMatchTeamsResponse) {
                val matchDetailResponseEvent = combinedMatchTeamsResponse.matchDetailResponse.events?.first()

                if(matchDetailResponseEvent != null){
                    callback.onDataLoaded(combinedMatchTeamsResponse)
                } else {
                    callback.onDataError()
                }
            }

            override fun onError(error: Throwable) {
                callback.onDataError()
            }

        })
    }
}