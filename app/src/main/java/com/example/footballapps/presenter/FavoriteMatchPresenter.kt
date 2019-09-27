package com.example.footballapps.presenter

import android.annotation.SuppressLint
import android.content.Context
import com.example.footballapps.R
import com.example.footballapps.application.FootballApps
import com.example.footballapps.favorite.FavoriteMatchItem
import com.example.footballapps.helper.database
import com.example.footballapps.view.FavoriteMatchView
import org.jetbrains.anko.db.SqlOrderDirection
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.select

class FavoriteMatchPresenter(
    private val favoriteMatchView: FavoriteMatchView,
    private val context: Context
) {

    fun getFavoriteMatchInfo() {
        favoriteMatchView.dataIsLoading()

        context.database.use {
            val favoriteMatchResult = select(FavoriteMatchItem.TABLE_FAVORITE_MATCH)
                .orderBy("ID_", SqlOrderDirection.DESC)
            val favoriteMatchList =
                favoriteMatchResult.parseList(classParser<FavoriteMatchItem>())
            if (favoriteMatchList.isNotEmpty()) {
                favoriteMatchView.showMatchData(favoriteMatchList)

                favoriteMatchView.dataLoadingFinished()
            } else {
                favoriteMatchView.dataFailedToLoad()
            }
        }

    }

    fun getFavoriteMatchInfoSearchResult(query: String) {
        favoriteMatchView.dataIsLoading()

        val capitalizedQuery = getCapitalizedWord(query)
        context.database.use {
            val favoriteMatchResult = select(FavoriteMatchItem.TABLE_FAVORITE_MATCH)
                .whereArgs(
                    "HOME_TEAM_NAME = {query} OR AWAY_TEAM_NAME = {query} OR LEAGUE_NAME LIKE '%$capitalizedQuery%'",
                    "query" to capitalizedQuery
                )
                .orderBy("ID_", SqlOrderDirection.DESC)
            val favoriteMatchList =
                favoriteMatchResult.parseList(classParser<FavoriteMatchItem>())
            if (favoriteMatchList.isNotEmpty()) {
                favoriteMatchView.showMatchData(favoriteMatchList)

                favoriteMatchView.dataLoadingFinished()
            } else {
                favoriteMatchView.dataFailedToLoad()
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private fun getCapitalizedWord(inputString: String): String {
        val space = " "
        val splitQuery = inputString.split(space)
        return splitQuery.joinToString(space) { it.capitalize() }
    }

}