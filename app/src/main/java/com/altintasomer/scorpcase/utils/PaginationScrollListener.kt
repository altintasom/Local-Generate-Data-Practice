package com.altintasomer.scorpcase.utils

import android.util.Log
import android.widget.AbsListView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.log

private const val TAG = "PaginationScrollListene"
abstract class PaginationScrollListener(private val layoutManager: GridLayoutManager) : RecyclerView.OnScrollListener() {

    var isScrolling = false
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)



    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
           isScrolling = true
        }
        val visibleItemCount  = layoutManager.childCount
        val totalItemCount = layoutManager.itemCount
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0){
            loadMoreItems()
        }
    }


    abstract fun loadMoreItems()
    abstract fun isLoading() : Boolean


}