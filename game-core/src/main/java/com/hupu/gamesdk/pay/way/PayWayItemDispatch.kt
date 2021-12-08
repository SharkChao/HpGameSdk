package com.hupu.gamesdk.pay.way

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.hupu.gamesdk.base.CommonDispatchBase
import com.hupu.gamesdk.base.ReflectUtil
import com.hupu.gamesdk.config.HpPayItem

internal class PayWayItemDispatch: CommonDispatchBase<HpPayItem, PayWayItemDispatch.PayWayItemViewHolder>() {

    private var listener: OnItemSelectListener? = null
    override fun createHolder(parent: ViewGroup): PayWayItemViewHolder {
        return PayWayItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
            ReflectUtil.getLayoutId(parent.context,"hp_game_core_pay_item_layout"),
            parent,
            false
        ))
    }

    override fun bindHolder(holder: PayWayItemViewHolder, data: HpPayItem, position: Int) {
       holder.tvPay.text = data.name
       Glide.with(holder.itemView.context).load(data.img).into(holder.ivPay)
        holder.ivSelect.setImageResource(if (data.select) ReflectUtil.getMipmapId(holder.itemView.context,"hp_game_core_pay_dialog_selected") else ReflectUtil.getMipmapId(holder.itemView.context,"hp_game_core_pay_dialog_unselected"))
        holder.itemView.setOnClickListener {
            listener?.onItemSelect(data)
        }
    }

    inner class PayWayItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var tvPay = itemView.findViewById<TextView>(ReflectUtil.getViewId(itemView.context,"tv_pay"))
        var ivPay = itemView.findViewById<ImageView>(ReflectUtil.getViewId(itemView.context,"iv_pay"))
        var ivSelect = itemView.findViewById<ImageView>(ReflectUtil.getViewId(itemView.context,"iv_pay_select"))
    }

    fun registerOnItemSelectListener(listener: OnItemSelectListener) {
        this.listener = listener
    }

    interface OnItemSelectListener {
        fun onItemSelect(data: HpPayItem)
    }
}