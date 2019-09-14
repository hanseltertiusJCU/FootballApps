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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.footballapps.R
import com.example.footballapps.activity.MatchDetailActivity
import com.example.footballapps.adapter.FavoriteMatchRecyclerViewAdapter
import com.example.footballapps.favorite.FavoriteMatchItem
import com.example.footballapps.presenter.FavoriteMatchPresenter
import com.example.footballapps.utils.gone
import com.example.footballapps.utils.invisible
import com.example.footballapps.utils.visible
import com.example.footballapps.view.FavoriteMatchView
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.constraint.layout.matchConstraint
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.onRefresh
import org.jetbrains.anko.support.v4.swipeRefreshLayout


class FavoriteMatchFragment : Fragment(), AnkoComponent<Context>, FavoriteMatchView {

    private lateinit var favoriteMatchRecyclerView: RecyclerView
    private lateinit var favoriteMatchProgressBar: ProgressBar
    private lateinit var favoriteMatchSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var favoriteMatchErrorText: TextView

    private var favoriteMatches: MutableList<FavoriteMatchItem> = mutableListOf()
    private lateinit var favoriteMatchRvAdapter: FavoriteMatchRecyclerViewAdapter

    private lateinit var favoriteMatchPresenter: FavoriteMatchPresenter

    private var favoriteMatchSearchView: SearchView? = null
    private var favoriteMatchSearchItem: MenuItem? = null

    private var isDataLoading = false
    private var isSearching = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return createView(AnkoContext.create(requireContext()))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initData()
    }

    private fun initData() {

        favoriteMatchRvAdapter = FavoriteMatchRecyclerViewAdapter(context!!, favoriteMatches) {
            context?.startActivity<MatchDetailActivity>(
                "eventId" to it.idEvent,
                "eventName" to it.strEvent,
                "homeTeamId" to it.homeTeamId,
                "awayTeamId" to it.awayTeamId
            )
        }

        favoriteMatchRecyclerView.adapter = favoriteMatchRvAdapter

        favoriteMatchPresenter = FavoriteMatchPresenter(this, context!!)

        favoriteMatchSwipeRefreshLayout.onRefresh {
            if (isSearching) {
                getFavoriteDataFromQuery(favoriteMatchSearchView?.query.toString())
            } else {
                getFavoriteData()
            }

        }
    }

    override fun onResume() {
        super.onResume()
        if (isSearching) {
            getFavoriteDataFromQuery(favoriteMatchSearchView?.query.toString())
        } else {
            getFavoriteData()
        }
    }

    override fun createView(ui: AnkoContext<Context>): View = with(ui) {
        constraintLayout {
            id = R.id.favorite_match_parent_layout
            lparams(width = matchParent, height = matchParent)

            favoriteMatchSwipeRefreshLayout = swipeRefreshLayout {

                setColorSchemeColors(ContextCompat.getColor(context, R.color.colorAccent))

                favoriteMatchRecyclerView = recyclerView {
                    lparams(width = matchParent, height = wrapContent)
                    layoutManager = LinearLayoutManager(context)
                }
            }.lparams {
                width = matchConstraint
                height = matchConstraint
                topToTop = R.id.favorite_match_parent_layout
                leftToLeft = R.id.favorite_match_parent_layout
                rightToRight = R.id.favorite_match_parent_layout
                bottomToBottom = R.id.favorite_match_parent_layout
                verticalBias = 0f
            }

            favoriteMatchProgressBar = progressBar().lparams {
                topToTop = R.id.favorite_match_parent_layout
                leftToLeft = R.id.favorite_match_parent_layout
                rightToRight = R.id.favorite_match_parent_layout
                bottomToBottom = R.id.favorite_match_parent_layout
            }

            favoriteMatchErrorText = themedTextView(R.style.text_content).lparams {
                topToTop = R.id.favorite_match_parent_layout
                leftToLeft = R.id.favorite_match_parent_layout
                rightToRight = R.id.favorite_match_parent_layout
                bottomToBottom = R.id.favorite_match_parent_layout
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
        favoriteMatchProgressBar.visible()
        favoriteMatchErrorText.gone()
        favoriteMatchRecyclerView.invisible()

        isDataLoading = true
    }

    override fun dataLoadingFinished() {
        favoriteMatchSwipeRefreshLayout.isRefreshing = false
        favoriteMatchProgressBar.gone()
        favoriteMatchErrorText.gone()
        favoriteMatchRecyclerView.visible()

        isDataLoading = false
    }

    override fun dataFailedToLoad(errorText: String) {
        favoriteMatchSwipeRefreshLayout.isRefreshing = false
        favoriteMatchProgressBar.gone()
        favoriteMatchErrorText.visible()
        favoriteMatchRecyclerView.invisible()

        isDataLoading = false

        favoriteMatchErrorText.text = errorText
    }

    override fun showMatchData(favoriteMatchList: List<FavoriteMatchItem>) {
        favoriteMatches.clear()
        favoriteMatches.addAll(favoriteMatchList)
        favoriteMatchRvAdapter.notifyDataSetChanged()
    }

    private fun getFavoriteData() {
        val isNetworkConnected = checkNetworkConnection()
        favoriteMatchPresenter.getFavoriteMatchInfo(isNetworkConnected)
    }

    private fun getFavoriteDataFromQuery(query: String) {
        val isNetworkConnected = checkNetworkConnection()
        favoriteMatchPresenter.getFavoriteMatchInfoSearchResult(
            isNetworkConnected,
            query
        )
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            favoriteMatchSearchItem?.collapseActionView()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {

        inflater?.inflate(R.menu.menu_search, menu)

        favoriteMatchSearchItem = menu!!.findItem(R.id.action_search)

        val favoriteSearchManager: SearchManager =
            context?.getSystemService(Context.SEARCH_SERVICE) as SearchManager

        if (favoriteMatchSearchItem != null) {
            favoriteMatchSearchView = favoriteMatchSearchItem?.actionView as SearchView

            favoriteMatchSearchItem?.setOnActionExpandListener(object :
                MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                    isSearching = true
                    getFavoriteDataFromQuery(favoriteMatchSearchView?.query.toString())
                    return true
                }

                override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                    isSearching = false
                    getFavoriteData()
                    return true
                }

            })

            favoriteMatchSearchView?.setOnQueryTextListener(object :
                SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (!isDataLoading) {
                        getFavoriteDataFromQuery(query!!)
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }

            })


        }

        favoriteMatchSearchView?.setSearchableInfo(favoriteSearchManager.getSearchableInfo(activity?.componentName))

        super.onCreateOptionsMenu(menu, inflater)
    }

}
