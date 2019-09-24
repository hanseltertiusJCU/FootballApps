package com.example.footballapps.adapter

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
                linearLayout {
                    lparams(width = matchParent, height = wrapContent)
                    padding = dip(16)
                    orientation = LinearLayout.HORIZONTAL
                    backgroundResource = attr(R.attr.selectableItemBackgroundBorderless).resourceId

//                    view {
//                        background = ContextCompat.getDrawable(context, R.color.color_grey_line)
//                    }.lparams {
//                        width = matchParent
//                        height = dip(1)
//                    }

                    imageView {
                        id = R.id.team_badge
                    }.lparams {
                        height = dip(48)
                        width = dip(48)
                    }

                    themedTextView(R.style.text_content) {
                        id = R.id.team_name
                    }.lparams {
                        margin = dip(16)
                    }

//                    view {
//                        background = ContextCompat.getDrawable(context, R.color.color_grey_line)
//                    }.lparams {
//                        width = matchParent
//                        height = dip(1)
//                    }
                }
            }
        }
    }

    class TeamViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val teamBadge: ImageView = view.find(R.id.team_badge)
        private val teamName: TextView = view.find(R.id.team_name)

        fun bindItem(team: TeamItem, listener: (TeamItem) -> Unit) {
            Picasso.get()
                .load(team.teamBadge)
                .placeholder(R.drawable.team_badge_placeholder)
                .into(teamBadge)

            teamName.text = team.teamName
            itemView.setOnClickListener { listener(team) }
        }

    }

}

