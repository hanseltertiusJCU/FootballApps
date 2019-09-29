package com.example.footballapps.repository

import com.example.footballapps.callback.TeamsRepositoryCallback
import com.example.footballapps.client.RetrofitClient
import com.example.footballapps.model.TeamItem
import com.example.footballapps.model.TeamResponse
import com.example.footballapps.service.TeamService
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class TeamsRepository {

    fun getTeams(id: String, callback: TeamsRepositoryCallback<TeamResponse?>) {
        RetrofitClient
            .createService(TeamService::class.java)
            .getTeamsResponse(id)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<TeamResponse> {
                override fun onComplete() {}

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(teamResponse: TeamResponse) {
                    val teamsList = teamResponse.teams
                    if (teamsList != null) {
                        if (teamsList.isNotEmpty()) {
                            callback.onDataLoaded(teamResponse)
                        } else {
                            callback.onDataError()
                        }
                    } else {
                        callback.onDataError()
                    }
                }

                override fun onError(e: Throwable) {
                    callback.onDataError()
                }

            })
    }

    fun getSearchResultTeams(query: String, callback: TeamsRepositoryCallback<TeamResponse?>) {
        RetrofitClient
            .createService(TeamService::class.java)
            .getSearchResultTeamsResponse(query)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<TeamResponse> {
                override fun onComplete() {}

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(teamResponse: TeamResponse) {
                    val searchResultTeamsList = teamResponse.teams
                    val filteredSearchResultTeamsList = mutableListOf<TeamItem>()

                    if (searchResultTeamsList != null) {
                        for (team in searchResultTeamsList) {
                            if (team.sportType.equals("Soccer")) {
                                filteredSearchResultTeamsList.add(team)
                            }
                        }
                        teamResponse.teams = filteredSearchResultTeamsList
                    }

                    if (filteredSearchResultTeamsList.isNotEmpty()) {
                        callback.onDataLoaded(teamResponse)
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