package com.example.footballapps.activity

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import com.example.footballapps.model.LeagueItem
import org.jetbrains.anko.*
import android.util.TypedValue
import android.view.Gravity
import android.view.MenuItem
import android.widget.ScrollView
import com.squareup.picasso.Picasso


class LeagueDetailActivity : AppCompatActivity() {

    lateinit var leagueItem : LeagueItem

    lateinit var tvLeagueDetailName : TextView
    lateinit var ivLeagueDetailImage : ImageView
    lateinit var tvLeagueDetailDesc : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        scrollView {
            verticalLayout {
                padding = dip(16)
                ivLeagueDetailImage = imageView().lparams {
                    width = convertDpToPx(96f)
                    height = convertDpToPx(96f)
                    gravity = Gravity.CENTER
                }
                tvLeagueDetailName = textView{
                    typeface = Typeface.DEFAULT_BOLD
                    textSize = 20f
                    textColor = Color.BLACK
                }.lparams{
                    gravity = Gravity.CENTER_HORIZONTAL
                    topMargin = dip(8)
                }
                textView("Description : "){
                    typeface = Typeface.DEFAULT_BOLD
                    textSize = 16f
                    textColor = Color.BLACK
                }.lparams{
                    topMargin = dip(8)
                }
                tvLeagueDetailDesc = textView{
                    textColor = Color.BLACK
                }.lparams{
                    topMargin = dip(8)
                }
            }
        }

        val intent = intent
        leagueItem = intent.getParcelableExtra("leagueItem")

        // todo: make the image into circle
        leagueItem.leagueImage?.let { Picasso.get().load(it).into(ivLeagueDetailImage) }
        tvLeagueDetailName.text = leagueItem.leagueName
        tvLeagueDetailDesc.text = leagueItem.leagueDesc

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
}