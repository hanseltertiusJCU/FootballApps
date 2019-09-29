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
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.footballapps.R
import com.example.footballapps.activity.TeamDetailActivity
import com.example.footballapps.adapter.TeamRecyclerViewAdapter
import com.example.footballapps.espresso.EspressoIdlingResource
import com.example.footballapps.lifecycle.FragmentLifecycle
import com.example.footballapps.model.TeamItem
import com.example.footballapps.model.TeamResponse
import com.example.footballapps.presenter.TeamsPresenter
import com.example.footballapps.repository.TeamsRepository
import com.example.footballapps.utils.GridSpacingItemDecoration
import com.example.footballapps.utils.gone
import com.example.footballapps.utils.invisible
import com.example.footballapps.utils.visible
import com.example.footballapps.view.TeamsView
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.constraint.layout.matchConstraint
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.progressBar
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.*
import org.jetbrains.anko.themedTextView
import org.jetbrains.anko.wrapContent

class LeagueTeamsFragment : Fragment(), TeamsView, FragmentLifecycle{

    private lateinit var leagueTeamsRecyclerView : RecyclerView
    private lateinit var leagueTeamsProgressBar : ProgressBar
    private lateinit var leagueTeamsSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var leagueTeamsErrorText : TextView

    private lateinit var leagueTeamsPresenter : TeamsPresenter

    private var leagueTeams : MutableList<TeamItem> = mutableListOf()
    private lateinit var leagueTeamsRvAdapter : TeamRecyclerViewAdapter

    private lateinit var leagueId : String

    private var leagueTeamSearchItem : MenuItem? = null
    private var leagueTeamSearchView : SearchView? = null

    private var isDataLoading = false
    private var isSearching = false

    private val spanCount = 2
    private val includeEdge = true


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return UI {

            leagueTeamsSwipeRefreshLayout = swipeRefreshLayout {

                layoutParams = ViewGroup.LayoutParams(matchParent, matchParent)

                setColorSchemeColors(ContextCompat.getColor(context, R.color.colorAccent))

                nestedScrollView {
                    lparams(width = matchParent, height = matchParent)
                    isFillViewport = true

                    constraintLayout {
                        id = R.id.league_teams_parent_layout
                        lparams(width = matchParent, height = matchParent)

                        leagueTeamsRecyclerView = recyclerView {
                            id = R.id.rv_league_teams
                        }.lparams{
                            width = matchConstraint
                            height = matchConstraint
                            topToTop = R.id.league_teams_parent_layout
                            leftToLeft = R.id.league_teams_parent_layout
                            rightToRight = R.id.league_teams_parent_layout
                            bottomToBottom = R.id.league_teams_parent_layout
                            verticalBias = 0f
                        }

                        leagueTeamsProgressBar = progressBar().lparams{
                            topToTop = R.id.league_teams_parent_layout
                            leftToLeft = R.id.league_teams_parent_layout
                            rightToRight = R.id.league_teams_parent_layout
                            bottomToBottom = R.id.league_teams_parent_layout
                        }

                        leagueTeamsErrorText = themedTextView(R.style.text_content).lparams{
                            topToTop = R.id.league_teams_parent_layout
                            leftToLeft = R.id.league_teams_parent_layout
                            rightToRight = R.id.league_teams_parent_layout
                            bottomToBottom = R.id.league_teams_parent_layout
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
        leagueId = arguments?.getString("leagueId") ?: "4328"

        leagueTeamsRvAdapter = TeamRecyclerViewAdapter(leagueTeams) {
            startActivity<TeamDetailActivity>("teamItem" to it)
        }

        leagueTeamsRecyclerView.layoutManager = GridLayoutManager(context, spanCount)

        leagueTeamsRecyclerView.adapter = leagueTeamsRvAdapter

        leagueTeamsRecyclerView.addItemDecoration(GridSpacingItemDecoration(spanCount = spanCount, space = dip(16), includeEdge = includeEdge))

        leagueTeamsPresenter = TeamsPresenter(this, TeamsRepository())

        EspressoIdlingResource.increment()
        leagueTeamsPresenter.getTeamsInfo(leagueId)

        leagueTeamsSwipeRefreshLayout.onRefresh {
            EspressoIdlingResource.increment()
            if(isSearching){
                leagueTeamsPresenter.getSearchTeamsInfo(leagueTeamSearchView?.query.toString())
            } else {
                leagueTeamsPresenter.getTeamsInfo(leagueId)
            }


        }


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

    override fun dataIsLoading() {
        leagueTeamsProgressBar.visible()
        leagueTeamsErrorText.gone()
        leagueTeamsRecyclerView.invisible()

        isDataLoading = true
    }

    override fun dataLoadingFinished() {
        if(!EspressoIdlingResource.idlingResource.isIdleNow){
            EspressoIdlingResource.decrement()
        }

        leagueTeamsSwipeRefreshLayout.isRefreshing = false
        leagueTeamsProgressBar.gone()
        leagueTeamsErrorText.gone()
        leagueTeamsRecyclerView.visible()

        isDataLoading = false
    }

    override fun dataFailedToLoad() {
        if(!EspressoIdlingResource.idlingResource.isIdleNow){
            EspressoIdlingResource.decrement()
        }
        leagueTeamsSwipeRefreshLayout.isRefreshing = false
        leagueTeamsProgressBar.gone()
        leagueTeamsErrorText.visible()
        leagueTeamsRecyclerView.invisible()

        val isNetworkConnected = checkNetworkConnection()
        if(isNetworkConnected){
            leagueTeamsErrorText.text = resources.getString(R.string.no_data_to_show)
        } else {
            leagueTeamsErrorText.text = resources.getString(R.string.no_internet_connection)
        }

        isDataLoading = false
    }

    override fun showTeamsData(teamsResponse: TeamResponse) {
        leagueTeams.clear()
        val teamsList = teamsResponse.teams
        if(teamsList != null){
            leagueTeams.addAll(teamsList)
        }
        leagueTeamsRvAdapter.notifyDataSetChanged()
    }

    override fun onPauseFragment() {
        leagueTeamSearchItem?.collapseActionView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)

        leagueTeamSearchItem = menu.findItem(R.id.action_search)

        val leagueTeamSearchManager : SearchManager = context?.getSystemService(Context.SEARCH_SERVICE) as SearchManager

        if(leagueTeamSearchItem != null){
            leagueTeamSearchView = leagueTeamSearchItem?.actionView as SearchView

            leagueTeamSearchView?.setSearchableInfo(leagueTeamSearchManager.getSearchableInfo(activity?.componentName))

            leagueTeamSearchItem?.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(menuItem: MenuItem?): Boolean {
                    isSearching = true
                    EspressoIdlingResource.increment()
                    leagueTeamsPresenter.getSearchTeamsInfo(leagueTeamSearchView?.query.toString())
                    return true
                }

                override fun onMenuItemActionCollapse(menuItem: MenuItem?): Boolean {
                    isSearching = false
                    EspressoIdlingResource.increment()
                    leagueTeamsPresenter.getTeamsInfo(leagueId)
                    return true
                }

            })

            leagueTeamSearchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if(!isDataLoading){
                        EspressoIdlingResource.increment()
                        leagueTeamsPresenter.getSearchTeamsInfo(query!!)
                    }
                    return true
                }

                override fun onQueryTextChange(query: String?): Boolean {
                    return false
                }

            })
        }

        super.onCreateOptionsMenu(menu, inflater)
    }


}