package com.example.footballapps.fragment


import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.footballapps.R
import com.example.footballapps.espresso.EspressoIdlingResource
import com.example.footballapps.model.TeamResponse
import com.example.footballapps.presenter.TeamDetailPresenter
import com.example.footballapps.repository.TeamDetailRepository
import com.example.footballapps.utils.gone
import com.example.footballapps.utils.invisible
import com.example.footballapps.utils.visible
import com.example.footballapps.view.TeamDetailView
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.support.v4.swipeRefreshLayout
import java.lang.StringBuilder

class TeamDetailInfoFragment : Fragment(), TeamDetailView {

    private lateinit var tvTeamDetailName : TextView
    private lateinit var tvTeamDetailFormedYear : TextView
    private lateinit var tvTeamDetailCountry : TextView
    private lateinit var tvTeamDetailLeague : TextView
    private lateinit var tvTeamDetailStadiumName : TextView
    private lateinit var tvTeamDetailStadiumLocation : TextView
    private lateinit var tvTeamDetailStadiumCapacity : TextView
    private lateinit var tvTeamDetailStadiumDescription : TextView
    private lateinit var tvTeamDetailDescription : TextView
    private lateinit var ivTeamDetailBadge : ImageView

    private lateinit var teamDetailPresenter: TeamDetailPresenter

    private lateinit var teamDetailScrollView : ScrollView
    private lateinit var teamDetailSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var teamDetailLayout : RelativeLayout
    private lateinit var teamDetailErrorDataText : TextView
    private lateinit var teamDetailProgressBar : ProgressBar

