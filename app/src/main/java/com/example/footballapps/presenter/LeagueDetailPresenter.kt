package com.example.footballapps.presenter

import android.util.Log
import com.example.footballapps.client.RetrofitClient
import com.example.footballapps.model.LeagueDetailResponse
import com.example.footballapps.model.LeagueItem
import com.example.footballapps.service.LeagueDetailService
import com.example.footballapps.view.LeagueDetailView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LeagueDetailPresenter(private val leagueDetailView : LeagueDetailView, private val leagueItem : LeagueItem){

    fun getLeagueDetailTitle(){
        leagueDetailView.showLeagueDetailTitle(leagueItem)
    }

    fun getLeagueDetailInfo(){
        leagueDetailView.dataIsLoading()

        val retrofitClient = RetrofitClient()
        val retrofit = retrofitClient.getClient()
        val leagueDetailService = retrofit?.create(LeagueDetailService::class.java)
        val call : Call<LeagueDetailResponse> = leagueDetailService?.getLeagueDetailResponse(leagueItem.leagueId!!)!!

        call.enqueue(object : Callback<LeagueDetailResponse>{

            override fun onResponse(call: Call<LeagueDetailResponse>, response: Response<LeagueDetailResponse>) {
                if(response.isSuccessful){

                    leagueDetailView.dataLoadingFinished()

                    val data = response.body()

                    val leagues = data?.leagues

                    leagueDetailView.showLeagueDetailInfo(leagues!!)
                }
            }

            override fun onFailure(call: Call<LeagueDetailResponse>, error: Throwable) {

                leagueDetailView.dataFailedToLoad()

                Log.e("errorTag", "Error : ${error.message}")
            }

        })
    }
}