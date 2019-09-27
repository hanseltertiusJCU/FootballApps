package com.example.footballapps.fragment


import android.app.SearchManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.footballapps.R
import com.example.footballapps.activity.MatchDetailActivity
import com.example.footballapps.adapter.FavoriteMatchRecyclerViewAdapter
import com.example.footballapps.favorite.FavoriteMatchItem
import com.example.footballapps.lifecycle.FragmentLifecycle
import com.example.footballapps.presenter.FavoriteMatchPresenter
import com.example.footballapps.utils.gone
import com.example.footballapps.utils.invisible
import com.example.footballapps.utils.visible
import com.example.footballapps.view.FavoriteMatchView
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.constraint.layout.matchConstraint
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.onRefresh
import org.jetbrains.anko.support.v4.swipeRefreshLayout


class FavoriteMatchFragment : Fragment(), AnkoComponent<Context>, FavoriteMatchView, FragmentLifecycle {

    private lateinit var favoriteMatchRecyclerView: RecyclerView
    private lateinit var favoriteMatchProgressBar: ProgressBar
    private lateinit var favoriteMatchErrorText: TextView

    private var favoriteMatches: MutableList<FavoriteMatchItem> = mutableListOf()
    private lateinit var favoriteMatchRvAdapter: FavoriteMatchRecyclerViewAdapter

    private lateinit var favoriteMatchPresenter: FavoriteMatchPresenter

    private var favoriteMatchSearchView: SearchView? = null
    private var favoriteMatchSearchItem: MenuItem? = null

    private var isDataLoading = false
    private var isSearching = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return createView(AnkoContext.create(requireContext()))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if(savedInstanceState == null){
            initData()
        }

    }

    private fun initData() {

        favoriteMatchRvAdapter = FavoriteMatchRecyclerViewAdapter(context!!, favoriteMatches) {
            context?.startActivity<MatchDetailActivity>(
                "favMatchItem" to it
            )
        }

        favoriteMatchRecyclerView.adapter = favoriteMatchRvAdapter

        favoriteMatchPresenter = FavoriteMatchPresenter(this, context!!)
    }

    override fun onResume() {
        super.onResume()
        if (isSearching) {
            getFavoriteDataFromQuery(favoriteMatchSearchView?.query.toString())
        } else {
            getFavoriteData()
        }
    }

    override fun createView(ui: AnkoContext<Context>): View = with(ui) {
        constraintLayout {
            id = R.id.favorite_match_parent_layout
            lparams(width = matchParent, height = matchParent)

            favoriteMatchRecyclerView = recyclerView {
                id = R.id.rv_favorite_match
                lparams(width = matchParent, height = wrapContent)
                layoutManager = LinearLayoutManager(context)
            }

            favoriteMatchProgressBar = progressBar().lparams {
                topToTop = R.id.favorite_match_parent_layout
                leftToLeft = R.id.favorite_match_parent_layout
                rightToRight = R.id.favorite_match_parent_layout
                bottomToBottom = R.id.favorite_match_parent_layout
            }

            favoriteMatchErrorText = themedTextView(R.style.text_content).lparams {
                topToTop = R.id.favorite_match_parent_layout
                leftToLeft = R.id.favorite_match_parent_layout
                rightToRight = R.id.favorite_match_parent_layout
                bottomToBottom = R.id.favorite_match_parent_layout
            }
        }
    }


    override fun dataIsLoading() {
        favoriteMatchProgressBar.visible()
        favoriteMatchErrorText.gone()
        favoriteMatchRecyclerView.invisible()

        isDataLoading = true
    }

    override fun dataLoadingFinished() {
        favoriteMatchProgressBar.gone()
        favoriteMatchErrorText.gone()
        favoriteMatchRecyclerView.visible()

        isDataLoading = false
    }

    override fun dataFailedToLoad() {
        favoriteMatchProgressBar.gone()
        favoriteMatchErrorText.visible()
        favoriteMatchRecyclerView.invisible()

        isDataLoading = false

        favoriteMatchErrorText.text = resources.getString(R.string.no_data_to_show)
    }

    override fun showMatchData(favoriteMatchList: List<FavoriteMatchItem>) {
        favoriteMatches.clear()
        favoriteMatches.addAll(favoriteMatchList)
        favoriteMatchRvAdapter.notifyDataSetChanged()
    }

    private fun getFavoriteData() {
        favoriteMatchPresenter.getFavoriteMatchInfo()
    }

    private fun getFavoriteDataFromQuery(query: String) {
        favoriteMatchPresenter.getFavoriteMatchInfoSearchResult(query)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            favoriteMatchSearchItem?.collapseActionView()
        }
    }

    override fun onPauseFragment() {
        favoriteMatchSearchItem?.collapseActionView()
    }

    override fun onResumeFragment() {}

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.menu_search, menu)

        favoriteMatchSearchItem = menu.findItem(R.id.action_search)

        val favoriteSearchManager: SearchManager =
            context?.getSystemService(Context.SEARCH_SERVICE) as SearchManager

        if (favoriteMatchSearchItem != null) {
            favoriteMatchSearchView = favoriteMatchSearchItem?.actionView as SearchView

            favoriteMatchSearchItem?.setOnActionExpandListener(object :
                MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                    isSearching = true
                    getFavoriteDataFromQuery(favoriteMatchSearchView?.query.toString())
                    return true
                }

                override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                    isSearching = false
                    getFavoriteData()
                    return true
                }

            })

            favoriteMatchSearchView?.setOnQueryTextListener(object :
                SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (!isDataLoading) {
                        getFavoriteDataFromQuery(query!!)
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }

            })


        }

        favoriteMatchSearchView?.setSearchableInfo(favoriteSearchManager.getSearchableInfo(activity?.componentName))

        super.onCreateOptionsMenu(menu, inflater)
    }

}
