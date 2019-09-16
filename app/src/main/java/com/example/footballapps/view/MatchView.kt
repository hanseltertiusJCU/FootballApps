package com.example.footballapps.view

import com.example.footballapps.model.MatchResponse

interface MatchView {
    fun dataIsLoading()
    fun dataLoadingFinished()
    fun dataFailedToLoad()
    fun showMatchesData(matchResponse: MatchResponse)
}