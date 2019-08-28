package com.example.footballapps.activity

import android.app.SearchManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
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
import kotlinx.android.synthetic.main.activity_search_match_schedule.*

class SearchMatchScheduleActivity : AppCompatActivity(), MatchView {

    private lateinit var searchResultMatchPresenter : MatchPresenter

    private var searchResultMatches : MutableList<MatchItem> = mutableListOf()
    private lateinit var searchResultMatchRvAdapter : MatchRecyclerViewAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_match_schedule)

        setSupportActionBar(toolbar_search_match_schedule)

        initData()
    }

    private fun initData() {

        searchResultMatchRvAdapter = MatchRecyclerViewAdapter(this, searchResultMatches)
        rv_search_match_schedule.adapter = searchResultMatchRvAdapter
        rv_search_match_schedule.layoutManager = LinearLayoutManager(this)

        searchResultMatchPresenter = MatchPresenter(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val menuInflater : MenuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_search, menu)

        val searchScheduleSearchItem : MenuItem? = menu!!.findItem(R.id.action_search)
        searchScheduleSearchItem?.expandActionView()

        val searchScheduleSearchManager : SearchManager = this@SearchMatchScheduleActivity.getSystemService(Context.SEARCH_SERVICE) as SearchManager

        var searchScheduleSearchView : SearchView? = null

        var isSearch: Boolean

        if(searchScheduleSearchItem != null) {
            searchScheduleSearchView = searchScheduleSearchItem.actionView as SearchView

            searchResultMatchPresenter.getSearchMatchInfo(searchScheduleSearchView.query.toString())

            searchScheduleSearchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                    isSearch = true
                    return true
                }

                override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                    isSearch = false
                    if(!isSearch) {
                        finish()
                    }
                    return true
                }

            })

            searchScheduleSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    searchResultMatchPresenter.getSearchMatchInfo(query!!)
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }

            })
        }

        searchScheduleSearchView?.setSearchableInfo(searchScheduleSearchManager.getSearchableInfo(this@SearchMatchScheduleActivity.componentName))

        return super.onCreateOptionsMenu(menu)
    }

    override fun dataIsLoading() {
        search_match_progress_bar.visible()
        search_match_error_data_text.gone()
        rv_search_match_schedule.invisible()
    }

    override fun dataLoadingFinished() {
        // todo: tinggal cek connectivity thing
        if(searchResultMatches.size == 0) {
            rv_search_match_schedule.invisible()
            search_match_error_data_text.visible()
            search_match_progress_bar.gone()
        } else {
            rv_search_match_schedule.visible()
            search_match_error_data_text.gone()
            search_match_progress_bar.gone()
        }
    }

    override fun showMatchData(matchList: List<MatchItem>) {
        searchResultMatches.clear()
        searchResultMatches.addAll(matchList)
        searchResultMatchRvAdapter.notifyDataSetChanged()
    }
}
