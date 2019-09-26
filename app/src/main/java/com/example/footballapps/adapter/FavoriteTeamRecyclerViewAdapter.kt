package com.example.footballapps.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.footballapps.R
import com.example.footballapps.favorite.FavoriteTeamItem
import com.squareup.picasso.Picasso
import org.jetbrains.anko.*

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
                linearLayout {
                    lparams(width = matchParent, height = wrapContent)
                    padding = dip(16)
                    orientation = LinearLayout.HORIZONTAL
                    backgroundResource = attr(R.attr.selectableItemBackground).resourceId

                    imageView {
                        id = R.id.favorite_team_badge
                    }.lparams{
                        height = dip(48)
                        width = dip(48)
                    }

                    themedTextView(R.style.text_content) {
                        id = R.id.favorite_team_name
                    }.lparams{
                        margin = dip(16)
                    }
                }
            }
        }
    }

    class FavoriteTeamViewHolder(view : View) : RecyclerView.ViewHolder(view) {

        private val favoriteTeamBadge : ImageView = view.find(R.id.favorite_team_badge)
        private val favoriteTeamName : TextView = view.find(R.id.favorite_team_name)

        fun bindItem(favoriteTeam : FavoriteTeamItem, listener : (FavoriteTeamItem) -> Unit) {
            Picasso.get().load(favoriteTeam.teamBadgeUrl).placeholder(R.drawable.team_badge_placeholder).into(favoriteTeamBadge)

            favoriteTeamName.text = favoriteTeam.teamName
            itemView.setOnClickListener { listener(favoriteTeam) }
        }
    }
}