package com.example.footballapps.utils

import android.graphics.Rect
import androidx.recyclerview.widget.RecyclerView
import android.view.View

class GridSpacingItemDecoration(
    private var spanCount : Int,
    private var space : Int,
    private var includeEdge : Boolean
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

        val itemPosition = parent.getChildAdapterPosition(view)
        val itemColumn = itemPosition % spanCount

        if(includeEdge){
            outRect.left = space - itemColumn * space / spanCount
            outRect.right = (itemColumn + 1) * space / spanCount

            if(itemPosition < spanCount){
                outRect.top = space
            }

            outRect.bottom = space
        } else {
            outRect.left = itemColumn * space / spanCount
            outRect.right = space - (itemColumn + 1) * space / spanCount

            if(itemPosition >= spanCount){
                outRect.top = space
            }
        }
    }
}