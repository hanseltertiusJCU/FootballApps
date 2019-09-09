package com.example.footballapps.presenter

import android.annotation.SuppressLint
import android.content.Context
import com.example.footballapps.R
import com.example.footballapps.application.FootballApps
import com.example.footballapps.favorite.FavoriteMatchItem
import com.example.footballapps.helper.database
import com.example.footballapps.view.FavoriteMatchView
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.select

class FavoriteMatchPresenter(
    private val favoriteMatchView: FavoriteMatchView,
    private val context: Context
) {

    companion object {
        val noDataText = FootballApps.res.getString(R.string.no_data_to_show)
        val noConnectionText = FootballApps.res.getString(R.string.no_internet_connection)
    }

    fun getFavoriteMatchInfo(isNetworkActive: Boolean) {
        favoriteMatchView.dataIsLoading()

        if (isNetworkActive) {
            context.database.use {
                val favoriteMatchResult = select(FavoriteMatchItem.TABLE_FAVORITE_MATCH)
                val favoriteMatchList =
                    favoriteMatchResult.parseList(classParser<FavoriteMatchItem>())
                if (favoriteMatchList.isNotEmpty()) {
                    favoriteMatchView.showMatchData(favoriteMatchList)

                    favoriteMatchView.dataLoadingFinished()
                } else {
                    favoriteMatchView.dataFailedToLoad(noDataText)
                }
            }
        } else {
            favoriteMatchView.dataFailedToLoad(noConnectionText)
        }

    }

    fun getFavoriteMatchInfoSearchResult(isNetworkActive: Boolean, query: String) {
        favoriteMatchView.dataIsLoading()

        if (isNetworkActive) {
            val capitalizedQuery = getCapitalizedWord(query)
            context.database.use {
                val favoriteMatchResult = select(FavoriteMatchItem.TABLE_FAVORITE_MATCH)
                    .whereArgs(
                        "HOME_TEAM_NAME = {query} OR AWAY_TEAM_NAME = {query} OR LEAGUE_NAME LIKE '%$capitalizedQuery%'",
                        "query" to capitalizedQuery
                    )
                val favoriteMatchList =
                    favoriteMatchResult.parseList(classParser<FavoriteMatchItem>())
                if (favoriteMatchList.isNotEmpty()) {
                    favoriteMatchView.showMatchData(favoriteMatchList)

                    favoriteMatchView.dataLoadingFinished()
                } else {
                    favoriteMatchView.dataFailedToLoad(noDataText)
                }
            }
        } else {
            favoriteMatchView.dataFailedToLoad(noConnectionText)
        }
    }

    @SuppressLint("DefaultLocale")
    private fun getCapitalizedWord(inputString: String): String {
        val space = " "
        val splitQuery = inputString.split(space)
        return splitQuery.joinToString(space) { it.capitalize() }
    }

}