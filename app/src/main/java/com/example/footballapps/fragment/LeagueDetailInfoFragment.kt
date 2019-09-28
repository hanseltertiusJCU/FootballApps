package com.example.footballapps.fragment

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.footballapps.R
import com.example.footballapps.espresso.EspressoIdlingResource
import com.example.footballapps.model.LeagueDetailResponse
import com.example.footballapps.presenter.LeagueDetailPresenter
import com.example.footballapps.repository.LeagueDetailRepository
import com.example.footballapps.utils.gone
import com.example.footballapps.utils.invisible
import com.example.footballapps.utils.visible
import com.example.footballapps.view.LeagueDetailView
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.design.themedAppBarLayout
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.swipeRefreshLayout

class LeagueDetailInfoFragment : Fragment(), LeagueDetailView {

    private lateinit var tvLeagueDetailName: TextView
    private lateinit var tvLeagueDetailFormedYear: TextView
    private lateinit var tvLeagueDetailCountry: TextView
    private lateinit var ivLeagueDetailImage: ImageView
    private lateinit var ivLeagueDetailTrophy: ImageView
    private lateinit var tvLeagueDetailDesc: TextView

    private lateinit var leagueDetailPresenter: LeagueDetailPresenter

    private lateinit var leagueDetailInfoScrollView: ScrollView
    private lateinit var leagueDetailInfoSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var leagueDetailInfoLayout: LinearLayout
    private lateinit var leagueDetailInfoErrorDataText: TextView
    private lateinit var leagueDetailInfoProgressBar: ProgressBar

