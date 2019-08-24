package com.example.footballapps.view

import com.example.footballapps.model.LeagueItem

interface MainView {
    // todo: function that change title on toolbar, show recyclerview data
    fun displayRecyclerViewItem(leagueInfoList : MutableList<LeagueItem>)
}