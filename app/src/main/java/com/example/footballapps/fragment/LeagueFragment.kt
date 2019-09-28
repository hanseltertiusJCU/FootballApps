package com.example.footballapps.fragment


import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.footballapps.R
import com.example.footballapps.activity.LeagueDetailActivity
import com.example.footballapps.adapter.LeagueRecyclerViewAdapter
import com.example.footballapps.model.LeagueItem
import com.example.footballapps.presenter.LeaguePresenter
import com.example.footballapps.utils.GridSpacingItemDecoration
import com.example.footballapps.view.LeagueView
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.wrapContent

/**
 * A simple [Fragment] subclass.
 */
class LeagueFragment : Fragment(), AnkoComponent<Context>, LeagueView {


    private lateinit var recyclerViewLeagueList : RecyclerView

    private lateinit var leaguePresenter : LeaguePresenter

    private var leagueItems : MutableList<LeagueItem> = mutableListOf()

    private val spanCount = 2
    private val includeEdge = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return createView(AnkoContext.create(requireContext()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(savedInstanceState == null){
            initData()
        }
    }

    private fun initData(){
        val leagueId = resources.getStringArray(R.array.league_id)
        val leagueName = resources.getStringArray(R.array.league_name)
        val leagueImage = resources.obtainTypedArray(R.array.league_image)

        leagueItems.clear()

        for(i in leagueName.indices){
            leagueItems.add(LeagueItem(leagueId[i], leagueName[i], leagueImage.getResourceId(i, 0)))
        }

        leagueImage.recycle()

        leaguePresenter = LeaguePresenter(this)

        leaguePresenter.displayLeagueInfoListToRecyclerView(leagueItems)
    }

    override fun createView(ui: AnkoContext<Context>): View = with(ui) {
        constraintLayout {
            lparams(width = matchParent, height = matchParent)
            recyclerViewLeagueList = recyclerView {
                id = R.id.rv_league_list
                isNestedScrollingEnabled = false
            }.lparams {
                width = matchParent
                height = wrapContent
            }
        }
    }

    override fun displayRecyclerViewItem(leagueInfoList: MutableList<LeagueItem>) {
        recyclerViewLeagueList.layoutManager = GridLayoutManager(context, spanCount)
        recyclerViewLeagueList.adapter = LeagueRecyclerViewAdapter(leagueInfoList) {
            startActivity<LeagueDetailActivity>("leagueItem" to it)
        }
        recyclerViewLeagueList.addItemDecoration(GridSpacingItemDecoration(spanCount = spanCount, space = dip(16), includeEdge = includeEdge))
    }


}
