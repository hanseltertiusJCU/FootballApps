package com.example.footballapps.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.footballapps.R
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.swipeRefreshLayout

class NextMatchFragment : Fragment() {

    private lateinit var nextMatchList : RecyclerView
    private lateinit var nextMatchProgressBar : ProgressBar
    private lateinit var nextMatchSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var nextMatchLeagueSpinner : Spinner

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return UI {
            constraintLayout {
                id = R.id.next_match_parent_layout
                lparams(width = matchParent, height = matchParent)
                topPadding = dip(16)
                leftPadding = dip(16)
                rightPadding = dip(16)

                nextMatchLeagueSpinner = spinner {
                    id = R.id.next_match_league_spinner
                }

                nextMatchSwipeRefreshLayout = swipeRefreshLayout{

                    nextMatchList = recyclerView {
                        lparams(width = matchParent, height = wrapContent)
                        layoutManager = LinearLayoutManager(context)
                    }

                }.lparams{
                    topToBottom = R.id.next_match_league_spinner
                    leftToLeft = R.id.next_match_parent_layout
                    rightToRight = R.id.next_match_parent_layout
                    bottomToBottom = R.id.next_match_parent_layout
                    verticalBias = 0f
                }

                nextMatchProgressBar = progressBar()
                    .lparams{
                        topToTop = R.id.next_match_parent_layout
                        leftToLeft = R.id.next_match_parent_layout
                        rightToRight = R.id.next_match_parent_layout
                        bottomToBottom = R.id.next_match_parent_layout
                    }

            }
        }.view()
    }
}