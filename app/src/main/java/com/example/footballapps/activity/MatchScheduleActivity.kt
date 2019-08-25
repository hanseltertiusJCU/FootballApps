package com.example.footballapps.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.example.footballapps.R
import com.example.footballapps.adapter.MatchViewPagerAdapter
import com.example.footballapps.fragment.LastMatchFragment
import com.example.footballapps.fragment.NextMatchFragment
import kotlinx.android.synthetic.main.activity_match_schedule.*

class MatchScheduleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match_schedule)

        setSupportActionBar(toolbar_match_schedule)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupViewPager(view_pager_match_schedule)

        layout_tab_match_schedule.setupWithViewPager(view_pager_match_schedule)

    }

    private fun setupViewPager (viewPager : ViewPager) {
        val matchViewPagerAdapter = MatchViewPagerAdapter(supportFragmentManager)
        matchViewPagerAdapter.addFragment(LastMatchFragment(), "Last Match")
        matchViewPagerAdapter.addFragment(NextMatchFragment(), "Next Match")
        viewPager.adapter = matchViewPagerAdapter
    }
}
