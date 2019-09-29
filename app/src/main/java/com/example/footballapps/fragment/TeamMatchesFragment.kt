package com.example.footballapps.fragment


import android.app.SearchManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
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
import com.example.footballapps.activity.MatchDetailActivity
import com.example.footballapps.activity.TeamDetailActivity
import com.example.footballapps.adapter.MatchRecyclerViewAdapter
import com.example.footballapps.espresso.EspressoIdlingResource
import com.example.footballapps.lifecycle.FragmentLifecycle
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
import org.jetbrains.anko.support.v4.*

class TeamMatchesFragment : Fragment(), MatchView, FragmentLifecycle {

    private lateinit var teamMatchesRecyclerView: RecyclerView
    private lateinit var teamMatchesProgressBar: ProgressBar
    private lateinit var teamMatchesSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var teamMatchesSpinner: Spinner
    private lateinit var teamMatchesErrorText: TextView

    private lateinit var teamMatchesPresenter: MatchPresenter

    private var teamMatches: MutableList<MatchItem> = mutableListOf()
    private lateinit var teamMatchesRvAdapter: MatchRecyclerViewAdapter

    private lateinit var teamId: String

    private var currentPosition = 0

    private var teamMatchSearchItem: MenuItem? = null
    private var teamMatchSearchView: SearchView? = null

    private var isDataLoading = false
    private var isSearching = false

    private lateinit var teamDetailActivity: TeamDetailActivity

