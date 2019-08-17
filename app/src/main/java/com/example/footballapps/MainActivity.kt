package com.example.footballapps

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import com.example.footballapps.adapter.LeagueRecyclerViewAdapter
import com.example.footballapps.model.LeagueItem

class MainActivity : AppCompatActivity() {

    private var leagueItems : MutableList<LeagueItem> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val leagueList = findViewById<RecyclerView>(R.id.rv_league_list)
        initData()
        leagueList.layoutManager = GridLayoutManager(this, 2)
        leagueList.adapter = LeagueRecyclerViewAdapter(this, leagueItems)
    }

    private fun initData(){
        val leagueName = resources.getStringArray(R.array.league_name)
        val leagueDesc = resources.getStringArray(R.array.league_desc)
        val leagueImage = resources.obtainTypedArray(R.array.league_image)

        leagueItems.clear()

        for(i in leagueName.indices) {
            leagueItems.add(LeagueItem(
                leagueName[i],
                leagueDesc[i],
                leagueImage.getResourceId(i, 0)))
        }

        leagueImage.recycle()
    }
}
