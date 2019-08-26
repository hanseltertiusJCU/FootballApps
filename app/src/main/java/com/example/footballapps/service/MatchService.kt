package com.example.footballapps.service

import com.example.footballapps.model.MatchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MatchService {
    @GET("eventsnextleague.php")
    fun getNextMatchResponse(@Query("id") idLeague : String) : Call<MatchResponse>

    @GET("eventspastleague.php")
    fun getPastMatchResponse(@Query("id") idLeague: String) : Call<MatchResponse>

    @GET("lookupevent.php?")
    fun getDetailMatchResponse(@Query("id") idEvent : String) : Call<MatchResponse>

    @GET("searchevents.php")
    fun getSearchMatchResponse(@Query("e") query : String) : Call<MatchResponse>
}