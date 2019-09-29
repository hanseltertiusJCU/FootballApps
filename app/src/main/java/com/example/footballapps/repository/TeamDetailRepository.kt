package com.example.footballapps.repository

import com.example.footballapps.callback.TeamDetailRepositoryCallback
import com.example.footballapps.client.RetrofitClient
import com.example.footballapps.model.TeamResponse
import com.example.footballapps.service.TeamService
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class TeamDetailRepository {

    fun getTeamDetail(id: String, callback: TeamDetailRepositoryCallback<TeamResponse?>) {
        RetrofitClient
            .createService(TeamService::class.java)
            .getTeamDetailResponse(id)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<TeamResponse> {
                override fun onComplete() {}

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(teamResponse: TeamResponse) {
                    val teamDetailList = teamResponse.teams
                    if (teamDetailList != null) {
                        if (teamDetailList.isNotEmpty()) {
                            callback.onDataLoaded(teamResponse)
                        } else {
                            callback.onDataError()
                        }
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