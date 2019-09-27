package com.example.footballapps.view

import com.example.footballapps.favorite.FavoriteMatchItem

interface FavoriteMatchView {
    fun dataIsLoading()
    fun dataLoadingFinished()
    fun dataFailedToLoad()
    fun showMatchData(favoriteMatchList: List<FavoriteMatchItem>)
}