package com.example.footballapps.service

import com.example.footballapps.model.LeagueDetailResponse
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface LeagueDetailService {
    @GET("lookupleague.php")
    fun getLeagueDetailResponse(@Query("id") idLeague : String) : Observable<LeagueDetailResponse>
}