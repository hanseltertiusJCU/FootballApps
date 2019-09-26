package com.example.footballapps.presenter

import android.annotation.SuppressLint
import android.content.Context
import com.example.footballapps.R
import com.example.footballapps.application.FootballApps
import com.example.footballapps.favorite.FavoriteTeamItem
import com.example.footballapps.helper.database
import com.example.footballapps.view.FavoriteTeamView
import org.jetbrains.anko.db.SqlOrderDirection
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.select

class FavoriteTeamPresenter(private val favoriteTeamView : FavoriteTeamView, private val context: Context) {

    companion object {
        val noDataText = FootballApps.res.getString(R.string.no_data_to_show)
        val noConnectionText = FootballApps.res.getString(R.string.no_internet_connection)
    }

    fun getFavoriteTeamInfo(isNetworkActive : Boolean){
        favoriteTeamView.dataIsLoading()

        if(isNetworkActive){
            context.database.use {
                val favoriteTeam = select(FavoriteTeamItem.TABLE_FAVORITE_TEAM).orderBy("ID_", SqlOrderDirection.DESC)
                val favoriteTeamList = favoriteTeam.parseList(classParser<FavoriteTeamItem>())
                if(favoriteTeamList.isNotEmpty()){
                    favoriteTeamView.showTeamData(favoriteTeamList)

                    favoriteTeamView.dataLoadingFinished()
                } else {
                    favoriteTeamView.dataFailedToLoad(noDataText)
                }
            }
        } else {
            favoriteTeamView.dataFailedToLoad(noConnectionText)
        }
    }

    fun getFavoriteTeamInfoSearchResult(isNetworkActive: Boolean, query : String){
        favoriteTeamView.dataIsLoading()

        if(isNetworkActive) {
            val capitalizedQuery = getCapitalizedWord(query)
            context.database.use {
                val favoriteTeamSearchResult = select(FavoriteTeamItem.TABLE_FAVORITE_TEAM).whereArgs("TEAM_NAME LIKE '%$capitalizedQuery%'").orderBy("ID_", SqlOrderDirection.DESC)
                val favoriteTeamList = favoriteTeamSearchResult.parseList(classParser<FavoriteTeamItem>())
                if(favoriteTeamList.isNotEmpty()){
                    favoriteTeamView.showTeamData(favoriteTeamList)

                    favoriteTeamView.dataLoadingFinished()
                } else {
                    favoriteTeamView.dataFailedToLoad(noDataText)
                }
            }
        } else {
            favoriteTeamView.dataFailedToLoad(noConnectionText)
        }
    }

    @SuppressLint("DefaultLocale")
    private fun getCapitalizedWord(inputString: String): String {
        val space = " "
        val splitQuery = inputString.split(space)
        return splitQuery.joinToString(space) { it.capitalize() }
    }





}