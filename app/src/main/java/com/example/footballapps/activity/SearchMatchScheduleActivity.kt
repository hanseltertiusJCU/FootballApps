package com.example.footballapps.activity

import android.app.SearchManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import com.example.footballapps.R
import kotlinx.android.synthetic.main.activity_search_match_schedule.*

class SearchMatchScheduleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_match_schedule)

        setSupportActionBar(toolbar_search_match_schedule)
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
        }

        searchScheduleSearchView?.setSearchableInfo(searchScheduleSearchManager.getSearchableInfo(this@SearchMatchScheduleActivity.componentName))

        return super.onCreateOptionsMenu(menu)
    }
}
