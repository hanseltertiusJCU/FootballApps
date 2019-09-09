package com.example.footballapps.activity

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.footballapps.R
import com.example.footballapps.adapter.MatchRecyclerViewAdapter
import com.example.footballapps.model.MatchItem
import com.example.footballapps.presenter.MatchPresenter
import com.example.footballapps.utils.gone
import com.example.footballapps.utils.invisible
import com.example.footballapps.utils.visible
import com.example.footballapps.view.MatchView
import kotlinx.android.synthetic.main.activity_search_info.*
import org.jetbrains.anko.startActivity


class SearchInfoActivity : AppCompatActivity(), MatchView {

    private lateinit var searchResultMatchPresenter: MatchPresenter

    private var searchResultMatches: MutableList<MatchItem> = mutableListOf()
    private lateinit var searchResultMatchRvAdapter: MatchRecyclerViewAdapter

    private var isDataLoading = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_info)

        setSupportActionBar(toolbar_search_info)

        initData()
    }

    private fun initData() {

        searchResultMatchRvAdapter = MatchRecyclerViewAdapter(this, searchResultMatches) {
            startActivity<MatchDetailActivity>(
                "eventId" to it.idEvent,
                "eventName" to it.strEvent,
                "homeTeamId" to it.homeTeamId,
                "awayTeamId" to it.awayTeamId
            )
        }
        rv_search_info.adapter = searchResultMatchRvAdapter
        rv_search_info.layoutManager = LinearLayoutManager(this)

        searchResultMatchPresenter = MatchPresenter(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val menuInflater: MenuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_search, menu)

        val searchScheduleSearchItem: MenuItem? = menu!!.findItem(R.id.action_search)
        searchScheduleSearchItem?.expandActionView()

        val searchScheduleSearchManager: SearchManager =
            this@SearchInfoActivity.getSystemService(Context.SEARCH_SERVICE) as SearchManager

        var searchScheduleSearchView: SearchView? = null

        var isSearch: Boolean

        if (searchScheduleSearchItem != null) {
            searchScheduleSearchView = searchScheduleSearchItem.actionView as SearchView

            searchResultMatchPresenter.getSearchMatchInfo(searchScheduleSearchView.query.toString())

            searchScheduleSearchItem.setOnActionExpandListener(object :
                MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                    isSearch = true
                    return true
                }

                override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                    isSearch = false
                    if (!isSearch) {
                        finish()
                    }
                    return true
                }

            })

            searchScheduleSearchView.setOnQueryTextListener(object :
                SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (!isDataLoading) {
                        searchResultMatchPresenter.getSearchMatchInfo(query!!)
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }

            })
        }

        searchScheduleSearchView?.setSearchableInfo(
            searchScheduleSearchManager.getSearchableInfo(
                this@SearchInfoActivity.componentName
            )
        )

        return super.onCreateOptionsMenu(menu)
    }

    override fun dataIsLoading() {
        isDataLoading = true
        search_info_progress_bar.visible()
        search_info_error_data_text.gone()
        rv_search_info.invisible()
    }

    override fun dataLoadingFinished() {
        when {
            searchResultMatches.size == 0 -> {
                rv_search_info.invisible()
                search_info_error_data_text.visible()
                search_info_progress_bar.gone()

                search_info_error_data_text.text = resources.getString(R.string.no_data_to_show)

                isDataLoading = false
            }
            else -> {
                rv_search_info.visible()
                search_info_error_data_text.gone()
                search_info_progress_bar.gone()

                isDataLoading = false
            }
        }

    }

    override fun dataFailedToLoad(errorText: String) {
        rv_search_info.invisible()
        search_info_error_data_text.visible()
        search_info_progress_bar.gone()

        search_info_error_data_text.text = errorText

        isDataLoading = false
    }

    override fun showMatchData(matchList: List<MatchItem>) {
        searchResultMatches.clear()
        searchResultMatches.addAll(matchList)
        searchResultMatchRvAdapter.notifyDataSetChanged()
    }
}
