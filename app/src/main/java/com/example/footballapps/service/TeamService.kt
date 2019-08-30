package com.example.footballapps.service

import retrofit2.http.GET
import retrofit2.http.Query

interface TeamService {
    @GET("lookupteam.php")
    fun getTeamResponse(@Query("id") idTeam : String)
}