package com.example.footballapps.repository

import com.example.footballapps.callback.LeagueDetailRepositoryCallback
import com.example.footballapps.client.RetrofitClient
import com.example.footballapps.model.LeagueDetailResponse
import com.example.footballapps.service.LeagueDetailService
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class LeagueDetailRepository {

    fun getLeagueDetail(id : String, callback : LeagueDetailRepositoryCallback<LeagueDetailResponse?>) {
        RetrofitClient
            .createService(LeagueDetailService::class.java)
            .getLeagueDetailResponse(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<LeagueDetailResponse> {
                override fun onComplete() {}

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(leagueDetailResponse: LeagueDetailResponse) {
                    val leaguesList = leagueDetailResponse.leagues
                    if(leaguesList != null){
                        if(leaguesList.isNotEmpty()){
                            callback.onDataLoaded(leagueDetailResponse)
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