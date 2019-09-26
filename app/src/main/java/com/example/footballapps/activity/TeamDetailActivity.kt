package com.example.footballapps.activity

import android.database.sqlite.SQLiteConstraintException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.footballapps.R
import com.example.footballapps.adapter.TeamDetailViewPagerAdapter
import com.example.footballapps.favorite.FavoriteTeamItem
import com.example.footballapps.fragment.TeamDetailInfoFragment
import com.example.footballapps.fragment.TeamMatchesFragment
import com.example.footballapps.fragment.TeamPlayersFragment
import com.example.footballapps.helper.database
import com.example.footballapps.model.TeamItem
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_team_detail.*
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.delete
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select
import org.jetbrains.anko.design.snackbar

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class TeamDetailActivity : AppCompatActivity() {

    private lateinit var teamName : String
    private lateinit var teamId : String

    // todo : fragmentpageradapter ganti jadi teamdetailviewpageradapter, mungkin viewpageradapter bisa di jadiin 1 aja
    private lateinit var teamDetailViewPagerAdapter : TeamDetailViewPagerAdapter

    private val teamDetailInfoFragment = TeamDetailInfoFragment()
    private val teamMatchesFragment = TeamMatchesFragment()
    private val teamPlayersFragment = TeamPlayersFragment()

    private var teamItem : TeamItem? = null
    private var favTeamItem : FavoriteTeamItem? = null

    private var currentPosition : Int = 0

    // todo : rapiin beberapa section dari code
    private var menuItem : Menu? = null
    private var isEventFavorite : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_detail)

        initData()
    }

    private fun initData() {
        val intent = intent
        teamItem = intent.getParcelableExtra<TeamItem>("teamItem")
        favTeamItem = intent.getParcelableExtra<FavoriteTeamItem>("favoriteTeamItem")

        when {
            teamItem != null -> {
                teamName = teamItem?.teamName ?: ""
                teamId = teamItem?.teamId ?: ""
            }
            favTeamItem != null -> {
                teamName = favTeamItem?.teamName ?: ""
                teamId = favTeamItem?.idTeam ?: ""
            }
            else -> {
                teamName = ""
                teamId = ""
            }
        }

        setToolbarBehavior()

        val bundle = Bundle()
        bundle.putString("teamId", teamId)

        teamDetailInfoFragment.arguments = bundle
        teamMatchesFragment.arguments = bundle
        teamPlayersFragment.arguments = bundle

        setupViewPager(view_pager_team_detail)

        tab_layout_team_detail.setupWithViewPager(view_pager_team_detail)

        checkFavoriteTeamState()

    }

    private fun setupViewPager(viewPager : ViewPager){
        teamDetailViewPagerAdapter = TeamDetailViewPagerAdapter(supportFragmentManager)
        teamDetailViewPagerAdapter.addFragment(teamDetailInfoFragment, "Info")
        teamDetailViewPagerAdapter.addFragment(teamMatchesFragment, "Matches")
        teamDetailViewPagerAdapter.addFragment(teamPlayersFragment, "Players")

        viewPager.adapter = teamDetailViewPagerAdapter

        viewPager.offscreenPageLimit = 2

    }

    private fun setToolbarBehavior(){
        setSupportActionBar(toolbar_team_detail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = teamName
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_favorite, menu)
        menuItem = menu
        setFavoriteTeamIcon()
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_add_to_favorite -> {
                if(favTeamItem != null || teamItem != null){
                    if(isEventFavorite) removeTeamFromFavoriteTeams() else addTeamToFavoriteTeams()

                    isEventFavorite = !isEventFavorite
                    setFavoriteTeamIcon()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun addTeamToFavoriteTeams(){
        try {
            when {
                favTeamItem != null -> {
                    database.use {
                        insert(FavoriteTeamItem.TABLE_FAVORITE_TEAM,
                            FavoriteTeamItem.TEAM_ID to favTeamItem?.idTeam,
                            FavoriteTeamItem.TEAM_NAME to favTeamItem?.teamName,
                            FavoriteTeamItem.TEAM_BADGE_URL to favTeamItem?.teamBadgeUrl
                        )
                    }
                }
                teamItem != null -> {
                    database.use {
                        insert(FavoriteTeamItem.TABLE_FAVORITE_TEAM,
                            FavoriteTeamItem.TEAM_ID to teamItem?.teamId,
                            FavoriteTeamItem.TEAM_NAME to teamItem?.teamName,
                            FavoriteTeamItem.TEAM_BADGE_URL to teamItem?.teamBadge)
                    }
                }
            }
            team_detail_coordinator_layout.snackbar("Add a team into favorites").show()
        } catch (e : SQLiteConstraintException) {
            team_detail_coordinator_layout.snackbar(e.localizedMessage).show()
        }
    }

    private fun removeTeamFromFavoriteTeams(){
        try {
            database.use {
                delete(FavoriteTeamItem.TABLE_FAVORITE_TEAM, "(TEAM_ID = {teamId})", "teamId" to teamId)
            }
            team_detail_coordinator_layout.snackbar("Remove a team from favorites").show()
        } catch (e : SQLiteConstraintException) {
            team_detail_coordinator_layout.snackbar(e.localizedMessage).show()
        }
    }

    private fun setFavoriteTeamIcon() {
        if(isEventFavorite){
            menuItem?.getItem(0)?.icon = ContextCompat.getDrawable(this, R.drawable.ic_added_to_favorites)
        } else {
            menuItem?.getItem(0)?.icon = ContextCompat.getDrawable(this, R.drawable.ic_add_to_favorites)
        }
    }

    private fun checkFavoriteTeamState(){
        database.use {
            val result = select(FavoriteTeamItem.TABLE_FAVORITE_TEAM).whereArgs("(TEAM_ID = {teamId})", "teamId" to teamId)
            val favorite = result.parseList(classParser<FavoriteTeamItem>())
            if(favorite.isNotEmpty()) isEventFavorite = true
        }
    }
}
