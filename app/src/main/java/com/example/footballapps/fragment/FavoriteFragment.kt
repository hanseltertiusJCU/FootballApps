package com.example.footballapps.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.footballapps.R
import com.example.footballapps.activity.MainActivity
import com.example.footballapps.adapter.ViewPagerAdapter
import com.example.footballapps.lifecycle.FragmentLifecycle
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_favorite.*

class FavoriteFragment : Fragment() {

    lateinit var favoriteViewPagerAdapter: ViewPagerAdapter
    var currentPosition = 0

    private val favoriteMatchesFragment = FavoriteMatchFragment()
    private val favoriteTeamsFragment = FavoriteTeamFragment()

    private lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivity = activity as MainActivity

        setupViewPager(view_pager_favorite_fragment)

        tab_layout_favorite_fragment.setupWithViewPager(view_pager_favorite_fragment)

    }


    private fun setupViewPager(viewPager: ViewPager) {
        favoriteViewPagerAdapter = ViewPagerAdapter(childFragmentManager)
        favoriteViewPagerAdapter.addFragment(favoriteMatchesFragment, "Matches")
        favoriteViewPagerAdapter.addFragment(favoriteTeamsFragment, "Teams")
        viewPager.adapter = favoriteViewPagerAdapter

        setListener()
    }

    private fun setListener() {
        view_pager_favorite_fragment.addOnPageChangeListener(
            TabLayout.TabLayoutOnPageChangeListener(
                tab_layout_favorite_fragment
            )
        )

        tab_layout_favorite_fragment.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                view_pager_favorite_fragment.currentItem = tab?.position!!

                val newPosition = tab.position

                val fragmentToHide =
                    favoriteViewPagerAdapter.getItem(currentPosition) as FragmentLifecycle
                fragmentToHide.onPauseFragment()

                currentPosition = newPosition

            }

        })

    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            val fragmentToHide =
                favoriteViewPagerAdapter.getItem(currentPosition) as FragmentLifecycle
            fragmentToHide.onPauseFragment()
        }
    }


}
