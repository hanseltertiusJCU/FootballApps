package com.example.footballapps.activity

import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import com.example.footballapps.model.LeagueItem
import org.jetbrains.anko.*
import android.util.TypedValue
import android.view.Gravity
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
                }.lparams{
                    gravity = Gravity.CENTER_HORIZONTAL
                }
                tvLeagueDetailDesc = textView()
            }
        }

        val intent = intent
        leagueItem = intent.getParcelableExtra("leagueItem")

        // todo: make the image into circle
        leagueItem.leagueImage?.let { Picasso.get().load(it).into(ivLeagueDetailImage) }
        tvLeagueDetailName.text = leagueItem.leagueName
        tvLeagueDetailDesc.text = leagueItem.leagueDesc
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