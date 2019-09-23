package com.example.footballapps.fragment

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.footballapps.R
import com.example.footballapps.adapter.LeagueTableRecyclerViewAdapter
import com.example.footballapps.espresso.EspressoIdlingResource
import com.example.footballapps.model.LeagueTableResponse
import com.example.footballapps.model.TeamInTableItem
import com.example.footballapps.presenter.LeagueTablePresenter
import com.example.footballapps.repository.LeagueTableRepository
import com.example.footballapps.utils.gone
import com.example.footballapps.utils.invisible
import com.example.footballapps.utils.visible
import com.example.footballapps.view.LeagueTableView
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.constraint.layout.matchConstraint
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.onRefresh
import org.jetbrains.anko.support.v4.swipeRefreshLayout

class LeagueTableFragment : Fragment(), LeagueTableView{

    private lateinit var leagueTableRecyclerView : RecyclerView
    private lateinit var leagueTableProgressBar : ProgressBar
    private lateinit var leagueTableSwipeRefreshLayout : SwipeRefreshLayout
    private lateinit var leagueTableErrorText : TextView

    private lateinit var leagueTablePresenter: LeagueTablePresenter

    private var teamsInLeagueTableList : MutableList<TeamInTableItem> = mutableListOf()
    private lateinit var leagueTableRvAdapter : LeagueTableRecyclerViewAdapter

    private lateinit var leagueId : String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return UI {
            constraintLayout {
                id = R.id.league_table_parent_layout
                lparams(width = matchParent, height = matchParent)

                leagueTableSwipeRefreshLayout = swipeRefreshLayout {

                    setColorSchemeColors(ContextCompat.getColor(context, R.color.colorAccent))

                    // todo : vertical layout variable
                    verticalLayout {
                        include<LinearLayout>(R.layout.layout_league_table_title_column)

                        leagueTableRecyclerView = recyclerView {
                            id = R.id.rv_league_table
                            lparams(width = matchParent, height = wrapContent)
                            layoutManager = LinearLayoutManager(context)
                        }

                    }

                }.lparams{
                    width = matchConstraint
                    height = matchConstraint
                    topToTop = R.id.league_table_parent_layout
                    leftToLeft = R.id.league_table_parent_layout
                    rightToRight = R.id.league_table_parent_layout
                    bottomToBottom = R.id.league_table_parent_layout
                    verticalBias = 0f
                }

                leagueTableProgressBar = progressBar().lparams{
                    topToTop = R.id.league_table_parent_layout
                    leftToLeft = R.id.league_table_parent_layout
                    rightToRight = R.id.league_table_parent_layout
                    bottomToBottom = R.id.league_table_parent_layout
                }

                leagueTableErrorText = themedTextView(R.style.text_content).lparams{
                    topToTop = R.id.league_table_parent_layout
                    leftToLeft = R.id.league_table_parent_layout
                    rightToRight = R.id.league_table_parent_layout
                    bottomToBottom = R.id.league_table_parent_layout
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

        leagueTableRvAdapter = LeagueTableRecyclerViewAdapter(context!!, teamsInLeagueTableList)

        leagueTableRecyclerView.adapter = leagueTableRvAdapter

        leagueTablePresenter = LeagueTablePresenter(this, LeagueTableRepository())

        EspressoIdlingResource.increment()
        leagueTablePresenter.getLeagueTableInfo(leagueId)

        leagueTableSwipeRefreshLayout.onRefresh {
            EspressoIdlingResource.increment()
            leagueTablePresenter.getLeagueTableInfo(leagueId)
        }
    }

    override fun dataIsLoading() {
        leagueTableProgressBar.visible()
        leagueTableErrorText.gone()
        leagueTableRecyclerView.invisible()
    }

    override fun dataLoadingFinished() {
        if(!EspressoIdlingResource.idlingResource.isIdleNow){
            EspressoIdlingResource.decrement()
        }

        leagueTableSwipeRefreshLayout.isRefreshing = false
        leagueTableProgressBar.gone()
        leagueTableErrorText.gone()
        leagueTableRecyclerView.visible()
    }

    override fun dataFailedToLoad() {
        if(!EspressoIdlingResource.idlingResource.isIdleNow){
            EspressoIdlingResource.decrement()
        }

        leagueTableSwipeRefreshLayout.isRefreshing = false
        leagueTableProgressBar.gone()
        leagueTableErrorText.visible()
        leagueTableRecyclerView.invisible()

        val isNetworkConnected = checkNetworkConnection()
        if(isNetworkConnected){
            leagueTableErrorText.text = resources.getString(R.string.no_data_to_show)
        } else {
            leagueTableErrorText.text = resources.getString(R.string.no_internet_connection)
        }
    }

    override fun showLeagueTable(leagueTableResponse: LeagueTableResponse) {
        teamsInLeagueTableList.clear()
        val leagueTableTeams = leagueTableResponse.leagueTable
        if(leagueTableTeams != null){
            teamsInLeagueTableList.addAll(leagueTableTeams)
        }
        Log.d(" table size", teamsInLeagueTableList.size.toString())

        leagueTableRvAdapter.notifyDataSetChanged()
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