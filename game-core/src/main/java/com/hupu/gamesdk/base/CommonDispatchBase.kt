package com.hupu.gamesdk.base

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

internal abstract class CommonDispatchBase<D, H: RecyclerView.ViewHolder> {
    private var videoDispatchAdapter: CommonDispatchAdapter? = null

    open fun setAdapter(videoDispatchAdapter: CommonDispatchAdapter?) {
        this.videoDispatchAdapter = videoDispatchAdapter
    }
    abstract fun createHolder(parent: ViewGroup): H
     abstract fun bindHolder(holder: H,data: D,position: Int)
}