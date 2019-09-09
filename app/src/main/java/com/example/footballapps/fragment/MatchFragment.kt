package com.example.footballapps.fragment


import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.footballapps.R
import com.example.footballapps.activity.FootballGameInfoActivity
import com.example.footballapps.activity.SearchInfoActivity
import com.example.footballapps.adapter.MatchViewPagerAdapter
import com.example.footballapps.lifecycle.FragmentLifecycle
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_match.*
import org.jetbrains.anko.startActivity

class MatchFragment : Fragment() {

    private lateinit var matchViewPagerAdapter: MatchViewPagerAdapter

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
        matchViewPagerAdapter.addFragment(LastMatchFragment(), "Last Match")
        matchViewPagerAdapter.addFragment(NextMatchFragment(), "Next Match")
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

            var currentPosition = 0

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

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {

        inflater?.inflate(R.menu.menu_search, menu)

        val scheduleSearchItem: MenuItem? = menu!!.findItem(R.id.action_search)

        val scheduleSearchManager: SearchManager =
            context?.getSystemService(Context.SEARCH_SERVICE) as SearchManager

        var scheduleSearchView: SearchView? = null

        if (scheduleSearchItem != null) {
            scheduleSearchView = scheduleSearchItem.actionView as SearchView
        }

        scheduleSearchView?.setSearchableInfo(scheduleSearchManager.getSearchableInfo(activity?.componentName))

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.action_search) {
            activity?.invalidateOptionsMenu()
            context?.startActivity<SearchInfoActivity>()
        }
        return super.onOptionsItemSelected(item)
    }


}
