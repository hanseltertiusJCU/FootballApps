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

                    if(matches != null){
                        matchView.showMatchData(matches)
                    }

                    matchView.dataLoadingFinished()
                }
            }

            override fun onFailure(call: Call<MatchResponse>, error: Throwable) {

                matchView.dataFailedToLoad()

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

                    if(matches != null){
                        matchView.showMatchData(matches)
                    }

                    matchView.dataLoadingFinished()
                }
            }

            override fun onFailure(call: Call<MatchResponse>, error: Throwable) {

                matchView.dataFailedToLoad()

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

                matchView.dataFailedToLoad()

                Log.e("errorTag", "Error : ${error.message}")
            }

        })
    }
}