package com.example.footballapps.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import com.example.footballapps.R
import com.example.footballapps.activity.MatchDetailActivity
import com.example.footballapps.activity.MatchScheduleActivity
import com.example.footballapps.adapter.MatchRecyclerViewAdapter
import com.example.footballapps.model.LeagueOption
import com.example.footballapps.model.MatchItem
import com.example.footballapps.presenter.MatchPresenter
import com.example.footballapps.utils.gone
import com.example.footballapps.utils.invisible
import com.example.footballapps.utils.visible
import com.example.footballapps.view.MatchView
import org.jetbrains.anko.constraint.layout.constraintLayout

import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.matchConstraint
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.onRefresh
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.swipeRefreshLayout


class LastMatchFragment : Fragment(), MatchView {

    private lateinit var lastMatchRecyclerView : RecyclerView
    private lateinit var lastMatchProgressBar : ProgressBar
    private lateinit var lastMatchSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var lastMatchLeagueSpinner : Spinner

    private lateinit var lastMatchPresenter: MatchPresenter

    private var lastMatches : MutableList<MatchItem> = mutableListOf()
    private lateinit var lastMatchRvAdapter : MatchRecyclerViewAdapter

    private lateinit var selectedLeagueId : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return UI {
            constraintLayout {
                id = R.id.last_match_parent_layout
                lparams(width = matchParent, height = matchParent)

                lastMatchLeagueSpinner = spinner {
                    id = R.id.last_match_league_spinner
                }.lparams{
                    width = matchParent
                    height = wrapContent
                    margin = dip(16)
                }

                lastMatchSwipeRefreshLayout = swipeRefreshLayout{

                    lastMatchRecyclerView = recyclerView {
                        lparams(width = matchParent, height = wrapContent)
                        layoutManager = LinearLayoutManager(context)
                    }

                }.lparams{
                    width = matchConstraint
                    height = matchConstraint
                    topToBottom = R.id.last_match_league_spinner
                    leftToLeft = R.id.last_match_parent_layout
                    rightToRight = R.id.last_match_parent_layout
                    bottomToBottom = R.id.last_match_parent_layout
                    verticalBias = 0f
                }

                lastMatchProgressBar = progressBar()
                    .lparams{
                        topToTop = R.id.last_match_parent_layout
                        leftToLeft = R.id.last_match_parent_layout
                        rightToRight = R.id.last_match_parent_layout
                        bottomToBottom = R.id.last_match_parent_layout
                    }

            }
        }.view
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
    }

    private fun initData(){

        val leagueIdList = resources.getStringArray(R.array.league_id)
        val leagueNameList = resources.getStringArray(R.array.league_name)

        val leagueOptions = mutableListOf<LeagueOption>()

        for(i in leagueIdList.indices) {
            leagueOptions.add(LeagueOption(leagueIdList[i], leagueNameList[i]))
        }

        val spinnerAdapter = ArrayAdapter(activity!!.applicationContext, android.R.layout.simple_spinner_dropdown_item, leagueOptions)
        lastMatchLeagueSpinner.adapter = spinnerAdapter

        var selectedLeagueOption = LeagueOption((activity as MatchScheduleActivity).leagueId, (activity as MatchScheduleActivity).leagueName)
        lastMatchLeagueSpinner.setSelection(spinnerAdapter.getPosition(selectedLeagueOption))

        lastMatchRvAdapter = MatchRecyclerViewAdapter(context!!, lastMatches){
            startActivity<MatchDetailActivity>("eventId" to it.idEvent, "eventName" to it.strEvent)
        }
        lastMatchRecyclerView.adapter = lastMatchRvAdapter

        lastMatchPresenter = MatchPresenter(this)

        lastMatchLeagueSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedLeagueOption = parent!!.selectedItem as LeagueOption
                selectedLeagueId = selectedLeagueOption.leagueId

                lastMatchPresenter.getPreviousMatchInfo(selectedLeagueId)

            }
        }

        lastMatchSwipeRefreshLayout.onRefresh {
            lastMatchPresenter.getPreviousMatchInfo(selectedLeagueId)
        }
    }

    override fun dataIsLoading() {
        lastMatchProgressBar.visible()
        lastMatchRecyclerView.invisible()
    }

    override fun dataLoadingFinished() {
        lastMatchProgressBar.gone()
        lastMatchRecyclerView.visible()
    }

    override fun showMatchData(matchList: List<MatchItem>) {
        lastMatchSwipeRefreshLayout.isRefreshing = false
        lastMatches.clear()
        lastMatches.addAll(matchList)
        lastMatchRvAdapter.notifyDataSetChanged()

    }


}
