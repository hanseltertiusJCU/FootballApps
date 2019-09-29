package com.example.footballapps.fragment


import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.footballapps.R
import com.example.footballapps.espresso.EspressoIdlingResource
import com.example.footballapps.model.TeamResponse
import com.example.footballapps.presenter.TeamDetailPresenter
import com.example.footballapps.repository.TeamDetailRepository
import com.example.footballapps.utils.gone
import com.example.footballapps.utils.visible
import com.example.footballapps.view.TeamDetailView
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.nestedScrollView
import org.jetbrains.anko.support.v4.swipeRefreshLayout

class TeamDetailInfoFragment : Fragment(), TeamDetailView {

    private lateinit var tvTeamDetailInfoLeague: TextView
    private lateinit var tvTeamDetailInfoStadiumName: TextView
    private lateinit var tvTeamDetailInfoStadiumLocation: TextView
    private lateinit var tvTeamDetailInfoStadiumCapacity: TextView
    private lateinit var tvTeamDetailInfoStadiumDescription: TextView
    private lateinit var tvTeamDetailInfoDescription: TextView
    private lateinit var ivTeamDetailJersey: ImageView

    private lateinit var teamDetailPresenter: TeamDetailPresenter

    private lateinit var teamDetailInfoNestedScrollView: NestedScrollView
    private lateinit var teamDetailInfoSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var teamDetailInfoLayout: LinearLayout
    private lateinit var teamDetailInfoErrorDataText: TextView
    private lateinit var teamDetailInfoProgressBar: ProgressBar

