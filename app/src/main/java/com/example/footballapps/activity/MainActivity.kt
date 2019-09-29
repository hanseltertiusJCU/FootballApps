package com.example.footballapps.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.footballapps.R
import com.example.footballapps.fragment.FavoriteFragment
import com.example.footballapps.fragment.LeagueFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var leagueFragment = LeagueFragment()
    private var favoriteFragment = FavoriteFragment()

    private var activeFragment: Fragment = leagueFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar_main)

        setListener()

        initializeFragments()

    }

    private fun setListener() {
        main_bottom_navigation.setOnNavigationItemSelectedListener { item ->
            val previousItem = main_bottom_navigation.selectedItemId
            val nextItem = item.itemId
            if (previousItem != nextItem) {
                when (nextItem) {
                    R.id.menu_item_leagues -> {
                        loadLeagueFragment()
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
        supportFragmentManager.beginTransaction().add(
            R.id.main_fragment_container,
            leagueFragment,
            leagueFragment::class.java.simpleName
        ).commit()

        supportFragmentManager.beginTransaction().add(
            R.id.main_fragment_container,
            favoriteFragment,
            favoriteFragment::class.java.simpleName
        ).hide(favoriteFragment).commit()

        supportActionBar?.title = getString(R.string.leagues)
    }

    private fun loadLeagueFragment() {
        supportFragmentManager.beginTransaction().hide(activeFragment).show(leagueFragment).commit()
        activeFragment = leagueFragment

        supportActionBar?.title = getString(R.string.leagues)
    }

    private fun loadFavoritesFragment() {
        supportFragmentManager.beginTransaction().hide(activeFragment).show(favoriteFragment)
            .commit()
        activeFragment = favoriteFragment

        supportActionBar?.title = getString(R.string.favorites)
    }

}
