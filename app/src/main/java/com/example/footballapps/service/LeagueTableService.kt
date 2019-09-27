package com.example.footballapps.service

import com.example.footballapps.model.LeagueTableResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface LeagueTableService {
    @GET("lookuptable.php")
    fun getLeagueTableResponse(@Query("l") idLeague : String) : Observable<LeagueTableResponse>

    @GET("lookuptable.php")
    fun getLeagueTableResponseSeason(@Query("l") idLeague : String, @Query("s") season : String) : Observable<LeagueTableResponse>
}