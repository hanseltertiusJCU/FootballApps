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
import com.example.footballapps.model.MatchResponse
import com.example.footballapps.presenter.MatchPresenter
import com.example.footballapps.repository.MatchesRepository
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


class LastMatchFragment : Fragment(), MatchView, FragmentLifecycle {

    private lateinit var lastMatchRecyclerView: RecyclerView
    private lateinit var lastMatchProgressBar: ProgressBar
    private lateinit var lastMatchSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var lastMatchLeagueSpinner: Spinner
    private lateinit var lastMatchErrorText: TextView

    private lateinit var lastMatchPresenter: MatchPresenter

    private var lastMatches: MutableList<MatchItem> = mutableListOf()
    private lateinit var lastMatchRvAdapter: MatchRecyclerViewAdapter

    private lateinit var lastMatchLeagueId: String
    private lateinit var lastMatchLeagueName: String

    var lastMatchSearchItem: MenuItem? = null
    private var lastMatchSearchView: SearchView? = null

    private var isDataLoading = false
    private var isSearching = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        return UI {
            constraintLayout {
                id = R.id.last_match_parent_layout
                lparams(width = matchParent, height = matchParent)

                lastMatchLeagueSpinner = spinner {
                    id = R.id.last_match_league_spinner
                }.lparams {
                    width = matchParent
                    height = wrapContent
                    margin = dip(16)
                }

                lastMatchSwipeRefreshLayout = swipeRefreshLayout {

                    setColorSchemeColors(ContextCompat.getColor(context, R.color.colorAccent))

                    lastMatchRecyclerView = recyclerView {
                        id = R.id.rv_last_match
                        lparams(width = matchParent, height = wrapContent)
                        layoutManager = LinearLayoutManager(context)
                    }

                }.lparams {
                    width = matchConstraint
                    height = matchConstraint
                    topToBottom = R.id.last_match_league_spinner
                    leftToLeft = R.id.last_match_parent_layout
                    rightToRight = R.id.last_match_parent_layout
                    bottomToBottom = R.id.last_match_parent_layout
                    verticalBias = 0f
                }

                lastMatchProgressBar = progressBar().lparams {
                    topToTop = R.id.last_match_parent_layout
                    leftToLeft = R.id.last_match_parent_layout
                    rightToRight = R.id.last_match_parent_layout
                    bottomToBottom = R.id.last_match_parent_layout
                }

                lastMatchErrorText = themedTextView(R.style.text_content).lparams {
                    topToTop = R.id.last_match_parent_layout
                    leftToLeft = R.id.last_match_parent_layout
                    rightToRight = R.id.last_match_parent_layout
                    bottomToBottom = R.id.last_match_parent_layout
                }

            }
        }.view
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
        lastMatchLeagueSpinner.adapter = spinnerAdapter

        var selectedLeagueOption = LeagueOption(
            (activity as FootballGameInfoActivity).leagueId,
            (activity as FootballGameInfoActivity).leagueName
        )
        lastMatchLeagueSpinner.setSelection(spinnerAdapter.getPosition(selectedLeagueOption))

        lastMatchRvAdapter = MatchRecyclerViewAdapter(context!!, lastMatches) {
            startActivity<MatchDetailActivity>(
                "eventId" to it.idEvent,
                "eventName" to it.strEvent,
                "homeTeamId" to it.homeTeamId,
                "awayTeamId" to it.awayTeamId
            )
        }
        lastMatchRecyclerView.adapter = lastMatchRvAdapter

        lastMatchPresenter = MatchPresenter(this, MatchesRepository())

        lastMatchLeagueSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedLeagueOption = parent!!.selectedItem as LeagueOption

                    lastMatchLeagueId = selectedLeagueOption.leagueId
                    lastMatchLeagueName = selectedLeagueOption.leagueName

                    (activity as FootballGameInfoActivity).leagueId = lastMatchLeagueId
                    (activity as FootballGameInfoActivity).leagueName = lastMatchLeagueName

