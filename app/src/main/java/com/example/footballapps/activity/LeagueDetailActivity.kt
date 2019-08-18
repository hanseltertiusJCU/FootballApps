package com.example.footballapps.activity

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.TypedValue
import android.view.Gravity
import android.view.MenuItem
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import com.example.footballapps.model.LeagueItem
import com.example.footballapps.utils.PicassoCircleTransformation
import com.squareup.picasso.Picasso
import org.jetbrains.anko.*
import org.jetbrains.anko.design.snackbar


class LeagueDetailActivity : AppCompatActivity() {

    lateinit var leagueItem : LeagueItem

    lateinit var tvLeagueDetailName : TextView
    lateinit var ivLeagueDetailImage : ImageView
    lateinit var tvLeagueDetailDesc : TextView

    lateinit var leagueDetailScrollView : ScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()

        initData()

        initToolbarBehavior()

    }

    private fun initView() {
        leagueDetailScrollView = scrollView {
            verticalLayout {
                padding = dip(16)
                ivLeagueDetailImage = imageView().lparams {
                    width = convertDpToPx(96f)
                    height = convertDpToPx(96f)
                    gravity = Gravity.CENTER
                }
                tvLeagueDetailName = textView {
                    typeface = Typeface.DEFAULT_BOLD
                    textSize = 20f
                    textColor = Color.BLACK
                }.lparams {
                    gravity = Gravity.CENTER_HORIZONTAL
                    topMargin = dip(8)
                }
                textView("Description : ") {
                    typeface = Typeface.DEFAULT_BOLD
                    textSize = 16f
                    textColor = Color.BLACK
                }.lparams {
                    topMargin = dip(8)
                }
                tvLeagueDetailDesc = textView {
                    textColor = Color.BLACK
                }.lparams {
                    topMargin = dip(8)
                }
            }
        }
    }

    private fun initData() {
        val intent = intent
        leagueItem = intent.getParcelableExtra("leagueItem")

        leagueDetailScrollView.snackbar(leagueItem.leagueName as CharSequence)

        leagueItem.leagueImage?.let { Picasso
            .get()
            .load(it)
            .transform(PicassoCircleTransformation())
            .into(ivLeagueDetailImage)
        }
        tvLeagueDetailName.text = leagueItem.leagueName
        tvLeagueDetailDesc.text = leagueItem.leagueDesc

    }

    private fun initToolbarBehavior() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = leagueItem.leagueName
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