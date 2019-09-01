package com.example.footballapps.view

import com.example.footballapps.model.MatchItem

interface MatchView {
    fun dataIsLoading()
    fun dataLoadingFinished()
    fun dataFailedToLoad(errorText : String)
    fun showMatchData(matchList : List<MatchItem>)
}