                    lastMatchPresenter.getPreviousMatchInfo(lastMatchLeagueId)

                }
            }

        lastMatchSwipeRefreshLayout.onRefresh {
            if (!isSearching) {
                lastMatchPresenter.getPreviousMatchInfo(lastMatchLeagueId)
            } else {
                lastMatchPresenter.getSearchMatchInfo(lastMatchSearchView?.query.toString())
            }

        }
    }

    override fun dataIsLoading() {
        lastMatchProgressBar.visible()
        lastMatchErrorText.gone()
        lastMatchRecyclerView.invisible()

        isDataLoading = true
    }

    override fun dataLoadingFinished() {
        lastMatchSwipeRefreshLayout.isRefreshing = false
        lastMatchProgressBar.gone()
        lastMatchErrorText.gone()
        lastMatchRecyclerView.visible()

        isDataLoading = false
    }

    override fun dataFailedToLoad() {
        lastMatchSwipeRefreshLayout.isRefreshing = false
        lastMatchProgressBar.gone()
        lastMatchErrorText.visible()
        lastMatchRecyclerView.invisible()

        isDataLoading = false

        lastMatchErrorText.text = resources.getString(R.string.no_data_to_show)
    }

    override fun showMatchesData(matchResponse: MatchResponse) {
        lastMatches.clear()
        if (isSearching) {
            val searchResultMatchesList = matchResponse.searchResultEvents
            if(searchResultMatchesList != null){
                lastMatches.addAll(searchResultMatchesList)
            }
        } else {
            val lastMatchesList = matchResponse.events
            if (lastMatchesList != null) {
                lastMatches.addAll(lastMatchesList)
            }
        }
        lastMatchRvAdapter.notifyDataSetChanged()
    }

    override fun onPauseFragment() {
        lastMatchSearchItem?.collapseActionView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
    }

    override fun onResumeFragment() {

        if (::lastMatchLeagueSpinner.isInitialized) {
            val selectedLeagueOption = lastMatchLeagueSpinner.selectedItem as LeagueOption

            lastMatchLeagueId = selectedLeagueOption.leagueId
            lastMatchLeagueName = selectedLeagueOption.leagueName

            (activity as FootballGameInfoActivity).leagueId = lastMatchLeagueId
            (activity as FootballGameInfoActivity).leagueName = lastMatchLeagueName
        }

    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        lastMatchLeagueId = (activity as FootballGameInfoActivity).leagueId
        lastMatchLeagueName = (activity as FootballGameInfoActivity).leagueName
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_search_with_info, menu)

        lastMatchSearchItem = menu?.findItem(R.id.action_search)

        val lastMatchLeagueInfoItem = menu?.findItem(R.id.action_info)

        val lastMatchSearchManager: SearchManager =
            context?.getSystemService(Context.SEARCH_SERVICE) as SearchManager

        if (lastMatchSearchItem != null) {
            lastMatchSearchView = lastMatchSearchItem?.actionView as SearchView

            lastMatchSearchView?.setSearchableInfo(lastMatchSearchManager.getSearchableInfo(activity?.componentName))

            lastMatchSearchItem?.setOnActionExpandListener(object :
                MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                    isSearching = true
                    lastMatchLeagueSpinner.gone()
                    lastMatchPresenter.getSearchMatchInfo(lastMatchSearchView?.query.toString())
                    lastMatchLeagueInfoItem?.isVisible = false
                    return true
                }

                override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                    isSearching = false
                    lastMatchLeagueSpinner.visible()
                    lastMatchPresenter.getPreviousMatchInfo(lastMatchLeagueId)
                    activity?.invalidateOptionsMenu()
                    return true
                }

            })

            lastMatchSearchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (!isDataLoading) {
                        lastMatchPresenter.getSearchMatchInfo(query!!)
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
                "leagueName" to lastMatchLeagueName,
                "leagueId" to lastMatchLeagueId
            )
            (activity as FootballGameInfoActivity).finish()
        }
        return super.onOptionsItemSelected(item)
    }


}
