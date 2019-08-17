package com.example.footballapps

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import com.example.footballapps.activity.LeagueDetailActivity
import com.example.footballapps.adapter.LeagueRecyclerViewAdapter
import com.example.footballapps.model.LeagueItem
import org.jetbrains.anko.intentFor

class MainActivity : AppCompatActivity() {

    private var leagueItems : MutableList<LeagueItem> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rvLeagueList = findViewById<RecyclerView>(R.id.rv_league_list)
        initData()
        rvLeagueList.layoutManager = GridLayoutManager(this, 2)
        rvLeagueList.adapter = LeagueRecyclerViewAdapter(this, leagueItems){
            startActivity(intentFor<LeagueDetailActivity>("leagueItem" to it))
        }
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

    private fun convertDpToPx(dp : Float) : Int {
        val r = resources
        val px = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            r.displayMetrics
        )
        return px.toInt()
    }
}
