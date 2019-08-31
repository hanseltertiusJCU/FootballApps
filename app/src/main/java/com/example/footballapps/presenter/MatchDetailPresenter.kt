package com.example.footballapps.presenter

import android.util.Log
import com.example.footballapps.client.RetrofitClient
import com.example.footballapps.model.CombinedMatchTeamsResponse
import com.example.footballapps.model.MatchResponse
import com.example.footballapps.model.TeamResponse
import com.example.footballapps.service.MatchService
import com.example.footballapps.service.TeamService
import com.example.footballapps.view.MatchDetailView
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers

class MatchDetailPresenter(private val matchDetailView: MatchDetailView) {

    fun getDetailMatchInfo(eventId: String, homeTeamId: String, awayTeamId: String) {
        matchDetailView.dataIsLoading()

        val retrofitClient = RetrofitClient()
        val retrofit = retrofitClient.getClient()
        val matchService = retrofit?.create(MatchService::class.java)
        val teamService = retrofit?.create(TeamService::class.java)

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

        val combinedMatchTeamsObservable: Observable<CombinedMatchTeamsResponse> = Observable.zip(
            matchResponseObservable,
            homeTeamResponseObservable,
            awayTeamResponseObservable,
            Function3<MatchResponse, TeamResponse, TeamResponse, CombinedMatchTeamsResponse> { matchDetailResponse, homeTeamResponse, awayTeamResponse ->
                CombinedMatchTeamsResponse(matchDetailResponse, homeTeamResponse, awayTeamResponse)
            }
        )

        combinedMatchTeamsObservable.subscribe(object :
            Observer<CombinedMatchTeamsResponse> {
            override fun onComplete() {}

            override fun onSubscribe(disposable: Disposable) {}

            override fun onNext(combinedMatchTeamsResponse: CombinedMatchTeamsResponse) {
                val matchDetailResponse = combinedMatchTeamsResponse.matchDetailResponse
                val matchDetail = matchDetailResponse.events.first()

                matchDetailView.showMatchData(matchDetail)

                val homeTeamResponse = combinedMatchTeamsResponse.homeTeamResponse
                val homeTeam = homeTeamResponse.teams.first()

                matchDetailView.showHomeTeamBadge(homeTeam.teamBadge)

                val awayTeamResponse = combinedMatchTeamsResponse.awayTeamResponse
                val awayTeam = awayTeamResponse.teams.first()

                matchDetailView.showAwayTeamBadge(awayTeam.teamBadge)

                matchDetailView.dataLoadingFinished()

            }

            override fun onError(error: Throwable) {
                Log.d("matchDetailError", error.message!!)

                matchDetailView.dataFailedToLoad()

            }

        })
    }
}