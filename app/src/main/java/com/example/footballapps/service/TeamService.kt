package com.example.footballapps.service

import com.example.footballapps.model.TeamResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface TeamService {
    @GET("lookup_all_teams.php")
    fun getTeamsResponse(@Query("id") idLeague: String): Observable<TeamResponse>

    @GET("lookupteam.php")
    fun getTeamDetailResponse(@Query("id") idTeam: String): Observable<TeamResponse>

    @GET("searchteams.php")
    fun getSearchResultTeamsResponse(@Query("t") query: String): Observable<TeamResponse>

}