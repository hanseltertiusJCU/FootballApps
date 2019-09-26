package com.example.footballapps.view

import com.example.footballapps.favorite.FavoriteTeamItem

interface FavoriteTeamView {
    fun dataIsLoading()
    fun dataLoadingFinished()
    fun dataFailedToLoad(errorText : String)
    fun showTeamData(favoriteTeamList: List<FavoriteTeamItem>)
}