    private lateinit var teamId : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return UI {
            constraintLayout {
                id = R.id.container_layout_team_detail
                teamDetailSwipeRefreshLayout = swipeRefreshLayout {

                    setColorSchemeColors(ContextCompat.getColor(context, R.color.colorAccent))

                    teamDetailScrollView = scrollView {
                        teamDetailLayout = relativeLayout {
                            id = R.id.team_detail_layout
                            padding = dip(16)
                            ivTeamDetailBadge = imageView {
                                id = R.id.iv_team_detail_image
                            }.lparams{
                                width = convertDpToPx(96f)
                                height = convertDpToPx(96f)
                                centerHorizontally()
                            }

                            tvTeamDetailName = themedTextView(R.style.text_title){
                                id = R.id.tv_team_detail_name
                            }.lparams {
                                centerHorizontally()
                                topMargin = dip(8)
                                bottomOf(R.id.iv_team_detail_image)
                            }

                            tvTeamDetailFormedYear = themedTextView(R.style.text_section){
                                id = R.id.tv_team_detail_formed_year
                            }.lparams {
                                centerHorizontally()
                                topMargin = dip(8)
                                bottomOf(R.id.tv_team_detail_name)
                            }

                            tvTeamDetailCountry = themedTextView(R.style.text_section){
                                id = R.id.tv_team_detail_country
                            }.lparams {
                                centerHorizontally()
                                topMargin = dip(8)
                                bottomOf(R.id.tv_team_detail_formed_year)
                            }

                            themedTextView("League : ", R.style.text_section) {
                                id = R.id.tv_league_title
                            }.lparams {
                                topMargin = dip(8)
                                bottomOf(R.id.tv_team_detail_country)
                            }

                            tvTeamDetailLeague = themedTextView(R.style.text_content){
                                id = R.id.tv_team_detail_league
                            }.lparams {
                                topMargin = dip(8)
                                bottomOf(R.id.tv_league_title)
                            }

                            themedTextView("Stadium : ", R.style.text_section){
                                id = R.id.tv_stadium_title
                            }.lparams {
                                topMargin = dip(8)
                                bottomOf(R.id.tv_team_detail_league)
                            }

                            themedTextView("Name : ", R.style.text_section){
                                id = R.id.tv_stadium_name_title
                            }.lparams {
                                topMargin = dip(8)
                                bottomOf(R.id.tv_stadium_title)
                            }

                            tvTeamDetailStadiumName = themedTextView(R.style.text_content){
                                id = R.id.tv_team_stadium_name
                            }.lparams{
                                topMargin = dip(8)
                                leftMargin = dip(4)
                                bottomOf(R.id.tv_stadium_title)
                                rightOf(R.id.tv_stadium_name_title)
                            }

                            themedTextView("Located In : ", R.style.text_section){
                                id = R.id.tv_stadium_located_in_title
                            }.lparams {
                                topMargin = dip(8)
                                bottomOf(R.id.tv_stadium_name_title)
                            }

                            tvTeamDetailStadiumLocation = themedTextView(R.style.text_content){
                                id = R.id.tv_team_stadium_location
                            }.lparams {
                                topMargin = dip(8)
                                leftMargin = dip(4)
                                bottomOf(R.id.tv_stadium_name_title)
                                rightOf(R.id.tv_stadium_located_in_title)
                            }

                            themedTextView("Capacity : ", R.style.text_section){
                                id = R.id.tv_stadium_capacity_title
                            }.lparams {
                                topMargin = dip(8)
                                bottomOf(R.id.tv_stadium_located_in_title)
                            }

                            tvTeamDetailStadiumCapacity = themedTextView(R.style.text_content){
                                id = R.id.tv_team_stadium_capacity
                            }.lparams {
                                topMargin = dip(8)
                                leftMargin = dip(4)
                                bottomOf(R.id.tv_stadium_located_in_title)
                                rightOf(R.id.tv_stadium_capacity_title)
                            }

                            themedTextView("Stadium Description : ", R.style.text_section){
                                id = R.id.tv_stadium_description_title
                            }.lparams{
                                topMargin = dip(8)
                                bottomOf(R.id.tv_stadium_capacity_title)
                            }

                            tvTeamDetailStadiumDescription = themedTextView(R.style.text_content){
                                id = R.id.tv_stadium_description
                            }.lparams{
                                topMargin = dip(8)
                                bottomOf(R.id.tv_stadium_description_title)
                            }

                            themedTextView("Team Description : ", R.style.text_section){
                                id = R.id.tv_team_description_title
                            }.lparams{
                                topMargin = dip(8)
                                bottomOf(R.id.tv_stadium_description)
                            }

                            tvTeamDetailDescription = themedTextView(R.style.text_content){
                                id = R.id.tv_team_description
                            }.lparams{
                                topMargin = dip(8)
                                bottomOf(R.id.tv_team_description_title)
                            }


                        }
                    }

                }

                teamDetailProgressBar = progressBar().lparams {
                    width = convertDpToPx(48f)
                    height = convertDpToPx(48f)
                    topToTop = R.id.container_layout_team_detail
                    startToStart = R.id.container_layout_team_detail
                    endToEnd = R.id.container_layout_team_detail
                    bottomToBottom = R.id.container_layout_team_detail
                }

                teamDetailErrorDataText = themedTextView(R.style.text_content).lparams {
                    topToTop = R.id.container_layout_team_detail
                    startToStart = R.id.container_layout_team_detail
                    endToEnd = R.id.container_layout_team_detail
                    bottomToBottom = R.id.container_layout_team_detail
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

        teamDetailPresenter = TeamDetailPresenter(this, TeamDetailRepository())

        EspressoIdlingResource.increment()
        teamDetailPresenter.getTeamDetailInfo(teamId)

        teamDetailSwipeRefreshLayout.setOnRefreshListener {
            EspressoIdlingResource.increment()
            teamDetailPresenter.getTeamDetailInfo(teamId)
        }
    }

    override fun dataIsLoading() {
        teamDetailProgressBar.visible()
        teamDetailErrorDataText.gone()
        teamDetailLayout.invisible()
    }

    override fun dataLoadingFinished() {
        if(!EspressoIdlingResource.idlingResource.isIdleNow) {
            EspressoIdlingResource.decrement()
        }
        teamDetailSwipeRefreshLayout.isRefreshing = false
        teamDetailProgressBar.gone()
        teamDetailErrorDataText.gone()
        teamDetailLayout.visible()
    }

    override fun dataFailedToLoad() {
        if(!EspressoIdlingResource.idlingResource.isIdleNow) {
            EspressoIdlingResource.decrement()
        }
        teamDetailSwipeRefreshLayout.isRefreshing = false
        teamDetailProgressBar.gone()
        teamDetailErrorDataText.visible()
        teamDetailLayout.invisible()

        val isNetworkConnected = checkNetworkConnection()
        if (isNetworkConnected) {
            teamDetailErrorDataText.text = resources.getString(R.string.no_data_to_show)
        } else {
            teamDetailErrorDataText.text = resources.getString(R.string.no_internet_connection)
        }
    }

    override fun showTeamDetailData(teamsResponse: TeamResponse) {
        val teamItemList = teamsResponse.teams

        if(teamItemList != null){
            val teamItem = teamItemList.first()

            Glide.with(context!!)
                .load(teamItem.teamBadge)
                .placeholder(R.drawable.team_badge_placeholder)
                .into(ivTeamDetailBadge)

            tvTeamDetailName.text = teamItem.teamName
            tvTeamDetailFormedYear.text = StringBuilder("est. ${teamItem.teamFormedYear}")
            tvTeamDetailCountry.text = StringBuilder("Based in ${teamItem.teamCountry}")

            tvTeamDetailLeague.text = teamItem.teamLeague

            tvTeamDetailStadiumName.text = teamItem.teamStadium
            tvTeamDetailStadiumLocation.text = teamItem.teamStadiumLocation
            tvTeamDetailStadiumCapacity.text = teamItem.teamStadiumCapacity
            tvTeamDetailStadiumDescription.text = teamItem.teamStadiumDescription

            tvTeamDetailDescription.text = teamItem.teamDesc
        }
    }

    private fun convertDpToPx(dp: Float): Int {
        val r = resources
        val px = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            r.displayMetrics
        )
        return px.toInt()
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
