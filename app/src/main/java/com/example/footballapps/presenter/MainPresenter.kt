package com.example.footballapps.presenter

import com.example.footballapps.model.LeagueItem
import com.example.footballapps.view.MainView

class MainPresenter(private val mainView: MainView) {

    fun displayLeagueInfoListToRecyclerView(leagueInfoList: MutableList<LeagueItem>) {
        mainView.displayRecyclerViewItem(leagueInfoList)
    }
}