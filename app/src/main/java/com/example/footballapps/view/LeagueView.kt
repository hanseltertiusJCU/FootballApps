package com.example.footballapps.view

import com.example.footballapps.model.LeagueItem

interface LeagueView {
    fun displayRecyclerViewItem(leagueInfoList: MutableList<LeagueItem>)
}