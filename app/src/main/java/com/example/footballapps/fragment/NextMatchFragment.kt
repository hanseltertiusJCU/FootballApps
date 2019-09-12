package com.example.footballapps.fragment

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.footballapps.R
import com.example.footballapps.activity.FootballGameInfoActivity
import com.example.footballapps.activity.LeagueDetailActivity
import com.example.footballapps.activity.MatchDetailActivity
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

    private lateinit var nextMatchRecyclerView: RecyclerView
    private lateinit var nextMatchProgressBar: ProgressBar
    private lateinit var nextMatchSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var nextMatchLeagueSpinner: Spinner
    private lateinit var nextMatchErrorText: TextView

    private lateinit var nextMatchPresenter: MatchPresenter

    private var nextMatches: MutableList<MatchItem> = mutableListOf()
    private lateinit var nextMatchRvAdapter: MatchRecyclerViewAdapter

    private lateinit var nextMatchLeagueId: String
    private lateinit var nextMatchLeagueName: String

    var nextMatchSearchItem: MenuItem? = null
    private var nextMatchSearchView: SearchView? = null

    private var isDataLoading = false
    private var isSearching = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return UI {
            constraintLayout {
                id = R.id.next_match_parent_layout
                lparams(width = matchParent, height = matchParent)

                nextMatchLeagueSpinner = spinner {
                    id = R.id.next_match_league_spinner
                }.lparams {
                    width = matchParent
                    height = wrapContent
                    margin = dip(16)
                }

                nextMatchSwipeRefreshLayout = swipeRefreshLayout {

                    setColorSchemeColors(ContextCompat.getColor(context, R.color.colorAccent))

                    nextMatchRecyclerView = recyclerView {
                        lparams(width = matchParent, height = wrapContent)
                        layoutManager = LinearLayoutManager(context)
                    }

                }.lparams {
                    width = matchConstraint
                    height = matchConstraint
                    topToBottom = R.id.next_match_league_spinner
                    leftToLeft = R.id.next_match_parent_layout
                    rightToRight = R.id.next_match_parent_layout
                    bottomToBottom = R.id.next_match_parent_layout
                    verticalBias = 0f
                }

                nextMatchProgressBar = progressBar().lparams {
                    topToTop = R.id.next_match_parent_layout
                    leftToLeft = R.id.next_match_parent_layout
                    rightToRight = R.id.next_match_parent_layout
                    bottomToBottom = R.id.next_match_parent_layout
                }

                nextMatchErrorText = themedTextView(R.style.text_content).lparams {
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

        for (i in leagueIdList.indices) {
            leagueOptions.add(LeagueOption(leagueIdList[i], leagueNameList[i]))
        }

        val spinnerAdapter = ArrayAdapter(
            activity!!.applicationContext,
            android.R.layout.simple_spinner_dropdown_item,
            leagueOptions
        )
        nextMatchLeagueSpinner.adapter = spinnerAdapter

        nextMatchRvAdapter = MatchRecyclerViewAdapter(context!!, nextMatches) {
            startActivity<MatchDetailActivity>(
                "eventId" to it.idEvent,
                "eventName" to it.strEvent,
                "homeTeamId" to it.homeTeamId,
                "awayTeamId" to it.awayTeamId
            )
        }
        nextMatchRecyclerView.adapter = nextMatchRvAdapter

        nextMatchPresenter = MatchPresenter(this)

        nextMatchLeagueSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {}

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedLeagueOption = parent!!.selectedItem as LeagueOption

                    nextMatchLeagueId = selectedLeagueOption.leagueId
                    nextMatchLeagueName = selectedLeagueOption.leagueName

                    (activity as FootballGameInfoActivity).leagueId = nextMatchLeagueId
                    (activity as FootballGameInfoActivity).leagueName = nextMatchLeagueName

                    nextMatchPresenter.getNextMatchInfo((activity as FootballGameInfoActivity).leagueId)
                }

            }

        nextMatchSwipeRefreshLayout.onRefresh {
            if (!isSearching) {
                nextMatchPresenter.getNextMatchInfo(nextMatchLeagueId)
            } else {
                nextMatchPresenter.getSearchMatchInfo(nextMatchSearchView?.query.toString())
            }

        }
    }

    override fun dataIsLoading() {
        nextMatchProgressBar.visible()
        nextMatchRecyclerView.invisible()
        nextMatchErrorText.gone()

        isDataLoading = true
    }

    override fun dataLoadingFinished() {
        nextMatchSwipeRefreshLayout.isRefreshing = false
        nextMatchProgressBar.gone()
        nextMatchRecyclerView.visible()
        nextMatchErrorText.gone()

        isDataLoading = false
    }

    override fun dataFailedToLoad(errorText: String) {
        nextMatchSwipeRefreshLayout.isRefreshing = false
        nextMatchProgressBar.gone()
        nextMatchRecyclerView.invisible()
        nextMatchErrorText.visible()

        isDataLoading = false

        nextMatchErrorText.text = errorText
    }

    override fun showMatchData(matchList: List<MatchItem>) {
        nextMatches.clear()
        nextMatches.addAll(matchList)
        nextMatchRvAdapter.notifyDataSetChanged()
    }

    override fun onPauseFragment() {
        nextMatchSearchItem?.collapseActionView()
    }

    override fun onResumeFragment() {
        if (::nextMatchLeagueSpinner.isInitialized) {
            val selectedLeagueOption = nextMatchLeagueSpinner.selectedItem as LeagueOption

            nextMatchLeagueId = selectedLeagueOption.leagueId
            nextMatchLeagueName = selectedLeagueOption.leagueName

            (activity as FootballGameInfoActivity).leagueId = nextMatchLeagueId
            (activity as FootballGameInfoActivity).leagueName = nextMatchLeagueName
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_search_with_info, menu)

        nextMatchSearchItem = menu?.findItem(R.id.action_search)

        val nextMatchLeagueInfoItem = menu?.findItem(R.id.action_info)

        val nextMatchSearchManager: SearchManager =
            context?.getSystemService(Context.SEARCH_SERVICE) as SearchManager

        if (nextMatchSearchItem != null) {
            nextMatchSearchView = nextMatchSearchItem?.actionView as SearchView

            nextMatchSearchView?.setSearchableInfo(nextMatchSearchManager.getSearchableInfo(activity?.componentName))

            nextMatchSearchItem?.setOnActionExpandListener(object :
                MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                    isSearching = true
                    nextMatchLeagueSpinner.gone()
                    nextMatchPresenter.getSearchMatchInfo(nextMatchSearchView?.query.toString())
                    nextMatchLeagueInfoItem?.isVisible = false
                    return true
                }

                override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                    isSearching = false
                    nextMatchLeagueSpinner.visible()
                    nextMatchPresenter.getNextMatchInfo(nextMatchLeagueId)
                    activity?.invalidateOptionsMenu()
                    return true
                }

            })

            nextMatchSearchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (!isDataLoading) {
                        nextMatchPresenter.getSearchMatchInfo(query!!)
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }

            })
        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.action_info) {
            startActivity<LeagueDetailActivity>(
                "leagueName" to nextMatchLeagueName,
                "leagueId" to nextMatchLeagueId
            )
            (activity as FootballGameInfoActivity).finish()
        }
        return super.onOptionsItemSelected(item)
    }
}