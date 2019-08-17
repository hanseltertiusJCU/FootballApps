package com.example.footballapps.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.footballapps.R
import com.example.footballapps.model.LeagueItem
import com.squareup.picasso.Picasso

class LeagueRecyclerViewAdapter(private val context : Context, private val leagueItems : List<LeagueItem>, private val clickListener : (LeagueItem) -> Unit) :
    RecyclerView.Adapter<LeagueRecyclerViewAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_league_list, parent , false ))


    override fun getItemCount(): Int = leagueItems.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(leagueItems[position], clickListener)
    }

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view){

        private val leagueName = view.findViewById<TextView>(R.id.league_name)
        private val leagueImage = view.findViewById<ImageView>(R.id.league_image)

        fun bindItem(leagueItem: LeagueItem, clickListener: (LeagueItem) -> Unit){
            leagueName.text = leagueItem.leagueName
            leagueItem.leagueImage?.let{Picasso.get().load(it).into(leagueImage)}
            itemView.setOnClickListener {
                clickListener(leagueItem)
            }
        }
    }
}