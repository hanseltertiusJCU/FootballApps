package com.example.footballapps.view

import com.example.footballapps.model.PlayerResponse

interface PlayerDetailView {
    fun dataIsLoading()
    fun dataLoadingFinished()
    fun dataFailedToLoad()
    fun showPlayerDetailData(playerResponse: PlayerResponse)
}