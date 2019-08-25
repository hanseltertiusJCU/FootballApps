package com.example.footballapps.view

import com.example.footballapps.model.LeagueItem

interface MainView {
    fun displayRecyclerViewItem(leagueInfoList : MutableList<LeagueItem>)
}