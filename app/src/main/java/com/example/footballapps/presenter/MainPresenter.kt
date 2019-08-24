package com.example.footballapps.presenter

import com.example.footballapps.model.LeagueItem
import com.example.footballapps.view.MainView

// todo: presenter itu return array list yang menampung LeagueItem
class MainPresenter(private val mainView : MainView){

    fun displayLeagueInfoListToRecyclerView(leagueInfoList : MutableList<LeagueItem>){
        // todo: tinggal panggil view
        mainView.displayRecyclerViewItem(leagueInfoList)
    }
}