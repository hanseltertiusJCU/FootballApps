package com.example.footballapps.adapter

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.footballapps.R
import com.example.footballapps.favorite.FavoriteTeamItem
import com.squareup.picasso.Picasso
import org.jetbrains.anko.*
import org.jetbrains.anko.cardview.v7.cardView

class FavoriteTeamRecyclerViewAdapter (private val favoriteTeamItemList : List<FavoriteTeamItem>, private val clickListener : (FavoriteTeamItem) -> Unit) : RecyclerView.Adapter<FavoriteTeamRecyclerViewAdapter.FavoriteTeamViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteTeamViewHolder =
        FavoriteTeamViewHolder(FavoriteTeamUI().createView(AnkoContext.create(parent.context, parent)))


    override fun getItemCount(): Int = favoriteTeamItemList.size

    override fun onBindViewHolder(holder: FavoriteTeamViewHolder, position: Int) {
        holder.bindItem(favoriteTeamItemList[position], clickListener)
    }

    class FavoriteTeamUI : AnkoComponent<ViewGroup> {
        override fun createView(ui: AnkoContext<ViewGroup>): View {
            return with(ui){

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

                        imageView {
                            id = R.id.favorite_team_badge
                            scaleType = ImageView.ScaleType.CENTER_CROP
                        }.lparams{
                            topMargin = dip(8)
                            width = dip(128)
                            height = dip(128)
                            gravity = Gravity.CENTER_HORIZONTAL
                        }

                        themedTextView(R.style.text_content) {
                            id = R.id.favorite_team_name
                            gravity = Gravity.CENTER
                        }.lparams{
                            margin = dip(8)
                            width = matchParent
                            height = matchParent
                        }

                    }.lparams(width = matchParent, height = matchParent)
                }
            }
        }
    }

    class FavoriteTeamViewHolder(view : View) : RecyclerView.ViewHolder(view) {

        private val favoriteTeamBadge : ImageView = view.find(R.id.favorite_team_badge)
        private val favoriteTeamName : TextView = view.find(R.id.favorite_team_name)

        fun bindItem(favoriteTeam : FavoriteTeamItem, listener : (FavoriteTeamItem) -> Unit) {

            favoriteTeam.teamBadgeUrl.let { Picasso.get().load(it).placeholder(R.drawable.team_badge_placeholder).fit().into(favoriteTeamBadge) }

            favoriteTeamName.text = favoriteTeam.teamName
            itemView.setOnClickListener { listener(favoriteTeam) }
        }
    }
}