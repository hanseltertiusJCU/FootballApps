package com.example.footballapps.presenter

import com.example.footballapps.model.LeagueItem
import com.example.footballapps.view.LeagueView

class LeaguePresenter(private val leagueView: LeagueView) {

    fun displayLeagueInfoListToRecyclerView(leagueInfoList: MutableList<LeagueItem>) {
        leagueView.displayRecyclerViewItem(leagueInfoList)
    }
}