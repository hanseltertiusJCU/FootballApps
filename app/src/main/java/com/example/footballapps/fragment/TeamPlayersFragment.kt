package com.example.footballapps.fragment


import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
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
import com.example.footballapps.activity.PlayerDetailActivity
import com.example.footballapps.adapter.PlayerRecyclerViewAdapter
import com.example.footballapps.espresso.EspressoIdlingResource
import com.example.footballapps.model.PlayerItem
import com.example.footballapps.model.PlayerResponse
import com.example.footballapps.presenter.PlayersPresenter
import com.example.footballapps.repository.PlayersRepository
import com.example.footballapps.utils.gone
import com.example.footballapps.utils.invisible
import com.example.footballapps.utils.visible
import com.example.footballapps.view.PlayersView
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.constraint.layout.matchConstraint
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.progressBar
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.nestedScrollView
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.swipeRefreshLayout
import org.jetbrains.anko.themedTextView

class TeamPlayersFragment : Fragment(), PlayersView {

    private lateinit var teamPlayersRecyclerView: RecyclerView
    private lateinit var teamPlayersProgressBar: ProgressBar
    private lateinit var teamPlayersSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var teamPlayersErrorText: TextView

    private lateinit var teamPlayersPresenter: PlayersPresenter

    private var teamPlayers: MutableList<PlayerItem> = mutableListOf()
    private lateinit var teamPlayersRvAdapter: PlayerRecyclerViewAdapter

    private lateinit var teamId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return UI {

            teamPlayersSwipeRefreshLayout = swipeRefreshLayout {

                layoutParams = ViewGroup.LayoutParams(matchParent, matchParent)

                setColorSchemeColors(ContextCompat.getColor(context, R.color.colorAccent))

                nestedScrollView {

                    lparams(width = matchParent, height = matchParent)
                    isFillViewport = true

                    constraintLayout {
                        id = R.id.team_players_parent_layout
                        lparams(width = matchParent, height = matchParent)

                        teamPlayersRecyclerView = recyclerView {
                            id = R.id.rv_team_players
                            layoutManager = LinearLayoutManager(context)
                        }.lparams {
                            width = matchConstraint
                            height = matchConstraint
                            topToTop = R.id.team_players_parent_layout
                            leftToLeft = R.id.team_players_parent_layout
                            rightToRight = R.id.team_players_parent_layout
                            bottomToBottom = R.id.team_players_parent_layout
                            verticalBias = 0f
                        }

                        teamPlayersProgressBar = progressBar().lparams {
                            topToTop = R.id.team_players_parent_layout
                            leftToLeft = R.id.team_players_parent_layout
                            rightToRight = R.id.team_players_parent_layout
                            bottomToBottom = R.id.team_players_parent_layout
                        }

                        teamPlayersErrorText = themedTextView(R.style.text_content).lparams {
                            topToTop = R.id.team_players_parent_layout
                            leftToLeft = R.id.team_players_parent_layout
                            rightToRight = R.id.team_players_parent_layout
                            bottomToBottom = R.id.team_players_parent_layout
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
        teamId = arguments?.getString("teamId") ?: "133604"

        teamPlayersRvAdapter = PlayerRecyclerViewAdapter(context!!, teamPlayers) {
            startActivity<PlayerDetailActivity>("playerItem" to it)
        }

        teamPlayersRecyclerView.adapter = teamPlayersRvAdapter

        teamPlayersPresenter = PlayersPresenter(this, PlayersRepository())

        EspressoIdlingResource.increment()
        teamPlayersPresenter.getPlayersInfo(teamId)

        teamPlayersSwipeRefreshLayout.setOnRefreshListener {
            EspressoIdlingResource.increment()
            teamPlayersPresenter.getPlayersInfo(teamId)
        }
    }

    override fun dataIsLoading() {
        teamPlayersProgressBar.visible()
        teamPlayersErrorText.gone()
        teamPlayersRecyclerView.invisible()
    }

    override fun dataLoadingFinished() {
        if (!EspressoIdlingResource.idlingResource.isIdleNow) {
            EspressoIdlingResource.decrement()
        }
        teamPlayersSwipeRefreshLayout.isRefreshing = false
        teamPlayersProgressBar.gone()
        teamPlayersErrorText.gone()
        teamPlayersRecyclerView.visible()
    }

    override fun dataFailedToLoad() {
        if (!EspressoIdlingResource.idlingResource.isIdleNow) {
            EspressoIdlingResource.decrement()
        }
        teamPlayersSwipeRefreshLayout.isRefreshing = false
        teamPlayersProgressBar.gone()
        teamPlayersErrorText.visible()
        teamPlayersRecyclerView.invisible()

        val isNetworkConnected = checkNetworkConnection()
        if (isNetworkConnected) {
            teamPlayersErrorText.text = resources.getString(R.string.no_data_to_show)
        } else {
            teamPlayersErrorText.text = resources.getString(R.string.no_internet_connection)
        }
    }

    override fun showPlayersData(playerResponse: PlayerResponse) {
        teamPlayers.clear()
        val playersList = playerResponse.playersList
        if (playersList != null) {
            teamPlayers.addAll(playersList)
        }
        teamPlayersRvAdapter.notifyDataSetChanged()
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
