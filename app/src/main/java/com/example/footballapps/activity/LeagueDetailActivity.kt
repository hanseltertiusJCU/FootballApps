package com.example.footballapps.activity

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.footballapps.R
import com.example.footballapps.espresso.EspressoIdlingResource
import com.example.footballapps.model.LeagueDetailResponse
import com.example.footballapps.model.LeagueItem
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
import org.jetbrains.anko.support.v4.swipeRefreshLayout


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class LeagueDetailActivity : AppCompatActivity(), LeagueDetailView {

    private lateinit var leagueName: String
    private lateinit var leagueId: String

    private lateinit var tvLeagueDetailName: TextView
    private lateinit var tvLeagueDetailFormedYear: TextView
    private lateinit var tvLeagueDetailCountry: TextView
    private lateinit var ivLeagueDetailImage: ImageView
    private lateinit var tvLeagueDetailDesc: TextView
    private lateinit var tvDescTitle: TextView

    private lateinit var leagueDetailPresenter: LeagueDetailPresenter

    private lateinit var leagueDetailScrollView: ScrollView
    private lateinit var toolbarLeagueDetail: Toolbar
    private lateinit var leagueDetailSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var leagueDetailLayout: RelativeLayout
    private lateinit var leagueDetailErrorDataText: TextView
    private lateinit var leagueDetailProgressBar: ProgressBar

    // todo : viewnya itu ubah jadi fragment, intinya kita itu ingin menampung viewpager + tablayout untuk teams, league table,

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()

        initData()

    }

    private fun initView() {
        coordinatorLayout {

            verticalLayout {
                themedAppBarLayout(R.style.ThemeOverlay_AppCompat_Dark_ActionBar) {
                    lparams(width = matchParent, height = wrapContent)

                    toolbarLeagueDetail = toolbar {
                        id = R.id.toolbar_league_detail
                        lparams(width = matchParent, height = dimenAttr(R.attr.actionBarSize))
                        popupTheme = R.style.ThemeOverlay_AppCompat_Light
                    }
                }

                constraintLayout {
                    id = R.id.container_layout_league_detail
                    leagueDetailSwipeRefreshLayout = swipeRefreshLayout {

                        setColorSchemeColors(ContextCompat.getColor(context, R.color.colorAccent))

                        leagueDetailScrollView = scrollView {
                            leagueDetailLayout = relativeLayout {
                                id = R.id.league_detail_layout
                                padding = dip(16)
                                ivLeagueDetailImage = imageView {
                                    id = R.id.iv_league_detail_image
                                }.lparams {
                                    width = convertDpToPx(96f)
                                    height = convertDpToPx(96f)
                                    centerHorizontally()
                                }

                                tvLeagueDetailName = themedTextView(R.style.text_title) {
                                    id = R.id.tv_league_detail_name
                                }.lparams {
                                    centerHorizontally()
                                    topMargin = dip(8)
                                    bottomOf(R.id.iv_league_detail_image)
                                }

                                tvLeagueDetailFormedYear = themedTextView(R.style.text_section) {
                                    id = R.id.tv_league_detail_formed_year
                                }.lparams {
                                    centerHorizontally()
                                    topMargin = dip(8)
                                    bottomOf(R.id.tv_league_detail_name)
                                }

                                tvLeagueDetailCountry = themedTextView(R.style.text_section) {
                                    id = R.id.tv_league_detail_country
                                }.lparams {
                                    centerHorizontally()
                                    topMargin = dip(8)
                                    bottomOf(R.id.tv_league_detail_formed_year)
                                }

                                tvDescTitle =
                                    themedTextView("Description : ", R.style.text_section) {
                                        id = R.id.tv_desc_title
                                    }.lparams {
                                        topMargin = dip(8)
                                        bottomOf(R.id.tv_league_detail_country)
                                    }
                                tvLeagueDetailDesc = themedTextView(R.style.text_content) {
                                    id = R.id.tv_league_detail_desc
                                }.lparams {
                                    topMargin = dip(8)
                                    bottomOf(R.id.tv_desc_title)
                                }
                            }
                        }
                    }

                    leagueDetailProgressBar = progressBar {
                        id = R.id.progress_bar
                    }.lparams {
                        width = convertDpToPx(48f)
                        height = convertDpToPx(48f)
                        topToTop = R.id.container_layout_league_detail
                        startToStart = R.id.container_layout_league_detail
                        endToEnd = R.id.container_layout_league_detail
                        bottomToBottom = R.id.container_layout_league_detail
                    }

                    leagueDetailErrorDataText = themedTextView(R.style.text_content).lparams {
                        topToTop = R.id.container_layout_league_detail
                        startToStart = R.id.container_layout_league_detail
                        endToEnd = R.id.container_layout_league_detail
                        bottomToBottom = R.id.container_layout_league_detail
                    }
                }
            }


        }
    }

    private fun initData() {
        val intent = intent
        leagueName = intent.getStringExtra("leagueName")
        leagueId = intent.getStringExtra("leagueId")

        setToolbarBehavior()

        leagueDetailPresenter = LeagueDetailPresenter(this, LeagueDetailRepository())

        EspressoIdlingResource.increment()
        leagueDetailPresenter.getLeagueDetailInfo(leagueId)

        leagueDetailSwipeRefreshLayout.setOnRefreshListener {
            EspressoIdlingResource.increment()
            leagueDetailPresenter.getLeagueDetailInfo(leagueId)
        }

    }

    override fun dataIsLoading() {
        leagueDetailProgressBar.visible()
        leagueDetailErrorDataText.gone()
        leagueDetailLayout.invisible()
    }

    override fun dataLoadingFinished() {
        if(!EspressoIdlingResource.idlingResource.isIdleNow) {
            EspressoIdlingResource.decrement()
        }
        leagueDetailSwipeRefreshLayout.isRefreshing = false
        leagueDetailProgressBar.gone()
        leagueDetailErrorDataText.gone()
        leagueDetailLayout.visible()
    }

    override fun dataFailedToLoad() {
        if(!EspressoIdlingResource.idlingResource.isIdleNow) {
            EspressoIdlingResource.decrement()
        }
        leagueDetailSwipeRefreshLayout.isRefreshing = false
        leagueDetailProgressBar.gone()
        leagueDetailErrorDataText.visible()
        leagueDetailLayout.invisible()

        val isNetworkConnected = checkNetworkConnection()
        if (isNetworkConnected) {
            leagueDetailErrorDataText.text = resources.getString(R.string.no_data_to_show)
        } else {
            leagueDetailErrorDataText.text = resources.getString(R.string.no_internet_connection)
        }

    }

    override fun showLeagueDetailTitle(leagueItem: LeagueItem) {
        setSupportActionBar(toolbarLeagueDetail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = leagueItem.leagueName
    }

    private fun setToolbarBehavior() {
        setSupportActionBar(toolbarLeagueDetail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = leagueName
    }

    override fun showLeagueDetailInfo(leagueDetailResponse: LeagueDetailResponse) {
        val leagueItemList = leagueDetailResponse.leagues

        if (leagueItemList != null) {
            val leagueDetailItem = leagueItemList.first()

            tvLeagueDetailName.text = leagueDetailItem.leagueName
            tvLeagueDetailDesc.text = leagueDetailItem.leagueDescription

            Glide.with(applicationContext)
                .load(leagueDetailItem.leagueBadge)
                .placeholder(R.drawable.empty_league_image_info)
                .into(ivLeagueDetailImage)

            tvLeagueDetailFormedYear.text =
                StringBuilder("est. ${leagueDetailItem.leagueFormedYear}")
            tvLeagueDetailCountry.text = StringBuilder("Based in ${leagueDetailItem.leagueCountry}")
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val menuInflater: MenuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_schedule, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when {
            item?.itemId == android.R.id.home -> finish()
            item?.itemId == R.id.action_match_schedule -> {
                startActivity<FootballGameInfoActivity>(
                    "leagueName" to leagueName,
                    "leagueId" to leagueId
                )
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
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


                    return (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                            || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                            || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN))
                }
            }
        }
        return false
    }
}