package com.example.footballapps.view

import com.example.footballapps.model.CombinedMatchTeamsResponse

interface MatchDetailView {
    fun dataIsLoading()
    fun dataLoadingFinished()
    fun dataFailedToLoad()
    fun showMatchDetailData(combinedMatchTeamsResponse: CombinedMatchTeamsResponse)
}