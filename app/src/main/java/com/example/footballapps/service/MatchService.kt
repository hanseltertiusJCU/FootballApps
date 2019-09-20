package com.example.footballapps.service

import com.example.footballapps.model.MatchResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface MatchService {
    @GET("eventsnextleague.php")
    fun getLeagueNextMatchesResponse(@Query("id") idLeague: String): Observable<MatchResponse>

    @GET("eventspastleague.php")
    fun getLeagueLastMatchesResponse(@Query("id") idLeague: String): Observable<MatchResponse>

    @GET("eventsnext.php")
    fun getTeamNextMatchesResponse(@Query("id") idTeam : String) : Observable<MatchResponse>

    @GET("eventslast.php")
    fun getTeamLastMatchesResponse(@Query("id") idTeam: String) : Observable<MatchResponse>

    @GET("lookupevent.php?")
    fun getMatchDetailResponse(@Query("id") idEvent: String): Observable<MatchResponse>

    @GET("searchevents.php")
    fun getSearchResultMatchesResponse(@Query("e") query: String): Observable<MatchResponse>
}