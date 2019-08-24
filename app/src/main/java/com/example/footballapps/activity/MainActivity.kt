package com.example.footballapps.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.TypedValue
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.footballapps.R
import com.example.footballapps.adapter.LeagueRecyclerViewAdapter
import com.example.footballapps.model.LeagueItem
import com.example.footballapps.presenter.MainPresenter
import com.example.footballapps.utils.GridSpacingItemDecoration
import com.example.footballapps.view.MainView
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.recyclerview.v7.recyclerView

class MainActivity : AppCompatActivity(), MainView {

    private lateinit var mainPresenter: MainPresenter

    private var leagueItems: MutableList<LeagueItem> = mutableListOf()

    private val spanCount = 2
    private val includeEdge = true

    private lateinit var mainActivityUI: MainActivityUI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainActivityUI = MainActivityUI()
        mainActivityUI.setContentView(this)

        initData()

    }

    private fun initData() {
        val leagueId = resources.getStringArray(R.array.league_id)
        val leagueName = resources.getStringArray(R.array.league_name)
        val leagueImage = resources.obtainTypedArray(R.array.league_image)

        leagueItems.clear()

        for (i in leagueName.indices) {
            leagueItems.add(
                LeagueItem(
                    leagueId[i],
                    leagueName[i],
                    leagueImage.getResourceId(i, 0)
                )
            )
        }

        leagueImage.recycle()

        mainPresenter = MainPresenter(this)

        Log.d("leagueItems", leagueItems.size.toString())

        mainPresenter.displayLeagueInfoListToRecyclerView(leagueItems)

    }

    override fun displayRecyclerViewItem(leagueInfoList: MutableList<LeagueItem>) {
        mainActivityUI.recyclerViewLeagueList.layoutManager =
            GridLayoutManager(this, spanCount)
        mainActivityUI.recyclerViewLeagueList.adapter = LeagueRecyclerViewAdapter(leagueInfoList) {leagueItem ->

            mainActivityUI.constraintLayoutView.snackbar(leagueItem.leagueName.toString(), "Option") {
                val options = listOf("Go to League Detail Info", "Go to League Match Info")
                selector("Where do you want to go to?", options) { _, i ->
                    if(i == 0) {
                        startActivity<LeagueDetailActivity>("leagueItem" to leagueItem)
                    } else {
                        toast(options[i])
                    }

                }
            }

        }
        mainActivityUI.recyclerViewLeagueList.addItemDecoration(
            GridSpacingItemDecoration(
                spanCount = spanCount,
                space = convertDpToPx(8f),
                includeEdge = includeEdge
            )
        )
    }

    private fun convertDpToPx(dp: Float): Int {
        val r = resources
        val px = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            r.displayMetrics
        )
        return px.toInt()
    }

    class MainActivityUI : AnkoComponent<MainActivity> {
        lateinit var recyclerViewLeagueList : RecyclerView

        lateinit var constraintLayoutView: View

        companion object {
            const val recyclerViewLeagueListId = 101
        }

        // todo: toolbar


        override fun createView(ui: AnkoContext<MainActivity>): View = with(ui){
            constraintLayoutView = constraintLayout {
                lparams(width = matchParent, height = matchParent)
                recyclerViewLeagueList = recyclerView {
                    id = recyclerViewLeagueListId
                }.lparams(width = matchParent, height = matchParent)
            }

            return@with constraintLayoutView
        }

    }
}
