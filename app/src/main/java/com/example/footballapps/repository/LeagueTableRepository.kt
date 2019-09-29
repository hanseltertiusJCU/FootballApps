package com.example.footballapps.repository

import com.example.footballapps.callback.LeagueTableRepositoryCallback
import com.example.footballapps.client.RetrofitClient
import com.example.footballapps.model.LeagueTableResponse
import com.example.footballapps.service.LeagueTableService
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class LeagueTableRepository {

    fun getLeagueTable(
        id: String,
        season: String,
        callback: LeagueTableRepositoryCallback<LeagueTableResponse?>
    ) {
        RetrofitClient
            .createService(LeagueTableService::class.java)
            .getLeagueTableResponse(id, season)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<LeagueTableResponse> {
                override fun onComplete() {}

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(leagueTableResponse: LeagueTableResponse) {
                    val leagueTable = leagueTableResponse.leagueTable
                    if (leagueTable != null) {
                        if (leagueTable.isNotEmpty()) {
                            callback.onDataLoaded(leagueTableResponse)
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