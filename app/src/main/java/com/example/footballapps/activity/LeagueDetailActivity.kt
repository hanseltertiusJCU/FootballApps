package com.example.footballapps.activity

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.util.TypedValue
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.example.footballapps.R
import com.example.footballapps.client.RetrofitClient
import com.example.footballapps.model.LeagueDetailItem
import com.example.footballapps.model.LeagueDetailResponse
import com.example.footballapps.model.LeagueItem
import com.example.footballapps.presenter.LeagueDetailPresenter
import com.example.footballapps.service.LeagueDetailService
import com.example.footballapps.utils.PicassoCircleTransformation
import com.example.footballapps.utils.gone
import com.example.footballapps.utils.invisible
import com.example.footballapps.utils.visible
import com.example.footballapps.view.LeagueDetailView
import com.squareup.picasso.Picasso
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.design.snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class LeagueDetailActivity : AppCompatActivity(), LeagueDetailView {

    private lateinit var leagueItem : LeagueItem

    private lateinit var tvLeagueDetailName : TextView
    private lateinit var ivLeagueDetailImage : ImageView
    private lateinit var tvLeagueDetailDesc : TextView
    private lateinit var tvDescTitle : TextView

    private lateinit var progressBar : ProgressBar

    private lateinit var leagueDetailPresenter : LeagueDetailPresenter

    private lateinit var leagueDetailScrollView : ScrollView

    private lateinit var toolbarLeagueDetail : Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()

        initData()

    }

    private fun initView() {

        coordinatorLayout{
            appBarLayout{
                lparams(width = matchParent, height = wrapContent)

                toolbarLeagueDetail = toolbar {
                    id = R.id.toolbar_league_detail
                    lparams(width = matchParent, height = dimenAttr(R.attr.actionBarSize))
                    popupTheme = R.style.ThemeOverlay_AppCompat_Light
                }
            }

            leagueDetailScrollView = scrollView {
                relativeLayout {
                    padding = dip(16)
                    ivLeagueDetailImage = imageView{
                        id = R.id.iv_league_detail_image
                    }.lparams {
                        width = convertDpToPx(96f)
                        height = convertDpToPx(96f)
                        centerHorizontally()
                    }

                    tvLeagueDetailName = textView {
                        id = R.id.tv_league_detail_name
                        typeface = Typeface.DEFAULT_BOLD
                        textSize = 20f
                        textColor = Color.BLACK
                    }.lparams {
                        centerHorizontally()
                        topMargin = dip(8)
                        bottomOf(R.id.iv_league_detail_image)
                    }

                    tvDescTitle = textView("Description : ") {
                        id = R.id.tv_desc_title
                        typeface = Typeface.DEFAULT_BOLD
                        textSize = 16f
                        textColor = Color.BLACK
                        visibility = View.INVISIBLE
                    }.lparams {
                        topMargin = dip(8)
                        bottomOf(R.id.tv_league_detail_name)
                    }
                    tvLeagueDetailDesc = textView {
                        id = R.id.tv_league_detail_desc
                        textColor = Color.BLACK
                    }.lparams {
                        topMargin = dip(8)
                        bottomOf(R.id.tv_desc_title)
                    }

                    progressBar = progressBar{
                        id = R.id.progress_bar
                    }.lparams{
                        width = convertDpToPx(48f)
                        height = convertDpToPx(48f)
                        centerInParent()
                    }
                }
            }.lparams{
                width = matchParent
                height = matchParent
                topMargin = dimenAttr(R.attr.actionBarSize)
            }
        }
    }

    private fun initData() {
        val intent = intent
        leagueItem = intent.getParcelableExtra("leagueItem")

        leagueDetailPresenter = LeagueDetailPresenter(this, leagueItem)

        leagueDetailPresenter.getLeagueDetailTitle()

        leagueDetailPresenter.getLeagueDetailInfo()

    }

    override fun dataIsLoading() {
        progressBar.visible()
        tvDescTitle.invisible()
    }

    override fun dataLoadingFinished() {
        progressBar.gone()
        tvDescTitle.visible()
    }

    override fun showLeagueDetailTitle(leagueItem: LeagueItem) {
        setSupportActionBar(toolbarLeagueDetail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = leagueItem.leagueName
    }

    override fun showLeagueDetailInfo(leagueDetailItemList: List<LeagueDetailItem>) {
        for(i in leagueDetailItemList.indices) {
            if(i == 0) {
                tvLeagueDetailName.text = leagueDetailItemList[i].leagueName
                tvLeagueDetailDesc.text = leagueDetailItemList[i].leagueDescription

                Glide.with(applicationContext)
                    .load(leagueDetailItemList[i].leagueBadge)
                    .placeholder(R.drawable.empty_league_image_info)
                    .into(ivLeagueDetailImage)

                break
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun convertDpToPx(dp : Float) : Int {
        val r = resources
        val px = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            r.displayMetrics
        )
        return px.toInt()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putIntArray("leagueDetailScrollPosition", intArrayOf(leagueDetailScrollView.scrollX, leagueDetailScrollView.scrollY))
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        val scrollPosition : IntArray? = savedInstanceState?.getIntArray("leagueDetailScrollPosition")
        if(scrollPosition != null){
            leagueDetailScrollView.post {
                leagueDetailScrollView.scrollTo(scrollPosition[0], scrollPosition[1])
            }
        }
    }
}