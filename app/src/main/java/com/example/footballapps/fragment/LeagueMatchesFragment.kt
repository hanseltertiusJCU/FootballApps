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

class LeagueMatchesFragment : Fragment(), MatchView, FragmentLifecycle {

    private lateinit var leagueMatchesRecyclerView : RecyclerView
    private lateinit var leagueMatchesProgressBar : ProgressBar
    private lateinit var leagueMatchesSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var leagueMatchesSpinner : Spinner
    private lateinit var leagueMatchesErrorText : TextView

    private lateinit var leagueMatchesPresenter : MatchPresenter

    private var leagueMatches : MutableList<MatchItem> = mutableListOf()
    private lateinit var leagueMatchesRvAdapter : MatchRecyclerViewAdapter

    private lateinit var leagueId : String

    private var currentPosition = 0

    private var leagueMatchSearchItem : MenuItem? = null
    private var leagueMatchSearchView : SearchView? = null

    private var isDataLoading = false
    private var isSearching = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return UI {
            // todo : perlu di bikin nested scrollview kali
            constraintLayout {
                id = R.id.league_match_parent_layout
                lparams(width = matchParent, height = matchParent)

                leagueMatchesSpinner = spinner {
                    id = R.id.league_match_spinner
                }.lparams{
                    width = matchParent
                    height = wrapContent
                    margin = dip(16)
                }

                leagueMatchesSwipeRefreshLayout = swipeRefreshLayout {

                    setColorSchemeColors(ContextCompat.getColor(context, R.color.colorAccent))

                    leagueMatchesRecyclerView = recyclerView {
                        id = R.id.rv_league_match
                        lparams(width = matchParent, height = wrapContent)
                        layoutManager = LinearLayoutManager(context)
                    }

                }.lparams {
                    width = matchConstraint
                    height = matchConstraint
                    topToBottom = R.id.league_match_spinner
                    leftToLeft = R.id.league_match_parent_layout
                    rightToRight = R.id.league_match_parent_layout
                    bottomToBottom = R.id.league_match_parent_layout
                    verticalBias = 0f
                }

                leagueMatchesProgressBar = progressBar().lparams{
                    topToTop = R.id.league_match_parent_layout
                    leftToLeft = R.id.league_match_parent_layout
                    rightToRight = R.id.league_match_parent_layout
                    bottomToBottom = R.id.league_match_parent_layout
                }

                leagueMatchesErrorText = themedTextView(R.style.text_content).lparams{
                    topToTop = R.id.league_match_parent_layout
                    leftToLeft = R.id.league_match_parent_layout
                    rightToRight = R.id.league_match_parent_layout
                    bottomToBottom = R.id.league_match_parent_layout
                }
            }
        }.view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
    }

    private fun initData(){
        leagueId = arguments?.getString("leagueId") ?: "4328"

        val matchesCategoryList = resources.getStringArray(R.array.matches_category)
        val leagueMatchesSpinnerAdapter = ArrayAdapter(activity!!.applicationContext, android.R.layout.simple_spinner_dropdown_item, matchesCategoryList)

        leagueMatchesSpinner.adapter = leagueMatchesSpinnerAdapter

        leagueMatchesRvAdapter = MatchRecyclerViewAdapter(context!!, leagueMatches) {
            startActivity<MatchDetailActivity>(
                "matchItem" to it
            )
        }

        leagueMatchesRecyclerView.adapter = leagueMatchesRvAdapter

        leagueMatchesPresenter = MatchPresenter(this, MatchesRepository())

        leagueMatchesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentPosition = position
                EspressoIdlingResource.increment()
                when (position) {
                    1 -> leagueMatchesPresenter.getNextMatchInfo(leagueId)
                    else -> leagueMatchesPresenter.getPreviousMatchInfo(leagueId)
                }
            }

        }

        leagueMatchesSwipeRefreshLayout.onRefresh {
            EspressoIdlingResource.increment()
            if(isSearching){
                leagueMatchesPresenter.getSearchMatchInfo(leagueMatchSearchView?.query.toString())
            } else {
                when (currentPosition) {
                    1 -> leagueMatchesPresenter.getNextMatchInfo(leagueId)
                    else -> leagueMatchesPresenter.getPreviousMatchInfo(leagueId)
                }
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
        leagueMatchesProgressBar.visible()
        leagueMatchesErrorText.gone()
        leagueMatchesRecyclerView.invisible()

        isDataLoading = true
    }

    override fun dataLoadingFinished() {
        if(!EspressoIdlingResource.idlingResource.isIdleNow) {
            EspressoIdlingResource.decrement()
        }
        leagueMatchesSwipeRefreshLayout.isRefreshing = false
        leagueMatchesProgressBar.gone()
        leagueMatchesErrorText.gone()
        leagueMatchesRecyclerView.visible()

        isDataLoading = false
    }

    override fun dataFailedToLoad() {
        if(!EspressoIdlingResource.idlingResource.isIdleNow) {
            EspressoIdlingResource.decrement()
        }

        leagueMatchesSwipeRefreshLayout.isRefreshing = false
        leagueMatchesProgressBar.gone()
        leagueMatchesErrorText.visible()
        leagueMatchesRecyclerView.invisible()

        val isNetworkConnected = checkNetworkConnection()
        if (isNetworkConnected) {
            leagueMatchesErrorText.text = resources.getString(R.string.no_data_to_show)
        } else {
            leagueMatchesErrorText.text = resources.getString(R.string.no_internet_connection)
        }

        isDataLoading = false

    }

    override fun showMatchesData(matchResponse: MatchResponse) {
        leagueMatches.clear()

        if(isSearching){
            val searchResultLeagueMatchesList = matchResponse.searchResultEvents
            if(searchResultLeagueMatchesList != null){
                leagueMatches.addAll(searchResultLeagueMatchesList)
            }
        } else {
            val leagueMatchesList = matchResponse.events
            if(leagueMatchesList != null){
                leagueMatches.addAll(leagueMatchesList)
            }
        }

        leagueMatchesRvAdapter.notifyDataSetChanged()
    }

    override fun onPauseFragment() {
        leagueMatchSearchItem?.collapseActionView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)

        leagueMatchSearchItem = menu.findItem(R.id.action_search)

        val leagueMatchSearchManager : SearchManager = context?.getSystemService(Context.SEARCH_SERVICE) as SearchManager

        if(leagueMatchSearchItem != null){
            leagueMatchSearchView = leagueMatchSearchItem?.actionView as SearchView

            leagueMatchSearchView?.setSearchableInfo(leagueMatchSearchManager.getSearchableInfo(activity?.componentName))

            leagueMatchSearchItem?.setOnActionExpandListener(object  : MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(menuItem: MenuItem?): Boolean {
                    isSearching = true
                    leagueMatchesSpinner.gone()
                    EspressoIdlingResource.increment()
                    leagueMatchesPresenter.getSearchMatchInfo(leagueMatchSearchView?.query.toString())
                    return true
                }

                override fun onMenuItemActionCollapse(menuItem: MenuItem?): Boolean {
                    isSearching = false
                    leagueMatchesSpinner.visible()
                    EspressoIdlingResource.increment()
                    when (currentPosition) {
                        1 -> leagueMatchesPresenter.getNextMatchInfo(leagueId)
                        else -> leagueMatchesPresenter.getPreviousMatchInfo(leagueId)
                    }
                    return true
                }

            })

            leagueMatchSearchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if(!isDataLoading){
                        EspressoIdlingResource.increment()
                        leagueMatchesPresenter.getSearchMatchInfo(query!!)
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