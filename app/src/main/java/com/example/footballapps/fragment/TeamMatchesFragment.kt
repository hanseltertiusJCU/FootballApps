package com.example.footballapps.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.footballapps.R
import com.example.footballapps.activity.MatchDetailActivity
import com.example.footballapps.adapter.MatchRecyclerViewAdapter
import com.example.footballapps.adapter.TeamRecyclerViewAdapter
import com.example.footballapps.espresso.EspressoIdlingResource
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

class TeamMatchesFragment : Fragment(), MatchView {

    private lateinit var teamMatchesRecyclerView : RecyclerView
    private lateinit var teamMatchesProgressBar : ProgressBar
    private lateinit var teamMatchesSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var teamMatchesSpinner : Spinner
    private lateinit var teamMatchesErrorText : TextView

    private lateinit var teamMatchesPresenter : MatchPresenter

    private var teamMatches : MutableList<MatchItem> = mutableListOf()
    private lateinit var teamMatchesRvAdapter : MatchRecyclerViewAdapter

    private lateinit var teamId : String

    private var currentPosition = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return UI{
            constraintLayout {
                id = R.id.team_matches_parent_layout

                teamMatchesSpinner = spinner {
                    id = R.id.team_match_spinner
                }.lparams{
                    width = matchParent
                    height = wrapContent
                    margin = dip(16)
                }

                teamMatchesSwipeRefreshLayout = swipeRefreshLayout {

                    setColorSchemeColors(ContextCompat.getColor(context, R.color.colorAccent))

                    teamMatchesRecyclerView = recyclerView {
                        id = R.id.rv_team_match
                        lparams(width = matchParent, height = wrapContent)
                        layoutManager = LinearLayoutManager(context)
                    }

                }.lparams {
                    width = matchConstraint
                    height = matchConstraint
                    topToBottom = R.id.team_match_spinner
                    leftToLeft = R.id.team_matches_parent_layout
                    rightToRight = R.id.team_matches_parent_layout
                    bottomToBottom = R.id.team_matches_parent_layout
                    verticalBias = 0f
                }

                teamMatchesProgressBar = progressBar().lparams{
                    topToTop = R.id.team_matches_parent_layout
                    leftToLeft = R.id.team_matches_parent_layout
                    rightToRight = R.id.team_matches_parent_layout
                    bottomToBottom = R.id.team_matches_parent_layout
                }

                teamMatchesErrorText = themedTextView(R.style.text_content).lparams{
                    topToTop = R.id.team_matches_parent_layout
                    leftToLeft = R.id.team_matches_parent_layout
                    rightToRight = R.id.team_matches_parent_layout
                    bottomToBottom = R.id.team_matches_parent_layout
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

        val matchesCategoryList = resources.getStringArray(R.array.matches_category)
        val teamMatchesSpinnerAdapter = ArrayAdapter(activity!!.applicationContext, android.R.layout.simple_spinner_dropdown_item, matchesCategoryList)

        teamMatchesSpinner.adapter = teamMatchesSpinnerAdapter

        teamMatchesRvAdapter = MatchRecyclerViewAdapter(context!!, teamMatches){
            startActivity<MatchDetailActivity>(
                "matchItem" to it
            )
        }

        teamMatchesRecyclerView.adapter = teamMatchesRvAdapter

        teamMatchesPresenter = MatchPresenter(this, MatchesRepository())

        teamMatchesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentPosition = position
                EspressoIdlingResource.increment()
                when (position){
                    1 -> teamMatchesPresenter.getTeamNextMatchInfo(teamId)
                    else -> teamMatchesPresenter.getTeamLastMatchInfo(teamId)
                }
            }

        }

        teamMatchesSwipeRefreshLayout.onRefresh {
            EspressoIdlingResource.increment()
            when (currentPosition){
                1 -> teamMatchesPresenter.getTeamNextMatchInfo(teamId)
                else -> teamMatchesPresenter.getTeamLastMatchInfo(teamId)
            }
        }



    }

    override fun dataIsLoading() {
        teamMatchesProgressBar.visible()
        teamMatchesErrorText.gone()
        teamMatchesRecyclerView.invisible()
    }

    override fun dataLoadingFinished() {
        if(!EspressoIdlingResource.idlingResource.isIdleNow) {
            EspressoIdlingResource.decrement()
        }
        teamMatchesSwipeRefreshLayout.isRefreshing = false
        teamMatchesProgressBar.gone()
        teamMatchesErrorText.gone()
        teamMatchesRecyclerView.visible()
    }

    override fun dataFailedToLoad() {
        if(!EspressoIdlingResource.idlingResource.isIdleNow) {
            EspressoIdlingResource.decrement()
        }
        teamMatchesSwipeRefreshLayout.isRefreshing = false
        teamMatchesProgressBar.gone()
        teamMatchesErrorText.visible()
        teamMatchesRecyclerView.invisible()
    }

    override fun showMatchesData(matchResponse: MatchResponse) {
        teamMatches.clear()

        when (currentPosition) {
            1 -> {
                val teamMatchesList = matchResponse.events
                if(teamMatchesList != null){
                    teamMatches.addAll(teamMatchesList)
                }
            }
            else -> {
                val teamMatchesList = matchResponse.results
                if(teamMatchesList != null){
                    teamMatches.addAll(teamMatchesList)
                }
            }
        }

        teamMatchesRvAdapter.notifyDataSetChanged()
    }




}
