package com.example.footballapps.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import com.example.footballapps.R
import org.jetbrains.anko.constraint.layout.constraintLayout

import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.swipeRefreshLayout

class LastMatchFragment : Fragment() {

    private lateinit var lastMatchList : RecyclerView
    private lateinit var lastMatchProgressBar : ProgressBar
    private lateinit var lastMatchSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var lastMatchLeagueSpinner : Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return UI {
            constraintLayout {
                id = R.id.last_match_parent_layout
                lparams(width = matchParent, height = matchParent)
                topPadding = dip(16)
                leftPadding = dip(16)
                rightPadding = dip(16)

                lastMatchLeagueSpinner = spinner {
                    id = R.id.last_match_league_spinner
                }

                lastMatchSwipeRefreshLayout = swipeRefreshLayout{

                    lastMatchList = recyclerView {
                        lparams(width = matchParent, height = wrapContent)
                        layoutManager = LinearLayoutManager(context)
                    }

                }.lparams{
                    topToBottom = R.id.last_match_league_spinner
                    leftToLeft = R.id.last_match_parent_layout
                    rightToRight = R.id.last_match_parent_layout
                    bottomToBottom = R.id.last_match_parent_layout
                    verticalBias = 0f
                }

                lastMatchProgressBar = progressBar()
                    .lparams{
                        topToTop = R.id.last_match_parent_layout
                        leftToLeft = R.id.last_match_parent_layout
                        rightToRight = R.id.last_match_parent_layout
                        bottomToBottom = R.id.last_match_parent_layout
                    }

            }
        }.view()
    }


}
