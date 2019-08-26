package com.example.footballapps.activity

import android.app.SearchManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.viewpager.widget.ViewPager
import com.example.footballapps.R
import com.example.footballapps.adapter.MatchViewPagerAdapter
import com.example.footballapps.fragment.LastMatchFragment
import com.example.footballapps.fragment.NextMatchFragment
import kotlinx.android.synthetic.main.activity_match_schedule.*
import org.jetbrains.anko.startActivity

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MatchScheduleActivity : AppCompatActivity() {

    lateinit var leagueName : String
    lateinit var leagueId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match_schedule)

        setSupportActionBar(toolbar_match_schedule)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val intent = intent

        leagueName = intent.getStringExtra("leagueName")
        leagueId = intent.getStringExtra("leagueId")

        setupViewPager(view_pager_match_schedule)

        layout_tab_match_schedule.setupWithViewPager(view_pager_match_schedule)

    }

    private fun setupViewPager (viewPager : ViewPager) {
        val matchViewPagerAdapter = MatchViewPagerAdapter(supportFragmentManager)
        matchViewPagerAdapter.addFragment(LastMatchFragment(), "Last Match")
        matchViewPagerAdapter.addFragment(NextMatchFragment(), "Next Match")
        viewPager.adapter = matchViewPagerAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val menuInflater : MenuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_search, menu)

        val scheduleSearchItem : MenuItem? = menu!!.findItem(R.id.action_search)

        val scheduleSearchManager : SearchManager = this@MatchScheduleActivity.getSystemService(Context.SEARCH_SERVICE) as SearchManager

        var scheduleSearchView : SearchView? = null

        if(scheduleSearchItem != null) {
            scheduleSearchView = scheduleSearchItem.actionView as SearchView
        }

        scheduleSearchView?.setSearchableInfo(scheduleSearchManager.getSearchableInfo(this@MatchScheduleActivity.componentName))

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == android.R.id.home){
            finish()
        } else if (item?.itemId == R.id.action_search) {
            invalidateOptionsMenu()
            startActivity<SearchMatchScheduleActivity>()
        }
        return super.onOptionsItemSelected(item!!)
    }
}
