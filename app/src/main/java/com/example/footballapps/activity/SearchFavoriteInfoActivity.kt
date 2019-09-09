package com.example.footballapps.activity

import android.app.SearchManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.footballapps.R
import com.example.footballapps.adapter.FavoriteMatchRecyclerViewAdapter
import com.example.footballapps.favorite.FavoriteMatchItem
import com.example.footballapps.presenter.FavoriteMatchPresenter
import com.example.footballapps.utils.gone
import com.example.footballapps.utils.invisible
import com.example.footballapps.utils.visible
import com.example.footballapps.view.FavoriteMatchView
import kotlinx.android.synthetic.main.activity_search_favorite_info.*
import org.jetbrains.anko.startActivity

class SearchFavoriteInfoActivity : AppCompatActivity(), FavoriteMatchView {

    private lateinit var searchResultFavoriteMatchPresenter: FavoriteMatchPresenter

    private var searchResultFavoriteMatches: MutableList<FavoriteMatchItem> = mutableListOf()
    private lateinit var searchResultFavoriteMatchRvAdapter: FavoriteMatchRecyclerViewAdapter

    private var isDataLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_favorite_info)

        setSupportActionBar(toolbar_search_favorite_info)

        initData()
    }

    private fun initData() {

        searchResultFavoriteMatchRvAdapter =
            FavoriteMatchRecyclerViewAdapter(this, searchResultFavoriteMatches) {
                startActivity<MatchDetailActivity>(
                    "eventId" to it.idEvent,
                    "eventName" to it.strEvent,
                    "homeTeamId" to it.homeTeamId,
                    "awayTeamId" to it.awayTeamId
                )
            }

        rv_search_favorite_info.adapter = searchResultFavoriteMatchRvAdapter
        rv_search_favorite_info.layoutManager = LinearLayoutManager(this)

        searchResultFavoriteMatchPresenter = FavoriteMatchPresenter(this, this)


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val menuInflater: MenuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_search, menu)

        val searchFavoriteInfoSearchItem: MenuItem? = menu!!.findItem(R.id.action_search)
        searchFavoriteInfoSearchItem?.expandActionView()

        val searchFavoriteInfoSearchManager: SearchManager =
            this@SearchFavoriteInfoActivity.getSystemService(Context.SEARCH_SERVICE) as SearchManager

        val searchFavoriteInfoSearchView: SearchView?

        var isSearching: Boolean

        if (searchFavoriteInfoSearchItem != null) {
            searchFavoriteInfoSearchView = searchFavoriteInfoSearchItem.actionView as SearchView

            getFavoriteDataFromQuery(searchFavoriteInfoSearchView.query.toString())

            searchFavoriteInfoSearchItem.setOnActionExpandListener(object :
                MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                    isSearching = true
                    return true
                }

                override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                    isSearching = false
                    if (!isSearching) {
                        finish()
                    }
                    return true
                }

            })

            searchFavoriteInfoSearchView.setOnQueryTextListener(object :
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

            searchFavoriteInfoSearchView.setSearchableInfo(
                searchFavoriteInfoSearchManager.getSearchableInfo(
                    this@SearchFavoriteInfoActivity.componentName
                )
            )
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun dataIsLoading() {
        isDataLoading = true
        search_favorite_info_progress_bar.visible()
        search_favorite_info_error_data_text.gone()
        rv_search_favorite_info.invisible()
    }

    override fun dataLoadingFinished() {
        search_favorite_info_progress_bar.gone()
        search_favorite_info_error_data_text.gone()
        rv_search_favorite_info.visible()

        isDataLoading = false
    }

    override fun dataFailedToLoad(errorText: String) {
        rv_search_favorite_info.invisible()
        search_favorite_info_error_data_text.visible()
        search_favorite_info_progress_bar.gone()

        search_favorite_info_error_data_text.text = errorText

        isDataLoading = false
    }

    override fun showMatchData(favoriteMatchList: List<FavoriteMatchItem>) {
        searchResultFavoriteMatches.clear()
        searchResultFavoriteMatches.addAll(favoriteMatchList)
        searchResultFavoriteMatchRvAdapter.notifyDataSetChanged()
    }

    private fun getFavoriteDataFromQuery(query: String) {
        val isNetworkConnected = checkNetworkConnection()
        searchResultFavoriteMatchPresenter.getFavoriteMatchInfoSearchResult(
            isNetworkConnected,
            query
        )
    }

    @Suppress("DEPRECATION")
    private fun checkNetworkConnection(): Boolean {

        val connectivityManager: ConnectivityManager? =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

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

                    return (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || networkCapabilities.hasTransport(
                        NetworkCapabilities.TRANSPORT_WIFI
                    ) || networkCapabilities.hasTransport(
                        NetworkCapabilities.TRANSPORT_VPN
                    ))
                }
            }
        }
        return false
    }

}
