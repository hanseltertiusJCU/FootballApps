package com.example.footballapps.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.example.footballapps.R
import com.example.footballapps.adapter.LeagueDetailViewPagerAdapter
import com.example.footballapps.fragment.LeagueDetailInfoFragment
import com.example.footballapps.fragment.LeagueTableFragment
import com.example.footballapps.fragment.LeagueTeamsFragment
import com.example.footballapps.lifecycle.FragmentLifecycle
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_league_detail.*
import org.jetbrains.anko.startActivity


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class LeagueDetailActivity : AppCompatActivity() {

    private lateinit var leagueName: String
    private lateinit var leagueId: String

    lateinit var leagueDetailViewPagerAdapter: LeagueDetailViewPagerAdapter

    private val leagueDetailInfoFragment = LeagueDetailInfoFragment()
    private val leagueTableFragment = LeagueTableFragment()
    private val leagueTeamsFragment = LeagueTeamsFragment()

    private var currentPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_league_detail)

        initData()

    }

    private fun initData() {
        val intent = intent
        leagueName = intent.getStringExtra("leagueName") ?: "English Premier League"
        leagueId = intent.getStringExtra("leagueId") ?: "4328"

        setToolbarBehavior()

        val bundle = Bundle()
        bundle.putString("leagueId", leagueId)

        leagueDetailInfoFragment.arguments = bundle
        leagueTableFragment.arguments = bundle
        leagueTeamsFragment.arguments = bundle

        setupViewPager(view_pager_league_detail)

        tab_layout_league_detail.setupWithViewPager(view_pager_league_detail)

    }

    private fun setupViewPager(viewPager: ViewPager) {
        leagueDetailViewPagerAdapter = LeagueDetailViewPagerAdapter(supportFragmentManager)
        leagueDetailViewPagerAdapter.addFragment(leagueDetailInfoFragment, "Info")
        leagueDetailViewPagerAdapter.addFragment(leagueTableFragment, "Table")
        leagueDetailViewPagerAdapter.addFragment(leagueTeamsFragment, "Teams")

        viewPager.adapter = leagueDetailViewPagerAdapter

        viewPager.offscreenPageLimit = 2

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

                val fragmentToShow =
                    leagueDetailViewPagerAdapter.getItem(newPosition)
                fragmentToShow.onResume()

                val fragmentToHide =
                    leagueDetailViewPagerAdapter.getItem(currentPosition)
                fragmentToHide.onPause()

                currentPosition = newPosition

            }

        })
    }

    private fun setToolbarBehavior() {
        setSupportActionBar(toolbar_league_detail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = leagueName
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
}