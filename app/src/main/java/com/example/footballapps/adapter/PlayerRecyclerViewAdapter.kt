package com.example.footballapps.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.footballapps.R
import com.example.footballapps.application.FootballApps
import com.example.footballapps.model.PlayerItem
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_player_data.view.*

class PlayerRecyclerViewAdapter
    (
    private val context: Context,
    private val playerItemList: List<PlayerItem>,
    private val clickListener: (PlayerItem) -> Unit
) : RecyclerView.Adapter<PlayerRecyclerViewAdapter.PlayerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder =
        PlayerViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_player_data,
                parent,
                false
            )
        )


    override fun getItemCount(): Int = playerItemList.size

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        holder.bindItem(playerItemList[position], clickListener)
    }

    class PlayerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        companion object {
            val valueUnknown = FootballApps.res.getString(R.string.value_unknown)
            val valueNone = FootballApps.res.getString(R.string.value_none)
        }

        fun bindItem(playerItem: PlayerItem, clickListener: (PlayerItem) -> Unit) {

            Picasso.get().load(playerItem.playerPhoto)
                .placeholder(R.drawable.ic_player_picture_placeholder)
                .into(itemView.iv_player_photo)

            itemView.tv_player_name.text = playerItem.playerName ?: valueUnknown
            itemView.tv_player_nationality.text =
                playerItem.playerNationality ?: valueUnknown
            itemView.tv_player_position.text = playerItem.playerPosition ?: valueUnknown
            itemView.tv_player_number.text = StringBuilder("#${formatValueData(playerItem.playerShirtNumber)}")

            itemView.setOnClickListener {
                clickListener(playerItem)
            }
        }

        private fun formatValueData(valueData : String?) : String{
            return if(valueData != null && valueData.trim().isNotEmpty()){
                valueData
            } else {
                valueNone
            }
        }

    }
}