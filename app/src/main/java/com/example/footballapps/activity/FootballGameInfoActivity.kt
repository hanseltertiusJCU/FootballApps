package com.example.footballapps.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.footballapps.R
import com.example.footballapps.fragment.FavoriteMatchFragment
import com.example.footballapps.fragment.MatchFragment
import kotlinx.android.synthetic.main.activity_football_game_info.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class FootballGameInfoActivity : AppCompatActivity() {

    lateinit var leagueName: String
    lateinit var leagueId: String

    private var matchFragment = MatchFragment()
    private var favoriteFragment = FavoriteMatchFragment()

    private var activeFragment: Fragment = matchFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_football_game_info)

        setSupportActionBar(toolbar_football_game_info)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val intent = intent

        leagueName = intent.getStringExtra("leagueName") ?: "English Premier League"
        leagueId = intent.getStringExtra("leagueId") ?: "4328"

        setListener()

        initializeFragments()

    }

    private fun setListener() {
        football_game_info_bottom_navigation.setOnNavigationItemSelectedListener { item ->
            val previousItem = football_game_info_bottom_navigation.selectedItemId
            val nextItem = item.itemId
            if (previousItem != nextItem) {
                when (nextItem) {
                    R.id.menu_item_match_schedule -> {
                        loadMatchFragment()
                    }
                    R.id.menu_item_favorite -> {
                        loadFavoritesFragment()
                    }
                }
            }
            true
        }
    }

    private fun initializeFragments() {
        supportFragmentManager.beginTransaction()
            .add(
                R.id.fragment_football_game_info_container,
                matchFragment,
                matchFragment::class.java.simpleName
            )
            .commit()

        supportFragmentManager.beginTransaction()
            .add(
                R.id.fragment_football_game_info_container,
                favoriteFragment,
                favoriteFragment::class.java.simpleName
            )
            .hide(favoriteFragment)
            .commit()
    }

    private fun loadMatchFragment() {
        supportFragmentManager.beginTransaction()
            .hide(activeFragment)
            .show(matchFragment)
            .commit()
        activeFragment = matchFragment

        supportActionBar?.title =
            matchFragment.matchViewPagerAdapter.getPageTitle(matchFragment.currentPosition)

    }

    private fun loadFavoritesFragment() {
        supportFragmentManager.beginTransaction()
            .hide(activeFragment)
            .show(favoriteFragment)
            .commit()
        activeFragment = favoriteFragment

        supportActionBar?.title = "Favorites"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

}
