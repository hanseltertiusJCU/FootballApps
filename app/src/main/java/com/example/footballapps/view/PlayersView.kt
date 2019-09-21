package com.example.footballapps.view

import com.example.footballapps.model.PlayerResponse

interface PlayersView {
    fun dataIsLoading()
    fun dataLoadingFinished()
    fun dataFailedToLoad()
    fun showPlayersData(playerResponse: PlayerResponse)
}