    private lateinit var teamId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return UI {
            teamDetailInfoSwipeRefreshLayout = swipeRefreshLayout {

                layoutParams = ViewGroup.LayoutParams(matchParent, matchParent)

                setColorSchemeColors(ContextCompat.getColor(context, R.color.colorAccent))

                teamDetailInfoNestedScrollView = nestedScrollView {

                    lparams(width = matchParent, height = matchParent)
                    isFillViewport = true

                    constraintLayout {
                        id = R.id.container_layout_team_detail

                        teamDetailInfoLayout = verticalLayout {
                            id = R.id.team_detail_layout
                            padding = dip(16)

                            themedTextView("Jersey : ", R.style.text_section) {
                                id = R.id.tv_team_description_title
                            }

                            ivTeamDetailJersey = imageView {
                                id = R.id.tv_team_jersey
                            }.lparams {
                                width = dip(128)
                                height = dip(128)
                                gravity = Gravity.CENTER_HORIZONTAL
                                topMargin = dip(8)
                            }

                            view {
                                background =
                                    ContextCompat.getDrawable(context, R.color.color_grey_line)
                            }.lparams {
                                width = matchParent
                                height = dip(1)
                                topMargin = dip(8)
                            }

                            themedTextView("League : ", R.style.text_section) {
                                id = R.id.tv_league_title
                            }.lparams {
                                topMargin = dip(8)
                            }

                            tvTeamDetailInfoLeague = themedTextView(R.style.text_content) {
                                id = R.id.tv_team_detail_league
                            }.lparams {
                                topMargin = dip(8)
                            }

                            view {
                                background =
                                    ContextCompat.getDrawable(context, R.color.color_grey_line)
                            }.lparams {
                                width = matchParent
                                height = dip(1)
                                topMargin = dip(8)
                            }

                            themedTextView("Stadium : ", R.style.text_section) {
                                id = R.id.tv_stadium_title
                            }.lparams {
                                topMargin = dip(8)
                            }

                            tvTeamDetailInfoStadiumName = themedTextView(R.style.text_content) {
                                id = R.id.tv_team_stadium_name
                            }.lparams {
                                topMargin = dip(8)
                            }

                            tvTeamDetailInfoStadiumLocation = themedTextView(R.style.text_content) {
                                id = R.id.tv_team_stadium_location
                            }.lparams {
                                topMargin = dip(8)
                            }

                            tvTeamDetailInfoStadiumCapacity = themedTextView(R.style.text_content) {
                                id = R.id.tv_team_stadium_capacity
                            }.lparams {
                                topMargin = dip(8)
                            }

                            view {
                                background =
                                    ContextCompat.getDrawable(context, R.color.color_grey_line)
                            }.lparams {
                                width = matchParent
                                height = dip(1)
                                topMargin = dip(8)
                            }

                            themedTextView("Stadium Description : ", R.style.text_section) {
                                id = R.id.tv_stadium_description_title
                            }.lparams {
                                topMargin = dip(8)
                            }

                            tvTeamDetailInfoStadiumDescription =
                                themedTextView(R.style.text_content) {
                                    id = R.id.tv_stadium_description
                                }.lparams {
                                    topMargin = dip(8)
                                }

                            view {
                                background =
                                    ContextCompat.getDrawable(context, R.color.color_grey_line)
                            }.lparams {
                                width = matchParent
                                height = dip(1)
                                topMargin = dip(8)
                            }

                            themedTextView("Team Description : ", R.style.text_section) {
                                id = R.id.tv_team_description_title
                            }.lparams {
                                topMargin = dip(8)
                            }

                            tvTeamDetailInfoDescription = themedTextView(R.style.text_content) {
                                id = R.id.tv_team_description
                            }.lparams {
                                topMargin = dip(8)
                            }

                        }

                        teamDetailInfoProgressBar = progressBar().lparams {
                            width = dip(48)
                            height = dip(48)
                            topToTop = R.id.container_layout_team_detail
                            startToStart = R.id.container_layout_team_detail
                            endToEnd = R.id.container_layout_team_detail
                            bottomToBottom = R.id.container_layout_team_detail
                        }

                        teamDetailInfoErrorDataText = themedTextView(R.style.text_content).lparams {
                            topToTop = R.id.container_layout_team_detail
                            startToStart = R.id.container_layout_team_detail
                            endToEnd = R.id.container_layout_team_detail
                            bottomToBottom = R.id.container_layout_team_detail
                        }


                    }.lparams(width = matchParent, height = matchParent)
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

        teamDetailPresenter = TeamDetailPresenter(this, TeamDetailRepository())

        EspressoIdlingResource.increment()
        teamDetailPresenter.getTeamDetailInfo(teamId)

        teamDetailInfoSwipeRefreshLayout.setOnRefreshListener {
            EspressoIdlingResource.increment()
            teamDetailPresenter.getTeamDetailInfo(teamId)
        }
    }

    override fun dataIsLoading() {
        teamDetailInfoProgressBar.visible()
        teamDetailInfoErrorDataText.gone()
        teamDetailInfoLayout.gone()
    }

    override fun dataLoadingFinished() {
        if (!EspressoIdlingResource.idlingResource.isIdleNow) {
            EspressoIdlingResource.decrement()
        }
        teamDetailInfoSwipeRefreshLayout.isRefreshing = false
        teamDetailInfoProgressBar.gone()
        teamDetailInfoErrorDataText.gone()
        teamDetailInfoLayout.visible()
    }

    override fun dataFailedToLoad() {
        if (!EspressoIdlingResource.idlingResource.isIdleNow) {
            EspressoIdlingResource.decrement()
        }
        teamDetailInfoSwipeRefreshLayout.isRefreshing = false
        teamDetailInfoProgressBar.gone()
        teamDetailInfoErrorDataText.visible()
        teamDetailInfoLayout.gone()

        val isNetworkConnected = checkNetworkConnection()
        if (isNetworkConnected) {
            teamDetailInfoErrorDataText.text = resources.getString(R.string.no_data_to_show)
        } else {
            teamDetailInfoErrorDataText.text = resources.getString(R.string.no_internet_connection)
        }
    }

    override fun showTeamDetailData(teamsResponse: TeamResponse) {
        val teamItemList = teamsResponse.teams

        if (teamItemList != null) {
            val teamItem = teamItemList.first()

            Glide.with(context!!)
                .load(teamItem.teamJersey)
                .placeholder(R.drawable.jersey_placeholder)
                .into(ivTeamDetailJersey)

            tvTeamDetailInfoLeague.text = teamItem.teamLeague

            tvTeamDetailInfoStadiumName.text = StringBuilder("Name : ${teamItem.teamStadium}")
            tvTeamDetailInfoStadiumLocation.text =
                StringBuilder("Located in : ${teamItem.teamStadiumLocation}")
            tvTeamDetailInfoStadiumCapacity.text =
                StringBuilder("Capacity : ${teamItem.teamStadiumCapacity}")
            tvTeamDetailInfoStadiumDescription.text = teamItem.teamStadiumDescription

            tvTeamDetailInfoDescription.text = teamItem.teamDesc
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


}
