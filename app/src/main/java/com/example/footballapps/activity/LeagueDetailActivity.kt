package com.example.footballapps.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.footballapps.R
import com.example.footballapps.adapter.LeagueDetailViewPagerAdapter
import com.example.footballapps.fragment.LeagueDetailInfoFragment
import com.example.footballapps.fragment.LeagueMatchesFragment
import com.example.footballapps.fragment.LeagueTableFragment
import com.example.footballapps.fragment.LeagueTeamsFragment
import com.example.footballapps.lifecycle.FragmentLifecycle
import com.example.footballapps.model.LeagueItem
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_league_detail.*


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class LeagueDetailActivity : AppCompatActivity() {

    private lateinit var leagueName: String
    private lateinit var leagueId: String

    lateinit var leagueDetailViewPagerAdapter: LeagueDetailViewPagerAdapter

    private val leagueDetailInfoFragment = LeagueDetailInfoFragment()
    private val leagueTableFragment = LeagueTableFragment()
    private val leagueTeamsFragment = LeagueTeamsFragment()
    private val leagueMatchesFragment = LeagueMatchesFragment()

    private var currentPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_league_detail)

        initData()

    }

    private fun initData() {
        val intent = intent
        val leagueItem = intent.getParcelableExtra<LeagueItem>("leagueItem")

        when {
            leagueItem != null -> {
                leagueName = leagueItem.leagueName ?: "English Premier League"
                leagueId = leagueItem.leagueId ?: "4328"
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

    }

    private fun setupViewPager(viewPager: ViewPager) {
        leagueDetailViewPagerAdapter = LeagueDetailViewPagerAdapter(supportFragmentManager)
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
                    // todo: tinggal pake pause di fragment lifecycle
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
        supportActionBar?.title = leagueName
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

}