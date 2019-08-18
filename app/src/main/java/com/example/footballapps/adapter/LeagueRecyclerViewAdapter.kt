package com.example.footballapps.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.footballapps.R
import com.example.footballapps.model.LeagueItem
import com.squareup.picasso.Picasso
import kotlinx.android.extensions.LayoutContainer
import org.jetbrains.anko.*
import org.jetbrains.anko.cardview.v7.cardView
import android.util.TypedValue



class LeagueRecyclerViewAdapter(private val leagueItems : List<LeagueItem>, private val clickListener : (LeagueItem) -> Unit) :
    RecyclerView.Adapter<LeagueRecyclerViewAdapter.ViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LeagueItemListUI().createView(AnkoContext.Companion.create(parent.context, parent)))


    override fun getItemCount(): Int = leagueItems.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(leagueItems[position], clickListener)
    }

    class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer{

        var leagueName : TextView = containerView.find(LeagueItemListUI.leagueNameTextViewId)
        var leagueImage : ImageView = containerView.find(LeagueItemListUI.leagueImageViewId)

        fun bindItem(leagueItem: LeagueItem, clickListener: (LeagueItem) -> Unit){
            leagueName.text = leagueItem.leagueName
            leagueItem.leagueImage?.let { Picasso.get().load(it).into(leagueImage) }
            containerView.setOnClickListener {
                clickListener(leagueItem)
            }
        }
    }

    class LeagueItemListUI : AnkoComponent<ViewGroup>{

        companion object {
            const val leagueImageViewId = 1
            const val leagueNameTextViewId = 2
        }

        override fun createView(ui: AnkoContext<ViewGroup>): View = with(ui){
            cardView {
                background = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = dip(5).toFloat()
                    setStroke(1, ContextCompat.getColor(context, android.R.color.darker_gray))
                    elevation = dip(4).toFloat()
                }
                lparams(width = matchParent, height = wrapContent)

                verticalLayout {
                    backgroundResource = attr(R.attr.selectableItemBackgroundBorderless).resourceId

                    imageView{
                        id = leagueImageViewId
                        layoutParams = LinearLayout.LayoutParams(matchParent, convertDpToPx(256f, context))
                        scaleType = ImageView.ScaleType.FIT_XY
                        contentDescription = R.string.league_image.toString()
                    }

                    textView{
                        id = leagueNameTextViewId
                        layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
                        textColor = Color.BLACK
                    }.lparams{
                        margin = dip(8)
                    }

                }.lparams(width = matchParent, height = matchParent)
            }
        }

        private fun convertDpToPx(dp : Float, context : Context) : Int {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics).toInt()
        }

    }
}