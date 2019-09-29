package com.example.footballapps.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.example.footballapps.R
import com.example.footballapps.adapter.ViewPagerAdapter
import com.example.footballapps.fragment.LeagueDetailInfoFragment
import com.example.footballapps.fragment.LeagueMatchesFragment
import com.example.footballapps.fragment.LeagueTableFragment
import com.example.footballapps.fragment.LeagueTeamsFragment
import com.example.footballapps.lifecycle.FragmentLifecycle
import com.example.footballapps.model.LeagueItem
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_league_detail.*
import kotlin.math.abs


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class LeagueDetailActivity : AppCompatActivity(), AppBarLayout.OnOffsetChangedListener {

    private var leagueItem : LeagueItem? = null

    private lateinit var leagueName: String
    private lateinit var leagueId: String


    lateinit var leagueDetailViewPagerAdapter: ViewPagerAdapter

    private val leagueDetailInfoFragment = LeagueDetailInfoFragment()
    private val leagueTableFragment = LeagueTableFragment()
    private val leagueTeamsFragment = LeagueTeamsFragment()
    private val leagueMatchesFragment = LeagueMatchesFragment()

    private var currentPosition: Int = 0

    private var isShow = true
    private var scrollRange = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_league_detail)

        initData()

    }

    private fun initData() {
        val intent = intent
        leagueItem = intent.getParcelableExtra("leagueItem")

        when {
            leagueItem != null -> {
                leagueName = leagueItem?.leagueName ?: "English Premier League"
                leagueId = leagueItem?.leagueId ?: "4328"
            }
            else -> {
                leagueName = "English Premier League"
                leagueId = "4328"
            }
        }

        setToolbarBehavior()

        val bundle = Bundle()
        bundle.putString("leagueId", leagueId)

        leagueDetailInfoFragment.arguments = bundle
        leagueTableFragment.arguments = bundle
        leagueTeamsFragment.arguments = bundle
        leagueMatchesFragment.arguments = bundle

        setupViewPager(view_pager_league_detail)

        tab_layout_league_detail.setupWithViewPager(view_pager_league_detail)

        // todo : league imagenya tinggal pake variable biar aman maybe, sbnrnya tinggal pake premier league image biar gampang
        Glide.with(applicationContext)
            .load(leagueItem?.leagueImage)
            .placeholder(R.drawable.empty_league_image_info)
            .into(iv_league_detail_logo)

        tv_league_detail_title.text = leagueItem?.leagueName

    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {

        if(scrollRange == -1){
            scrollRange = appBarLayout.totalScrollRange
        }

        // todo : when nya jadi 1 tempat saja.
        when {
            scrollRange + verticalOffset == 0 -> {
                collapsing_toolbar_layout_league_detail.title = leagueItem?.leagueName
                isShow = true
            }
            isShow -> {
                collapsing_toolbar_layout_league_detail.title = " "
                isShow = false
            }
            abs(verticalOffset) == appBarLayout.totalScrollRange -> iv_league_detail_logo.contentDescription = getString(R.string.league_detail_logo_collapsed)
            verticalOffset == 0 -> iv_league_detail_logo.contentDescription = getString(R.string.league_detail_logo_expanded)
            else -> iv_league_detail_logo.contentDescription = getString(R.string.league_detail_logo_collapsing)
        }

    }

    private fun setupViewPager(viewPager: ViewPager) {
        leagueDetailViewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        leagueDetailViewPagerAdapter.addFragment(leagueDetailInfoFragment, "Info")
        leagueDetailViewPagerAdapter.addFragment(leagueTableFragment, "Table")
        leagueDetailViewPagerAdapter.addFragment(leagueTeamsFragment, "Teams")
        leagueDetailViewPagerAdapter.addFragment(leagueMatchesFragment, "Matches")

        viewPager.adapter = leagueDetailViewPagerAdapter

        viewPager.offscreenPageLimit = 3

        setListener()

    }

    private fun setListener() {
        view_pager_league_detail.addOnPageChangeListener(
            TabLayout.TabLayoutOnPageChangeListener(tab_layout_league_detail)
        )

        tab_layout_league_detail.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                val newPosition = tab?.position!!

                if(currentPosition == 2 || currentPosition == 3) {
                    val fragmentToHide =
                        leagueDetailViewPagerAdapter.getItem(currentPosition) as FragmentLifecycle
                    fragmentToHide.onPauseFragment()
                }

                currentPosition = newPosition

            }

        })
    }

    private fun setToolbarBehavior() {
        setSupportActionBar(toolbar_league_detail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onResume() {
        super.onResume()
        league_detail_app_bar_layout.addOnOffsetChangedListener(this)
    }

    override fun onPause() {
        super.onPause()
        league_detail_app_bar_layout.removeOnOffsetChangedListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

}