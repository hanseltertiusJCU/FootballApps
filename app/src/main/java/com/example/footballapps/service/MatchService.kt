package com.example.footballapps.service

import com.example.footballapps.model.MatchResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface MatchService {
    @GET("eventsnextleague.php")
    fun getNextMatchResponse(@Query("id") idLeague: String): Observable<MatchResponse>

    @GET("eventspastleague.php")
    fun getPastMatchResponse(@Query("id") idLeague: String): Observable<MatchResponse>

    @GET("lookupevent.php?")
    fun getDetailMatchResponse(@Query("id") idEvent: String): Observable<MatchResponse>

    @GET("searchevents.php")
    fun getSearchMatchResponse(@Query("e") query: String): Observable<MatchResponse>
}