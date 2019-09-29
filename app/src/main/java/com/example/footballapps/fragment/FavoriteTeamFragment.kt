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
import androidx.fragment.app.Fragment
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.footballapps.R
import com.example.footballapps.activity.TeamDetailActivity
import com.example.footballapps.adapter.FavoriteTeamRecyclerViewAdapter
import com.example.footballapps.favorite.FavoriteTeamItem
import com.example.footballapps.lifecycle.FragmentLifecycle
import com.example.footballapps.presenter.FavoriteTeamPresenter
import com.example.footballapps.utils.GridSpacingItemDecoration
import com.example.footballapps.utils.gone
import com.example.footballapps.utils.invisible
import com.example.footballapps.utils.visible
import com.example.footballapps.view.FavoriteTeamView
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.constraint.layout.matchConstraint
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.support.v4.onRefresh
import org.jetbrains.anko.support.v4.swipeRefreshLayout

class FavoriteTeamFragment : Fragment(), AnkoComponent<Context>, FavoriteTeamView, FragmentLifecycle {

    private lateinit var favoriteTeamRecyclerView : RecyclerView
    private lateinit var favoriteTeamProgressBar : ProgressBar
    private lateinit var favoriteTeamErrorText : TextView

    private var favoriteTeams : MutableList<FavoriteTeamItem> = mutableListOf()
    private lateinit var favoriteTeamRvAdapter : FavoriteTeamRecyclerViewAdapter

    private lateinit var favoriteTeamPresenter : FavoriteTeamPresenter

    private var favoriteTeamSearchView : SearchView? = null
    private var favoriteTeamSearchItem : MenuItem? = null

    private var isDataLoading = false
    private var isSearching = false

    private val spanCount = 2
    private val includeEdge = true

    private var searchQuery : String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return createView(AnkoContext.create(requireContext()))
    }

    override fun createView(ui: AnkoContext<Context>): View = with(ui) {
        constraintLayout {
            id = R.id.favorite_team_parent_layout
            lparams(width = matchParent, height = matchParent)

            favoriteTeamRecyclerView = recyclerView {
                id = R.id.rv_favorite_team
                lparams(width = matchParent, height = wrapContent)
            }

            favoriteTeamProgressBar = progressBar().lparams{
                topToTop = R.id.favorite_team_parent_layout
                leftToLeft = R.id.favorite_team_parent_layout
                rightToRight = R.id.favorite_team_parent_layout
                bottomToBottom = R.id.favorite_team_parent_layout
            }

            favoriteTeamErrorText = themedTextView(R.style.text_content).lparams {
                topToTop = R.id.favorite_team_parent_layout
                leftToLeft = R.id.favorite_team_parent_layout
                rightToRight = R.id.favorite_team_parent_layout
                bottomToBottom = R.id.favorite_team_parent_layout
            }

        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initData()
    }

    override fun dataIsLoading() {

        favoriteTeamProgressBar.visible()
        favoriteTeamErrorText.gone()
        favoriteTeamRecyclerView.invisible()

        isDataLoading = true
    }

    override fun dataLoadingFinished() {
        favoriteTeamProgressBar.gone()
        favoriteTeamErrorText.gone()
        favoriteTeamRecyclerView.visible()

        isDataLoading = false
    }

    override fun dataFailedToLoad() {
        favoriteTeamProgressBar.gone()
        favoriteTeamErrorText.visible()
        favoriteTeamRecyclerView.invisible()

        isDataLoading = false

        favoriteTeamErrorText.text = resources.getString(R.string.no_data_to_show)
    }

    override fun showTeamData(favoriteTeamList: List<FavoriteTeamItem>) {
        favoriteTeams.clear()
        favoriteTeams.addAll(favoriteTeamList)
        favoriteTeamRvAdapter.notifyDataSetChanged()
    }

    override fun onPauseFragment() {
        favoriteTeamSearchItem?.collapseActionView()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if(hidden){
            favoriteTeamSearchItem?.collapseActionView()
        }
    }

    override fun onResume() {
        super.onResume()
        if(isSearching){
            getFavoriteDataFromQuery(searchQuery)
        } else {
            getFavoriteData()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.menu_search, menu)

        favoriteTeamSearchItem = menu.findItem(R.id.action_search)

        val favoriteSearchManager : SearchManager = context?.getSystemService(Context.SEARCH_SERVICE) as SearchManager

        if(favoriteTeamSearchItem != null){
            favoriteTeamSearchView = favoriteTeamSearchItem?.actionView as SearchView

            favoriteTeamSearchItem?.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(menuItem: MenuItem?): Boolean {
                    isSearching = true
                    getFavoriteDataFromQuery(favoriteTeamSearchView?.query.toString())
                    return true
                }

                override fun onMenuItemActionCollapse(menuItem: MenuItem?): Boolean {
                    isSearching = false
                    searchQuery = ""
                    getFavoriteData()
                    return true
                }

            })

            favoriteTeamSearchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    if(!isDataLoading){
                        searchQuery = query
                        getFavoriteDataFromQuery(searchQuery)
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }

            })
        }

        favoriteTeamSearchView?.setSearchableInfo(favoriteSearchManager.getSearchableInfo(activity?.componentName))

        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun initData(){
        favoriteTeamRvAdapter = FavoriteTeamRecyclerViewAdapter(favoriteTeams) {
            context?.startActivity<TeamDetailActivity>("favoriteTeamItem" to it)
        }

        favoriteTeamRecyclerView.layoutManager = GridLayoutManager(context, spanCount)

        favoriteTeamRecyclerView.adapter = favoriteTeamRvAdapter

        favoriteTeamRecyclerView.addItemDecoration(GridSpacingItemDecoration(spanCount = spanCount, space = dip(16), includeEdge = includeEdge))

        favoriteTeamPresenter = FavoriteTeamPresenter(this, context!!)

    }
    private fun getFavoriteData(){
        favoriteTeamPresenter.getFavoriteTeamInfo()
    }

    private fun getFavoriteDataFromQuery(query : String){
        favoriteTeamPresenter.getFavoriteTeamInfoSearchResult(query)
    }

}
