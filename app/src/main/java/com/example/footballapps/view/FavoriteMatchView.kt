package com.example.footballapps.view

import com.example.footballapps.favorite.FavoriteMatchItem

interface FavoriteMatchView {
    fun dataIsLoading()
    fun dataLoadingFinished()
    fun dataFailedToLoad(errorText : String)
    fun showMatchData(favoriteMatchList : List<FavoriteMatchItem>)
}