package com.example.footballapps.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.footballapps.R
import com.example.footballapps.model.LeagueItem
import com.squareup.picasso.Picasso
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_league_list.view.*

class LeagueRecyclerViewAdapter(private val context : Context, private val leagueItems : List<LeagueItem>, private val clickListener : (LeagueItem) -> Unit) :
    RecyclerView.Adapter<LeagueRecyclerViewAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_league_list, parent , false ))


    override fun getItemCount(): Int = leagueItems.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(leagueItems[position], clickListener)
    }

    class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer{

        fun bindItem(leagueItem: LeagueItem, clickListener: (LeagueItem) -> Unit){
            containerView.league_name.text = leagueItem.leagueName
            leagueItem.leagueImage?.let { Picasso.get().load(it).into(containerView.league_image) }
            containerView.setOnClickListener {
                clickListener(leagueItem)
            }
        }
    }
}