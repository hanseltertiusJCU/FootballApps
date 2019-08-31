package com.example.footballapps.activity

import android.app.SearchManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.viewpager.widget.ViewPager
import com.example.footballapps.R
import com.example.footballapps.adapter.MatchViewPagerAdapter
import com.example.footballapps.fragment.LastMatchFragment
import com.example.footballapps.fragment.NextMatchFragment
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_match_schedule.*
import org.jetbrains.anko.startActivity
import com.example.footballapps.lifecycle.FragmentLifecycle


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MatchScheduleActivity : AppCompatActivity() {

    lateinit var leagueName: String
    lateinit var leagueId: String

    private val matchViewPagerAdapter = MatchViewPagerAdapter(supportFragmentManager)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match_schedule)

        setSupportActionBar(toolbar_match_schedule)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val intent = intent

        leagueName = intent.getStringExtra("leagueName")
        leagueId = intent.getStringExtra("leagueId")

        setupViewPager(view_pager_match_schedule)

        tab_layout_match_schedule.setupWithViewPager(view_pager_match_schedule)

    }

    private fun setupViewPager(viewPager: ViewPager) {
        matchViewPagerAdapter.addFragment(LastMatchFragment(), "Last Match")
        matchViewPagerAdapter.addFragment(NextMatchFragment(), "Next Match")
        viewPager.adapter = matchViewPagerAdapter

        setListener()
    }

    private fun setListener(){

        view_pager_match_schedule.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tab_layout_match_schedule))

        tab_layout_match_schedule.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            var currentPosition = 0

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position) {
                    1 -> {
                        view_pager_match_schedule.currentItem = 1
                        supportActionBar?.title = matchViewPagerAdapter.getPageTitle(1)
                    }
                    else -> {
                        view_pager_match_schedule.currentItem = 0
                        supportActionBar?.title = matchViewPagerAdapter.getPageTitle(0)
                    }

                }

                val newPosition = tab?.position!!

                val fragmentToShow = matchViewPagerAdapter.getItem(newPosition) as FragmentLifecycle
                fragmentToShow.onResumeFragment()

                val fragmentToHide = matchViewPagerAdapter.getItem(currentPosition) as FragmentLifecycle
                fragmentToHide.onPauseFragment()

                currentPosition = newPosition

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val menuInflater: MenuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_match_schedule, menu)

        val scheduleSearchItem: MenuItem? = menu!!.findItem(R.id.action_search)

        val scheduleSearchManager: SearchManager =
            this@MatchScheduleActivity.getSystemService(Context.SEARCH_SERVICE) as SearchManager

        var scheduleSearchView: SearchView? = null

        if (scheduleSearchItem != null) {
            scheduleSearchView = scheduleSearchItem.actionView as SearchView
        }

        scheduleSearchView?.setSearchableInfo(scheduleSearchManager.getSearchableInfo(this@MatchScheduleActivity.componentName))

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when {
            item?.itemId == android.R.id.home -> finish()
            item?.itemId == R.id.action_search -> {
                invalidateOptionsMenu()
                startActivity<SearchMatchScheduleActivity>()
            }
            item?.itemId == R.id.action_league_detail -> startActivity<LeagueDetailActivity>("leagueName" to leagueName, "leagueId" to leagueId)
        }
        return super.onOptionsItemSelected(item!!)
    }
}
