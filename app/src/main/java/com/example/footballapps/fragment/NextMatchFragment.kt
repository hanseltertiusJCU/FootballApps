package com.example.footballapps.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.footballapps.R
import com.example.footballapps.activity.MatchDetailActivity
import com.example.footballapps.activity.MatchScheduleActivity
import com.example.footballapps.adapter.MatchRecyclerViewAdapter
import com.example.footballapps.lifecycle.FragmentLifecycle
import com.example.footballapps.model.LeagueOption
import com.example.footballapps.model.MatchItem
import com.example.footballapps.presenter.MatchPresenter
import com.example.footballapps.utils.gone
import com.example.footballapps.utils.invisible
import com.example.footballapps.utils.visible
import com.example.footballapps.view.MatchView
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.constraint.layout.matchConstraint
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.onRefresh
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.swipeRefreshLayout

class NextMatchFragment : Fragment(), MatchView, FragmentLifecycle {

    private lateinit var nextMatchRecyclerView : RecyclerView
    private lateinit var nextMatchProgressBar : ProgressBar
    private lateinit var nextMatchSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var nextMatchLeagueSpinner : Spinner
    private lateinit var nextMatchErrorText : TextView

    private lateinit var  nextMatchPresenter: MatchPresenter

    private var nextMatches : MutableList<MatchItem> = mutableListOf()

    private lateinit var nextMatchRvAdapter : MatchRecyclerViewAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return UI {
            constraintLayout {
                id = R.id.next_match_parent_layout
                lparams(width = matchParent, height = matchParent)

                nextMatchLeagueSpinner = spinner {
                    id = R.id.next_match_league_spinner
                }.lparams{
                    width = matchParent
                    height = wrapContent
                    margin = dip(16)
                }

                nextMatchSwipeRefreshLayout = swipeRefreshLayout{

                    setColorSchemeColors(ContextCompat.getColor(context, R.color.colorAccent))

                    nextMatchRecyclerView = recyclerView {
                        lparams(width = matchParent, height = wrapContent)
                        layoutManager = LinearLayoutManager(context)
                    }

                }.lparams{
                    width = matchConstraint
                    height = matchConstraint
                    topToBottom = R.id.next_match_league_spinner
                    leftToLeft = R.id.next_match_parent_layout
                    rightToRight = R.id.next_match_parent_layout
                    bottomToBottom = R.id.next_match_parent_layout
                    verticalBias = 0f
                }

                nextMatchProgressBar = progressBar().lparams{
                        topToTop = R.id.next_match_parent_layout
                        leftToLeft = R.id.next_match_parent_layout
                        rightToRight = R.id.next_match_parent_layout
                        bottomToBottom = R.id.next_match_parent_layout
                    }

                nextMatchErrorText = textView{
                    textColor = Color.BLACK
                }.lparams{
                    topToTop = R.id.next_match_parent_layout
                    leftToLeft = R.id.next_match_parent_layout
                    rightToRight = R.id.next_match_parent_layout
                    bottomToBottom = R.id.next_match_parent_layout
                }

            }
        }.view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
    }

    private fun initData() {

        val leagueIdList = resources.getStringArray(R.array.league_id)
        val leagueNameList = resources.getStringArray(R.array.league_name)

        val leagueOptions = mutableListOf<LeagueOption>()

        for(i in leagueIdList.indices){
            leagueOptions.add(LeagueOption(leagueIdList[i], leagueNameList[i]))
        }

        val spinnerAdapter = ArrayAdapter(activity!!.applicationContext, android.R.layout.simple_spinner_dropdown_item, leagueOptions)
        nextMatchLeagueSpinner.adapter = spinnerAdapter

        nextMatchRvAdapter = MatchRecyclerViewAdapter(context!!, nextMatches){
            startActivity<MatchDetailActivity>(
                "eventId" to it.idEvent,
                "eventName" to it.strEvent,
                "homeTeamId" to it.homeTeamId,
                "awayTeamId" to it.awayTeamId
            )
        }
        nextMatchRecyclerView.adapter = nextMatchRvAdapter

        nextMatchPresenter = MatchPresenter(this)

        nextMatchLeagueSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedLeagueOption = parent!!.selectedItem as LeagueOption

                (activity as MatchScheduleActivity).leagueId = selectedLeagueOption.leagueId
                (activity as MatchScheduleActivity).leagueName = selectedLeagueOption.leagueName

                nextMatchPresenter.getNextMatchInfo((activity as MatchScheduleActivity).leagueId)
            }

        }

        nextMatchSwipeRefreshLayout.onRefresh {
            nextMatchPresenter.getNextMatchInfo((activity as MatchScheduleActivity).leagueId)
        }
    }

    override fun dataIsLoading() {
        nextMatchProgressBar.visible()
        nextMatchRecyclerView.invisible()
        nextMatchErrorText.gone()
    }

    override fun dataLoadingFinished() {
        nextMatchSwipeRefreshLayout.isRefreshing = false
        nextMatchProgressBar.gone()
        nextMatchRecyclerView.visible()
        nextMatchErrorText.gone()
    }

    override fun dataFailedToLoad(errorText : String) {
        nextMatchSwipeRefreshLayout.isRefreshing = false
        nextMatchProgressBar.gone()
        nextMatchRecyclerView.invisible()
        nextMatchErrorText.visible()

        nextMatchErrorText.text = errorText
    }

    override fun showMatchData(matchList: List<MatchItem>) {
        nextMatches.clear()
        nextMatches.addAll(matchList)
        nextMatchRvAdapter.notifyDataSetChanged()
    }

    override fun onPauseFragment() {}

    override fun onResumeFragment() {
        if(::nextMatchLeagueSpinner.isInitialized){
            val selectedLeagueOption = nextMatchLeagueSpinner.selectedItem as LeagueOption

            (activity as MatchScheduleActivity).leagueId = selectedLeagueOption.leagueId
            (activity as MatchScheduleActivity).leagueName = selectedLeagueOption.leagueName
        }

    }
}