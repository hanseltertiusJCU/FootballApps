package com.example.footballapps.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.footballapps.R
import com.example.footballapps.adapter.TeamDetailViewPagerAdapter
import com.example.footballapps.fragment.TeamDetailInfoFragment
import com.example.footballapps.fragment.TeamMatchesFragment
import com.example.footballapps.fragment.TeamPlayersFragment
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_team_detail.*

class TeamDetailActivity : AppCompatActivity() {

    private lateinit var teamName : String
    private lateinit var teamId : String

    // todo : fragmentpageradapter ganti jadi teamdetailviewpageradapter, mungkin viewpageradapter bisa di jadiin 1 aja
    private lateinit var teamDetailViewPagerAdapter : TeamDetailViewPagerAdapter

    private val teamDetailInfoFragment = TeamDetailInfoFragment()
    private val teamMatchesFragment = TeamMatchesFragment()
    private val teamPlayersFragment = TeamPlayersFragment()

    private var currentPosition : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_detail)

        initData()
    }

    private fun initData() {
        val intent = intent
        // todo : intentnya tinggal bawa parcelable
        teamName = intent.getStringExtra("teamName") ?: "Arsenal"
        teamId = intent.getStringExtra("teamId") ?: "133604"

        setToolbarBehavior()

        val bundle = Bundle()
        bundle.putString("teamId", teamId)

        // todo: put bundle into the fragment
        teamDetailInfoFragment.arguments = bundle
        teamMatchesFragment.arguments = bundle
        teamPlayersFragment.arguments = bundle

        setupViewPager(view_pager_team_detail)

        tab_layout_team_detail.setupWithViewPager(view_pager_team_detail)

    }

    private fun setupViewPager(viewPager : ViewPager){
        teamDetailViewPagerAdapter = TeamDetailViewPagerAdapter(supportFragmentManager)
        teamDetailViewPagerAdapter.addFragment(teamDetailInfoFragment, "Info")
        teamDetailViewPagerAdapter.addFragment(teamMatchesFragment, "Matches")
        teamDetailViewPagerAdapter.addFragment(teamPlayersFragment, "Players")

        viewPager.adapter = teamDetailViewPagerAdapter

        viewPager.offscreenPageLimit = 2

        setListener()
    }

    private fun setListener(){
        view_pager_team_detail.addOnPageChangeListener(
            TabLayout.TabLayoutOnPageChangeListener(tab_layout_team_detail)
        )

        tab_layout_team_detail.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {

            }

        })
    }

    // todo: set toolbar behavior
    private fun setToolbarBehavior(){
        setSupportActionBar(toolbar_team_detail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = teamName
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
