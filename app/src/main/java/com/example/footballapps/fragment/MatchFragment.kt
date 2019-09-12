package com.example.footballapps.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.footballapps.R
import com.example.footballapps.activity.FootballGameInfoActivity
import com.example.footballapps.adapter.MatchViewPagerAdapter
import com.example.footballapps.lifecycle.FragmentLifecycle
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_match.*

class MatchFragment : Fragment() {

    lateinit var matchViewPagerAdapter: MatchViewPagerAdapter
    var currentPosition = 0

    var lastMatchFragment = LastMatchFragment()
    var nextMatchFragment = NextMatchFragment()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_match, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewPager(view_pager_match_schedule)

        tab_layout_match_schedule.setupWithViewPager(view_pager_match_schedule)
    }

    private fun setupViewPager(viewPager: ViewPager) {
        matchViewPagerAdapter = MatchViewPagerAdapter(childFragmentManager)
        matchViewPagerAdapter.addFragment(lastMatchFragment, "Last Match")
        matchViewPagerAdapter.addFragment(nextMatchFragment, "Next Match")
        viewPager.adapter = matchViewPagerAdapter

        setListener()
    }

    private fun setListener() {
        view_pager_match_schedule.addOnPageChangeListener(
            TabLayout.TabLayoutOnPageChangeListener(
                tab_layout_match_schedule
            )
        )

        tab_layout_match_schedule.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    1 -> {
                        view_pager_match_schedule.currentItem = 1
                        (activity as FootballGameInfoActivity).supportActionBar?.title =
                            matchViewPagerAdapter.getPageTitle(1)
                    }
                    else -> {
                        view_pager_match_schedule.currentItem = 0
                        (activity as FootballGameInfoActivity).supportActionBar?.title =
                            matchViewPagerAdapter.getPageTitle(0)
                    }

                }

                val newPosition = tab?.position!!

                val fragmentToShow = matchViewPagerAdapter.getItem(newPosition) as FragmentLifecycle
                fragmentToShow.onResumeFragment()

                val fragmentToHide =
                    matchViewPagerAdapter.getItem(currentPosition) as FragmentLifecycle
                fragmentToHide.onPauseFragment()

                currentPosition = newPosition

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

        })
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            when (currentPosition) {
                1 -> nextMatchFragment.nextMatchSearchItem?.collapseActionView()
                else -> lastMatchFragment.lastMatchSearchItem?.collapseActionView()
            }
        }
    }


}
