package com.example.footballapps.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.footballapps.R
import com.example.footballapps.adapter.PlayerRecyclerViewAdapter
import com.example.footballapps.adapter.TeamRecyclerViewAdapter
import com.example.footballapps.espresso.EspressoIdlingResource
import com.example.footballapps.model.PlayerItem
import com.example.footballapps.model.PlayerResponse
import com.example.footballapps.presenter.PlayersPresenter
import com.example.footballapps.presenter.TeamsPresenter
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
import org.jetbrains.anko.support.v4.swipeRefreshLayout
import org.jetbrains.anko.themedTextView
import org.jetbrains.anko.wrapContent

/**
 * A simple [Fragment] subclass.
 */
class TeamPlayersFragment : Fragment(), PlayersView {

    private lateinit var teamPlayersRecyclerView : RecyclerView
    private lateinit var teamPlayersProgressBar : ProgressBar
    private lateinit var teamPlayersSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var teamPlayersErrorText : TextView

    private lateinit var teamPlayersPresenter : PlayersPresenter

    private var teamPlayers : MutableList<PlayerItem> = mutableListOf()
    private lateinit var teamPlayersRvAdapter : PlayerRecyclerViewAdapter

    private lateinit var teamId : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return UI {
            constraintLayout {
                id = R.id.team_players_parent_layout
                lparams(width = matchParent, height = matchParent)

                teamPlayersSwipeRefreshLayout = swipeRefreshLayout {
                    setColorSchemeColors(ContextCompat.getColor(context, R.color.colorAccent))

                    teamPlayersRecyclerView = recyclerView {
                        id = R.id.rv_team_players
                        lparams(width = matchParent, height = wrapContent)
                        layoutManager = LinearLayoutManager(context)
                    }
                }.lparams {
                    width = matchConstraint
                    height = matchConstraint
                    topToTop = R.id.team_players_parent_layout
                    leftToLeft = R.id.team_players_parent_layout
                    rightToRight = R.id.team_players_parent_layout
                    bottomToBottom = R.id.team_players_parent_layout
                    verticalBias = 0f
                }

                teamPlayersProgressBar = progressBar().lparams{
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
        }.view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
    }

    private fun initData(){
        teamId = arguments?.getString("teamId") ?: "133604"

        // todo : tinggal pake adapter
        teamPlayersRvAdapter = PlayerRecyclerViewAdapter(context!!, teamPlayers){
            // todo: implement it
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
        teamPlayersRecyclerView.visible()
    }

    override fun dataLoadingFinished() {
        if(!EspressoIdlingResource.idlingResource.isIdleNow){
            EspressoIdlingResource.decrement()
        }
        teamPlayersSwipeRefreshLayout.isRefreshing = false
        teamPlayersProgressBar.gone()
        teamPlayersErrorText.gone()
        teamPlayersRecyclerView.visible()
    }

    override fun dataFailedToLoad() {
        if(!EspressoIdlingResource.idlingResource.isIdleNow){
            EspressoIdlingResource.decrement()
        }
        teamPlayersSwipeRefreshLayout.isRefreshing = false
        teamPlayersProgressBar.gone()
        teamPlayersErrorText.visible()
        teamPlayersRecyclerView.invisible()
    }

    override fun showPlayersData(playerResponse: PlayerResponse) {
        teamPlayers.clear()
        val playersList = playerResponse.playersList
        if(playersList != null){
            teamPlayers.addAll(playersList)
        }
        teamPlayersRvAdapter.notifyDataSetChanged()
    }


}