    private lateinit var leagueId: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return UI {
            constraintLayout {
                id = R.id.container_layout_league_detail
                leagueDetailInfoSwipeRefreshLayout = swipeRefreshLayout {

                    setColorSchemeColors(ContextCompat.getColor(context, R.color.colorAccent))

                    leagueDetailInfoScrollView = scrollView {
                        leagueDetailInfoLayout = verticalLayout {
                            id = R.id.league_detail_layout
                            padding = dip(16)
                            ivLeagueDetailImage = imageView {
                                id = R.id.iv_league_detail_image
                            }.lparams {
                                width = dip(128)
                                height = dip(128)
                                gravity = Gravity.CENTER_HORIZONTAL
                            }

                            tvLeagueDetailName = themedTextView(R.style.text_title) {
                                id = R.id.tv_league_detail_name
                            }.lparams {
                                gravity = Gravity.CENTER_HORIZONTAL
                                topMargin = dip(8)
                            }

                            tvLeagueDetailFormedYear = themedTextView(R.style.text_content) {
                                id = R.id.tv_league_detail_formed_year
                            }.lparams {
                                gravity = Gravity.CENTER_HORIZONTAL
                                topMargin = dip(8)
                            }

                            tvLeagueDetailCountry = themedTextView(R.style.text_content) {
                                id = R.id.tv_league_detail_country
                            }.lparams {
                                gravity = Gravity.CENTER_HORIZONTAL
                                topMargin = dip(8)
                            }

                            view {
                                background = ContextCompat.getDrawable(context, R.color.color_grey_line)
                            }.lparams {
                                width = matchParent
                                height = dip(1)
                                topMargin = dip(8)
                            }

                            themedTextView("Trophy : ", R.style.text_section) {
                                id = R.id.tv_desc_title
                            }.lparams {
                                topMargin = dip(8)
                            }
                            ivLeagueDetailTrophy = imageView {
                                id = R.id.iv_league_detail_trophy
                            }.lparams {
                                width = dip(128)
                                height = dip(128)
                                gravity = Gravity.CENTER_HORIZONTAL
                                topMargin = dip(8)
                            }

                            view {
                                background = ContextCompat.getDrawable(context, R.color.color_grey_line)
                            }.lparams {
                                width = matchParent
                                height = dip(1)
                                topMargin = dip(8)
                            }

                            themedTextView("Description : ", R.style.text_section) {
                                id = R.id.tv_desc_title
                            }.lparams {
                                topMargin = dip(8)
                            }
                            tvLeagueDetailDesc = themedTextView(R.style.text_content) {
                                id = R.id.tv_league_detail_desc
                            }.lparams {
                                gravity = Gravity.CENTER_HORIZONTAL
                                topMargin = dip(8)
                            }
                        }
                    }
                }

                leagueDetailInfoProgressBar = progressBar {
                    id = R.id.progress_bar
                }.lparams {
                    width = convertDpToPx(48f)
                    height = convertDpToPx(48f)
                    topToTop = R.id.container_layout_league_detail
                    startToStart = R.id.container_layout_league_detail
                    endToEnd = R.id.container_layout_league_detail
                    bottomToBottom = R.id.container_layout_league_detail
                }

                leagueDetailInfoErrorDataText = themedTextView(R.style.text_content).lparams {
                    topToTop = R.id.container_layout_league_detail
                    startToStart = R.id.container_layout_league_detail
                    endToEnd = R.id.container_layout_league_detail
                    bottomToBottom = R.id.container_layout_league_detail
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

        leagueDetailPresenter = LeagueDetailPresenter(this, LeagueDetailRepository())

        EspressoIdlingResource.increment()
        leagueDetailPresenter.getLeagueDetailInfo(leagueId)

        leagueDetailInfoSwipeRefreshLayout.setOnRefreshListener {
            EspressoIdlingResource.increment()
            leagueDetailPresenter.getLeagueDetailInfo(leagueId)
        }
    }

    override fun dataIsLoading() {
        leagueDetailInfoProgressBar.visible()
        leagueDetailInfoErrorDataText.gone()
        leagueDetailInfoLayout.invisible()
    }

    override fun dataLoadingFinished() {
        if(!EspressoIdlingResource.idlingResource.isIdleNow) {
            EspressoIdlingResource.decrement()
        }
        leagueDetailInfoSwipeRefreshLayout.isRefreshing = false
        leagueDetailInfoProgressBar.gone()
        leagueDetailInfoErrorDataText.gone()
        leagueDetailInfoLayout.visible()
    }

    override fun dataFailedToLoad() {
        if(!EspressoIdlingResource.idlingResource.isIdleNow) {
            EspressoIdlingResource.decrement()
        }
        leagueDetailInfoSwipeRefreshLayout.isRefreshing = false
        leagueDetailInfoProgressBar.gone()
        leagueDetailInfoErrorDataText.visible()
        leagueDetailInfoLayout.invisible()

        val isNetworkConnected = checkNetworkConnection()
        if (isNetworkConnected) {
            leagueDetailInfoErrorDataText.text = resources.getString(R.string.no_data_to_show)
        } else {
            leagueDetailInfoErrorDataText.text = resources.getString(R.string.no_internet_connection)
        }
    }

    override fun showLeagueDetailInfo(leagueDetailResponse: LeagueDetailResponse) {
        val leagueItemList = leagueDetailResponse.leagues

        if (leagueItemList != null) {
            val leagueDetailItem = leagueItemList.first()

            tvLeagueDetailName.text = leagueDetailItem.leagueName
            tvLeagueDetailDesc.text = leagueDetailItem.leagueDescription

            Glide.with(context!!)
                .load(leagueDetailItem.leagueBadge)
                .placeholder(R.drawable.empty_league_image_info)
                .into(ivLeagueDetailImage)

            Glide.with(context!!)
                .load(leagueDetailItem.leagueTrophy)
                .placeholder(R.drawable.trophy_placeholder)
                .into(ivLeagueDetailTrophy)

            tvLeagueDetailFormedYear.text =
                StringBuilder("est. ${leagueDetailItem.leagueFormedYear}")
            tvLeagueDetailCountry.text = StringBuilder("Based in ${leagueDetailItem.leagueCountry}")
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