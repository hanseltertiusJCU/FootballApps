package com.example.footballapps.activity

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.footballapps.R
import com.example.footballapps.adapter.LeagueRecyclerViewAdapter
import com.example.footballapps.model.LeagueItem
import com.example.footballapps.presenter.MainPresenter
import com.example.footballapps.utils.GridSpacingItemDecoration
import com.example.footballapps.view.MainView
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.design.themedAppBarLayout
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

        setSupportActionBar(mainActivityUI.toolbarMain)
        supportActionBar?.title = "Leagues"

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

        mainPresenter.displayLeagueInfoListToRecyclerView(leagueItems)

        setSupportActionBar(mainActivityUI.toolbarMain)

    }

    override fun displayRecyclerViewItem(leagueInfoList: MutableList<LeagueItem>) {
        mainActivityUI.recyclerViewLeagueList.layoutManager =
            GridLayoutManager(this, spanCount)
        mainActivityUI.recyclerViewLeagueList.adapter =
            LeagueRecyclerViewAdapter(leagueInfoList) { leagueItem ->
                startActivity<LeagueDetailActivity>(
                    "leagueName" to leagueItem.leagueName,
                    "leagueId" to leagueItem.leagueId
                )
            }
        mainActivityUI.recyclerViewLeagueList.addItemDecoration(
            GridSpacingItemDecoration(
                spanCount = spanCount,
                space = convertDpToPx(16f),
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
        lateinit var recyclerViewLeagueList: RecyclerView

        lateinit var constraintLayoutView: View

        lateinit var toolbarMain: Toolbar

        companion object {
            const val recyclerViewLeagueListId = 101
        }


        override fun createView(ui: AnkoContext<MainActivity>): View = with(ui) {

            coordinatorLayout {

                verticalLayout {
                    themedAppBarLayout(R.style.ThemeOverlay_AppCompat_Dark_ActionBar) {
                        lparams(width = matchParent, height = wrapContent)
                        toolbarMain = toolbar {
                            lparams(width = matchParent, height = dimenAttr(R.attr.actionBarSize))
                            popupTheme = R.style.ThemeOverlay_AppCompat_Light
                        }
                    }

                    constraintLayoutView = constraintLayout {
                        lparams(width = matchParent, height = wrapContent)
                        recyclerViewLeagueList = recyclerView {
                            id = recyclerViewLeagueListId
                            isNestedScrollingEnabled = false
                        }.lparams(width = matchParent, height = wrapContent)
                    }

                }

            }
        }

    }
}
