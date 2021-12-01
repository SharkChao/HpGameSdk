package com.hupu.gamesdk.base

import android.util.SparseArray
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

internal class CommonDispatchAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dispatcherPool = SparseArray<CommonDispatchBase<Any, RecyclerView.ViewHolder>>()
    private val dataList = ArrayList<Any>()

    fun getDataList(): ArrayList<Any>{
        return dataList
    }


    fun registerDispatcher(dispatcher: CommonDispatchBase<*, *>){
        if (dispatcherPool.indexOfValue(dispatcher as CommonDispatchBase<Any, RecyclerView.ViewHolder>) < 0){
            val key = dispatcherPool.size()
            dispatcherPool.put(key, dispatcher)
            dispatcher.setAdapter(this)
        }
    }


    /**
     * 根据itemType获取到具体adapter代理
     */
    private fun getItemDispatcher(itemType: Int): CommonDispatchBase<Any, RecyclerView.ViewHolder> {
       return dispatcherPool[itemType]
    }

    /**
     * 分发创建viewHolder
     */
    private fun dispatchCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return getItemDispatcher(viewType).createHolder(parent)
    }

    /**
     * 分发绑定viewHolder
     */
    private fun dispatchBindViewHolder(holder: RecyclerView.ViewHolder, position: Int){
        val itemDispatcher = getItemDispatcher(holder.itemViewType)
        val shareDispatchBase = itemDispatcher as CommonDispatchBase<Any, RecyclerView.ViewHolder>
        dataList[position]?.let { shareDispatchBase.bindHolder(holder, it, position) }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return dispatchCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        dispatchBindViewHolder(holder, position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun getItemViewType(position: Int): Int {
        for (index in 0 until dispatcherPool.size()){
            val shareDispatchBase = dispatcherPool[index]
            val type: Type? = shareDispatchBase.javaClass.genericSuperclass
            try {
                val p = type as ParameterizedType
                val c = p.actualTypeArguments[0] as Class<*>
                if (c.isInstance(dataList[position])) {
                    return index
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return super.getItemViewType(position)
    }


    class Builder{
        private var adapter =  CommonDispatchAdapter()
        fun registerDispatcher(dispatcher: CommonDispatchBase<*, *>): Builder {
            adapter.registerDispatcher(dispatcher as CommonDispatchBase<Any, RecyclerView.ViewHolder>)
            return this
        }
        fun build(): CommonDispatchAdapter {
            return adapter
        }
    }
}