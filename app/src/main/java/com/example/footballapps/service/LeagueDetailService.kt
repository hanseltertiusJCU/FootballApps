package com.example.footballapps.service

import com.example.footballapps.model.LeagueDetailResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface LeagueDetailService {
    @GET("lookupleague.php?id={idLeague}")
    fun getLeagueDetailResponse(@Path("idLeague") idLeague : String) : Call<LeagueDetailResponse>
}