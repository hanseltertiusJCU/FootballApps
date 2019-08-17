package com.example.footballapps.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import com.example.footballapps.R
import com.example.footballapps.adapter.LeagueRecyclerViewAdapter
import com.example.footballapps.model.LeagueItem
import com.example.footballapps.utils.GridSpacingItemDecoration
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.intentFor

class MainActivity : AppCompatActivity() {

    private var leagueItems : MutableList<LeagueItem> = mutableListOf()

    private val spanCount = 2
    private val includeEdge = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initData()
        rv_league_list.layoutManager = GridLayoutManager(this, spanCount)
        rv_league_list.adapter = LeagueRecyclerViewAdapter(this, leagueItems){
            startActivity(intentFor<LeagueDetailActivity>("leagueItem" to it))
        }
        rv_league_list.addItemDecoration(GridSpacingItemDecoration(spanCount = spanCount, space = convertDpToPx(8f), includeEdge = includeEdge))
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
