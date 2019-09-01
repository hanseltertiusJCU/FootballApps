package com.example.footballapps.presenter

import android.util.Log
import com.example.footballapps.R
import com.example.footballapps.application.FootballApps
import com.example.footballapps.client.RetrofitClient
import com.example.footballapps.model.LeagueDetailResponse
import com.example.footballapps.model.LeagueItem
import com.example.footballapps.service.LeagueDetailService
import com.example.footballapps.view.LeagueDetailView
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LeagueDetailPresenter(private val leagueDetailView : LeagueDetailView){

    companion object {
        val noDataText = FootballApps.res.getString(R.string.no_data_to_show)
        val noConnectionText = FootballApps.res.getString(R.string.no_internet_connection)
        val failedToRetrieveText = FootballApps.res.getString(R.string.failed_to_retrieve_data)
    }

    fun getLeagueDetailInfo(leagueId : String){

        leagueDetailView.dataIsLoading()

        val retrofitClient = RetrofitClient()
        val retrofit = retrofitClient.getClient()
        val leagueDetailService = retrofit?.create(LeagueDetailService::class.java)

        val leagueDetailResponseObservable : Observable<LeagueDetailResponse>? = leagueDetailService
            ?.getLeagueDetailResponse(leagueId)
            ?.subscribeOn(Schedulers.newThread())
            ?.observeOn(AndroidSchedulers.mainThread())

        leagueDetailResponseObservable?.subscribe(object : Observer<LeagueDetailResponse> {
            override fun onComplete() {}

            override fun onSubscribe(disposable: Disposable) {}

            override fun onNext(leagueDetailResponse: LeagueDetailResponse) {

                val leagues = leagueDetailResponse.leagues

                if(leagues != null){
                    leagueDetailView.showLeagueDetailInfo(leagues.first())

                    leagueDetailView.dataLoadingFinished()
                } else {
                    leagueDetailView.dataFailedToLoad(noDataText)
                }

            }

            override fun onError(error: Throwable) {
                Log.d("leagueDetailError", error.message!!)

                if(error.message!!.contains("Unable to resolve host")){
                    leagueDetailView.dataFailedToLoad(noConnectionText)
                }  else {
                    leagueDetailView.dataFailedToLoad(failedToRetrieveText)
                }

            }

        })
    }
}