    private var searchQuery: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return UI {
            teamMatchesSwipeRefreshLayout = swipeRefreshLayout {

                layoutParams = ViewGroup.LayoutParams(matchParent, matchParent)

                setColorSchemeColors(ContextCompat.getColor(context, R.color.colorAccent))

                nestedScrollView {

                    lparams(width = matchParent, height = matchParent)
                    isFillViewport = true

                    constraintLayout {
                        id = R.id.team_matches_parent_layout
                        lparams(width = matchParent, height = matchParent)

                        teamMatchesSpinner = spinner {
                            id = R.id.team_match_spinner
                        }.lparams {
                            width = matchParent
                            height = wrapContent
                            margin = dip(16)
                        }

                        teamMatchesRecyclerView = recyclerView {
                            id = R.id.rv_team_match
                            layoutManager = LinearLayoutManager(context)
                        }.lparams {
                            width = matchConstraint
                            height = matchConstraint
                            topToBottom = R.id.team_match_spinner
                            leftToLeft = R.id.team_matches_parent_layout
                            rightToRight = R.id.team_matches_parent_layout
                            bottomToBottom = R.id.team_matches_parent_layout
                            verticalBias = 0f
                        }

                        teamMatchesProgressBar = progressBar().lparams {
                            topToTop = R.id.team_matches_parent_layout
                            leftToLeft = R.id.team_matches_parent_layout
                            rightToRight = R.id.team_matches_parent_layout
                            bottomToBottom = R.id.team_matches_parent_layout
                        }

                        teamMatchesErrorText = themedTextView(R.style.text_content).lparams {
                            topToTop = R.id.team_matches_parent_layout
                            leftToLeft = R.id.team_matches_parent_layout
                            rightToRight = R.id.team_matches_parent_layout
                            bottomToBottom = R.id.team_matches_parent_layout
                        }

                    }
                }

            }
        }.view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
    }

    private fun initData() {

        teamDetailActivity = activity as TeamDetailActivity

        teamId = arguments?.getString("teamId") ?: "133604"

        val matchesCategoryList = resources.getStringArray(R.array.matches_category)
        val teamMatchesSpinnerAdapter = ArrayAdapter(
            activity!!.applicationContext,
            android.R.layout.simple_spinner_dropdown_item,
            matchesCategoryList
        )

        teamMatchesSpinner.adapter = teamMatchesSpinnerAdapter

        teamMatchesRvAdapter = MatchRecyclerViewAdapter(context!!, teamMatches) {
            startActivity<MatchDetailActivity>(
                "matchItem" to it
            )
        }

        teamMatchesRecyclerView.adapter = teamMatchesRvAdapter

        teamMatchesPresenter = MatchPresenter(this, MatchesRepository())

        teamMatchesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                currentPosition = position
                EspressoIdlingResource.increment()
                when (position) {
                    1 -> teamMatchesPresenter.getTeamNextMatchInfo(teamId)
                    else -> teamMatchesPresenter.getTeamLastMatchInfo(teamId)
                }
            }

        }

        teamMatchesSwipeRefreshLayout.onRefresh {
            EspressoIdlingResource.increment()
            if (isSearching) {
                teamMatchesPresenter.getSearchMatchInfo(searchQuery)
            } else {
                when (currentPosition) {
                    1 -> teamMatchesPresenter.getTeamNextMatchInfo(teamId)
                    else -> teamMatchesPresenter.getTeamLastMatchInfo(teamId)
                }
            }

        }


    }

    override fun dataIsLoading() {
        teamMatchesProgressBar.visible()
        teamMatchesErrorText.gone()
        teamMatchesRecyclerView.invisible()

        isDataLoading = true
    }

    override fun dataLoadingFinished() {
        if (!EspressoIdlingResource.idlingResource.isIdleNow) {
            EspressoIdlingResource.decrement()
        }
        teamMatchesSwipeRefreshLayout.isRefreshing = false
        teamMatchesProgressBar.gone()
        teamMatchesErrorText.gone()
        teamMatchesRecyclerView.visible()

        isDataLoading = false
    }

    override fun dataFailedToLoad() {
        if (!EspressoIdlingResource.idlingResource.isIdleNow) {
            EspressoIdlingResource.decrement()
        }
        teamMatchesSwipeRefreshLayout.isRefreshing = false
        teamMatchesProgressBar.gone()
        teamMatchesErrorText.visible()
        teamMatchesRecyclerView.invisible()

        val isNetworkConnected = checkNetworkConnection()
        if (isNetworkConnected) {
            teamMatchesErrorText.text = resources.getString(R.string.no_data_to_show)
        } else {
            teamMatchesErrorText.text = resources.getString(R.string.no_internet_connection)
        }

        isDataLoading = false
    }

    override fun showMatchesData(matchResponse: MatchResponse) {
        teamMatches.clear()

        if (isSearching) {
            val searchResultTeamMatchesList = matchResponse.searchResultEvents
            if (searchResultTeamMatchesList != null) {
                teamMatches.addAll(searchResultTeamMatchesList)
            }
        } else {
            when (currentPosition) {
                1 -> {
                    val teamMatchesList = matchResponse.events
                    if (teamMatchesList != null) {
                        teamMatches.addAll(teamMatchesList)
                    }
                }
                else -> {
                    val teamMatchesList = matchResponse.results
                    if (teamMatchesList != null) {
                        teamMatches.addAll(teamMatchesList)
                    }
                }
            }
        }


        teamMatchesRvAdapter.notifyDataSetChanged()
    }

    override fun onPauseFragment() {
        teamMatchSearchItem?.collapseActionView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)

        teamMatchSearchItem = menu.findItem(R.id.action_search)

        val teamMatchSearchManager: SearchManager =
            context?.getSystemService(Context.SEARCH_SERVICE) as SearchManager

        if (teamMatchSearchItem != null) {
            teamMatchSearchView = teamMatchSearchItem?.actionView as SearchView

            teamMatchSearchView?.setSearchableInfo(teamMatchSearchManager.getSearchableInfo(activity?.componentName))

            teamMatchSearchItem?.setOnActionExpandListener(object :
                MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(menuItem: MenuItem?): Boolean {
                    isSearching = true
                    teamMatchesSpinner.gone()
                    EspressoIdlingResource.increment()
                    teamDetailActivity.favoriteMenuItem?.isVisible = false
                    teamMatchesPresenter.getSearchMatchInfo(searchQuery)
                    return true
                }

                override fun onMenuItemActionCollapse(menuItem: MenuItem?): Boolean {
                    isSearching = false
                    teamMatchesSpinner.visible()
                    searchQuery = ""
                    EspressoIdlingResource.increment()
                    when (currentPosition) {
                        1 -> teamMatchesPresenter.getTeamNextMatchInfo(teamId)
                        else -> teamMatchesPresenter.getTeamLastMatchInfo(teamId)
                    }
                    teamDetailActivity.invalidateOptionsMenu()
                    return true
                }

            })

            teamMatchSearchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    if (!isDataLoading) {
                        searchQuery = query
                        EspressoIdlingResource.increment()
                        teamMatchesPresenter.getSearchMatchInfo(query)
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

    @Suppress("DEPRECATION")
    private fun checkNetworkConnection(): Boolean {

        val connectivityManager: ConnectivityManager? =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT < 23) {
                val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo

                if (networkInfo != null) {
                    return (networkInfo.isConnected && (networkInfo.type == ConnectivityManager.TYPE_WIFI || networkInfo.type == ConnectivityManager.TYPE_MOBILE || networkInfo.type == ConnectivityManager.TYPE_VPN))
                }

            } else {
                val network: Network? = connectivityManager.activeNetwork

                if (network != null) {
                    val networkCapabilities: NetworkCapabilities =
                        connectivityManager.getNetworkCapabilities(network)!!


                    return (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                            || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                            || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN))
                }
            }
        }
        return false
    }


}
