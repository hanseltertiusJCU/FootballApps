package com.example.footballapps.service

import com.example.footballapps.model.PlayerResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface PlayersService {
    @GET("lookup_all_players.php")
    fun getPlayersResponse(@Query("id") idTeam: String): Observable<PlayerResponse>

    @GET("lookupplayer.php")
    fun getPlayerDetailResponse(@Query("id") idPlayer: String): Observable<PlayerResponse>
}