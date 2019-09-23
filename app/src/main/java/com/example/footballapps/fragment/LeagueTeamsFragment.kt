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
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.footballapps.R
import com.example.footballapps.adapter.TeamRecyclerViewAdapter
import com.example.footballapps.espresso.EspressoIdlingResource
import com.example.footballapps.model.TeamItem
import com.example.footballapps.model.TeamResponse
import com.example.footballapps.presenter.TeamsPresenter
import com.example.footballapps.repository.TeamsRepository
import com.example.footballapps.utils.gone
import com.example.footballapps.utils.invisible
import com.example.footballapps.utils.visible
import com.example.footballapps.view.TeamsView
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.constraint.layout.matchConstraint
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.progressBar
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.onRefresh
import org.jetbrains.anko.support.v4.swipeRefreshLayout
import org.jetbrains.anko.themedTextView
import org.jetbrains.anko.wrapContent

class LeagueTeamsFragment : Fragment(), TeamsView{

    private lateinit var leagueTeamsRecyclerView : RecyclerView
    private lateinit var leagueTeamsProgressBar : ProgressBar
    private lateinit var leagueTeamsSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var leagueTeamsErrorText : TextView

    private lateinit var leagueTeamsPresenter : TeamsPresenter

    private var leagueTeams : MutableList<TeamItem> = mutableListOf()
    private lateinit var leagueTeamsRvAdapter : TeamRecyclerViewAdapter

    private lateinit var leagueId : String

    // todo: pake search item

    // todo: pake value isdataloading sm issearching


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return UI {
            constraintLayout {
                id = R.id.league_teams_parent_layout
                lparams(width = matchParent, height = matchParent)

                leagueTeamsSwipeRefreshLayout = swipeRefreshLayout {
                    setColorSchemeColors(ContextCompat.getColor(context, R.color.colorAccent))

                    leagueTeamsRecyclerView = recyclerView {
                        id = R.id.rv_league_teams
                        lparams(width = matchParent, height = wrapContent)
                        layoutManager = LinearLayoutManager(context)
                    }

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
        }.view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
    }

    // todo: implement view nya
    private fun initData() {
        leagueId = arguments?.getString("leagueId") ?: "4328"

        leagueTeamsRvAdapter = TeamRecyclerViewAdapter(leagueTeams) {
            // todo : implement it
        }

        leagueTeamsRecyclerView.adapter = leagueTeamsRvAdapter

        leagueTeamsPresenter = TeamsPresenter(this, TeamsRepository())

        EspressoIdlingResource.increment()
        leagueTeamsPresenter.getTeamsInfo(leagueId)

        leagueTeamsSwipeRefreshLayout.onRefresh {
            // todo: is searching nya
            EspressoIdlingResource.increment()
            leagueTeamsPresenter.getTeamsInfo(leagueId)
        }


    }

    override fun dataIsLoading() {
        leagueTeamsProgressBar.visible()
        leagueTeamsErrorText.gone()
        leagueTeamsRecyclerView.invisible()
    }

    override fun dataLoadingFinished() {
        if(!EspressoIdlingResource.idlingResource.isIdleNow){
            EspressoIdlingResource.decrement()
        }

        leagueTeamsSwipeRefreshLayout.isRefreshing = false
        leagueTeamsProgressBar.gone()
        leagueTeamsErrorText.gone()
        leagueTeamsRecyclerView.visible()

        // todo: is data loading false
    }

    override fun dataFailedToLoad() {
        if(!EspressoIdlingResource.idlingResource.isIdleNow){
            EspressoIdlingResource.decrement()
        }
        leagueTeamsSwipeRefreshLayout.isRefreshing = false
        leagueTeamsProgressBar.gone()
        leagueTeamsErrorText.visible()
        leagueTeamsRecyclerView.invisible()

        // todo : is data loading false

        val isNetworkConnected = checkNetworkConnection()
        if(isNetworkConnected){
            leagueTeamsErrorText.text = resources.getString(R.string.no_data_to_show)
        } else {
            leagueTeamsErrorText.text = resources.getString(R.string.no_internet_connection)
        }
    }

    override fun showTeamsData(teamsResponse: TeamResponse) {
        leagueTeams.clear()
        val teamsList = teamsResponse.teams
        if(teamsList != null){
            leagueTeams.addAll(teamsList)
        }
        Log.d("size", leagueTeams.size.toString())
        Log.d("test team", leagueTeams.first().teamName.toString())
        leagueTeamsRvAdapter.notifyDataSetChanged()
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

    // todo: mungkin pake visible hintnya gmn


}