package com.example.footballapps.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.footballapps.R
import com.example.footballapps.fragment.FavoriteMatchFragment
import com.example.footballapps.fragment.MatchFragment
import kotlinx.android.synthetic.main.activity_football_game_info.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class FootballGameInfoActivity : AppCompatActivity() {

    lateinit var leagueName: String
    lateinit var leagueId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_football_game_info)

        setSupportActionBar(toolbar_football_game_info)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val intent = intent

        leagueName = intent.getStringExtra("leagueName")
        leagueId = intent.getStringExtra("leagueId")

        setListener(savedInstanceState)

        loadMatchFragment(savedInstanceState)

    }

    private fun setListener(savedInstanceState: Bundle?) {
        football_game_info_bottom_navigation.setOnNavigationItemSelectedListener { item ->
            val previousItem = football_game_info_bottom_navigation.selectedItemId
            val nextItem = item.itemId
            if (previousItem != nextItem) {
                when (nextItem) {
                    R.id.menu_item_match_schedule -> {
                        loadMatchFragment(savedInstanceState)
                    }
                    R.id.menu_item_favorite -> {
                        loadFavoritesFragment(savedInstanceState)
                    }
                }
            }
            true
        }
    }

    private fun loadMatchFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(
                    R.id.fragment_football_game_info_container,
                    MatchFragment(),
                    MatchFragment::class.java.simpleName
                )
                .commit()
        }
    }

    private fun loadFavoritesFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(
                    R.id.fragment_football_game_info_container,
                    FavoriteMatchFragment(),
                    FavoriteMatchFragment::class.java.simpleName
                )
                .commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

}
