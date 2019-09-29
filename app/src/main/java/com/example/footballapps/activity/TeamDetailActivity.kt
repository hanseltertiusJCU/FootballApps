package com.example.footballapps.activity

import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.example.footballapps.R
import com.example.footballapps.adapter.ViewPagerAdapter
import com.example.footballapps.favorite.FavoriteTeamItem
import com.example.footballapps.fragment.TeamDetailInfoFragment
import com.example.footballapps.fragment.TeamMatchesFragment
import com.example.footballapps.fragment.TeamPlayersFragment
import com.example.footballapps.helper.database
import com.example.footballapps.lifecycle.FragmentLifecycle
import com.example.footballapps.model.TeamItem
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_team_detail.*
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.delete
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select
import org.jetbrains.anko.design.snackbar
import java.lang.StringBuilder
import kotlin.math.abs

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class TeamDetailActivity : AppCompatActivity(), AppBarLayout.OnOffsetChangedListener {

    private lateinit var teamName: String
    private lateinit var teamId: String

    private lateinit var teamDetailViewPagerAdapter: ViewPagerAdapter

    private val teamDetailInfoFragment = TeamDetailInfoFragment()
    private val teamMatchesFragment = TeamMatchesFragment()
    private val teamPlayersFragment = TeamPlayersFragment()

    private var teamItem: TeamItem? = null
    private var favTeamItem: FavoriteTeamItem? = null

    private var currentPosition: Int = 0

    private var menu: Menu? = null
    private var isTeamFavorite: Boolean = false

    var favoriteMenuItem : MenuItem? = null

    // todo : isshow jadi isexpanded saja
    private var isShow = true
    private var scrollRange = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_detail)

        initData()
    }

    private fun initData() {
        val intent = intent
        teamItem = intent.getParcelableExtra("teamItem")
        favTeamItem = intent.getParcelableExtra("favoriteTeamItem")

        // todo : mungkin pake beberapa variable untuk placeholder, team nya pake arsenal, trus jadiin functionnya
        when {
            teamItem != null -> {
                teamName = teamItem?.teamName ?: ""
                teamId = teamItem?.teamId ?: ""
                Glide.with(applicationContext).load(teamItem?.teamBadge).placeholder(R.drawable.team_badge_placeholder).into(iv_team_detail_logo)
                tv_team_detail_title.text = teamItem?.teamName
                tv_team_detail_established.text = StringBuilder("est. ${teamItem?.teamFormedYear}")
                tv_team_detail_origin.text = StringBuilder("Based in ${teamItem?.teamCountry}")
            }
            favTeamItem != null -> {
                teamName = favTeamItem?.teamName ?: ""
                teamId = favTeamItem?.idTeam ?: ""
                Glide.with(applicationContext).load(favTeamItem?.teamBadgeUrl).placeholder(R.drawable.team_badge_placeholder).into(iv_team_detail_logo)
                tv_team_detail_title.text = favTeamItem?.teamName
                tv_team_detail_established.text = StringBuilder("est. ${favTeamItem?.teamFormedYear}")
                tv_team_detail_origin.text = StringBuilder("Based in ${favTeamItem?.teamCountry}")
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

    override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {

        if(scrollRange == -1){
            scrollRange = appBarLayout.totalScrollRange
        }

        when {
            scrollRange + verticalOffset == 0 -> {
                collapsing_toolbar_layout_team_detail.title = teamName
                isShow = true
            }
            isShow -> {
                collapsing_toolbar_layout_team_detail.title = " "
                isShow = false
            }
            abs(verticalOffset) == appBarLayout.totalScrollRange -> iv_team_detail_logo.contentDescription = getString(
                            R.string.team_detail_logo_collapsed)
            verticalOffset == 0 -> iv_team_detail_logo.contentDescription = getString(R.string.team_detail_logo_expanded)
            else -> iv_team_detail_logo.contentDescription = getString(R.string.team_detail_logo_collapsing)
        }

    }

    private fun setupViewPager(viewPager: ViewPager) {
        teamDetailViewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
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
                val newPosition = tab?.position!!

                if(currentPosition == 1) {
                    val fragmentToHide =
                        teamDetailViewPagerAdapter.getItem(currentPosition) as FragmentLifecycle
                    fragmentToHide.onPauseFragment()
                }

                currentPosition = newPosition
            }

        })
    }

    override fun onResume() {
        super.onResume()
        team_detail_app_bar_layout.addOnOffsetChangedListener(this)
    }

    override fun onPause() {
        super.onPause()
        team_detail_app_bar_layout.removeOnOffsetChangedListener(this)
    }

    private fun setToolbarBehavior() {
        setSupportActionBar(toolbar_team_detail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_favorite, menu)
        this.menu = menu
        favoriteMenuItem = menu?.findItem(R.id.action_add_to_favorite)
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
                // todo : ga usah pake fav team item null or team item null
                if (favTeamItem != null || teamItem != null) {
                    changeFavoriteTeamState()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun addTeamToFavoriteTeams() {
        try {
            // todo : tinggal pake variable saja daripada pake fav team item ataupun team item
            when {
                favTeamItem != null -> {
                    database.use {
                        insert(
                            FavoriteTeamItem.TABLE_FAVORITE_TEAM,
                            FavoriteTeamItem.TEAM_ID to favTeamItem?.idTeam,
                            FavoriteTeamItem.TEAM_NAME to favTeamItem?.teamName,
                            FavoriteTeamItem.TEAM_BADGE_URL to favTeamItem?.teamBadgeUrl,
                            FavoriteTeamItem.TEAM_FORMED_YEAR to favTeamItem?.teamFormedYear,
                            FavoriteTeamItem.TEAM_COUNTRY to favTeamItem?.teamCountry
                        )
                    }
                }
                teamItem != null -> {
                    database.use {
                        insert(
                            FavoriteTeamItem.TABLE_FAVORITE_TEAM,
                            FavoriteTeamItem.TEAM_ID to teamItem?.teamId,
                            FavoriteTeamItem.TEAM_NAME to teamItem?.teamName,
                            FavoriteTeamItem.TEAM_BADGE_URL to teamItem?.teamBadge,
                            FavoriteTeamItem.TEAM_FORMED_YEAR to teamItem?.teamFormedYear,
                            FavoriteTeamItem.TEAM_COUNTRY to teamItem?.teamCountry
                        )
                    }
                }
            }
            team_detail_coordinator_layout.snackbar("Add a team into favorites").setAction(getString(R.string.undo)){changeFavoriteTeamState()}.show()
        } catch (e: SQLiteConstraintException) {
            team_detail_coordinator_layout.snackbar(e.localizedMessage).show()
        }
    }

    private fun removeTeamFromFavoriteTeams() {
        try {
            database.use {
                delete(
                    FavoriteTeamItem.TABLE_FAVORITE_TEAM,
                    "(TEAM_ID = {teamId})",
                    "teamId" to teamId
                )
            }
            team_detail_coordinator_layout.snackbar("Remove a team from favorites").setAction(getString(R.string.undo)){changeFavoriteTeamState()}.show()
        } catch (e: SQLiteConstraintException) {
            team_detail_coordinator_layout.snackbar(e.localizedMessage).show()
        }
    }

    private fun setFavoriteTeamIcon() {
        if (isTeamFavorite) {
            this.menu?.getItem(0)?.icon =
                ContextCompat.getDrawable(this, R.drawable.ic_added_to_favorites)
        } else {
            this.menu?.getItem(0)?.icon =
                ContextCompat.getDrawable(this, R.drawable.ic_add_to_favorites)
        }
    }

    private fun changeFavoriteTeamState() {
        if (isTeamFavorite) removeTeamFromFavoriteTeams() else addTeamToFavoriteTeams()

        isTeamFavorite = !isTeamFavorite
        setFavoriteTeamIcon()
    }

    private fun checkFavoriteTeamState() {
        database.use {
            val result = select(FavoriteTeamItem.TABLE_FAVORITE_TEAM).whereArgs(
                "(TEAM_ID = {teamId})",
                "teamId" to teamId
            )
            val favorite = result.parseList(classParser<FavoriteTeamItem>())
            if (favorite.isNotEmpty()) isTeamFavorite = true
        }
    }
}
