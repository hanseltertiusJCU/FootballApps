package com.example.footballapps.adapter

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
import com.example.footballapps.model.TeamItem
import com.squareup.picasso.Picasso
import org.jetbrains.anko.*
import org.jetbrains.anko.cardview.v7.cardView

class TeamRecyclerViewAdapter(
    private val teams: List<TeamItem>,
    private val listener: (TeamItem) -> Unit
) : RecyclerView.Adapter<TeamRecyclerViewAdapter.TeamViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder =
        TeamViewHolder(TeamUI().createView(AnkoContext.create(parent.context, parent)))

    override fun getItemCount(): Int = teams.size

    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        holder.bindItem(teams[position], listener)
    }

    class TeamUI : AnkoComponent<ViewGroup> {
        override fun createView(ui: AnkoContext<ViewGroup>): View {
            return with(ui) {
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
                            id = R.id.team_badge
                            scaleType = ImageView.ScaleType.CENTER_CROP
                        }.lparams {
                            width = matchParent
                            height = dip(128)
                        }

                        themedTextView(R.style.text_content) {
                            id = R.id.team_name
                            gravity = Gravity.CENTER
                        }.lparams {
                            margin = dip(8)
                            width = matchParent
                            height = matchParent
                        }

                    }.lparams(width = matchParent, height = matchParent)
                }
            }
        }
    }

    class TeamViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val teamBadge: ImageView = view.find(R.id.team_badge)
        private val teamName: TextView = view.find(R.id.team_name)

        fun bindItem(team: TeamItem, listener: (TeamItem) -> Unit) {

            team.teamBadge.let { Picasso.get().load(it).placeholder(R.drawable.team_badge_placeholder).fit().into(teamBadge) }

            teamName.text = team.teamName
            itemView.setOnClickListener { listener(team) }
        }

    }

}

