package com.hupu.gamesdk.pay.way

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.hupu.gamesdk.R
import com.hupu.gamesdk.base.CommonDispatchBase
import com.hupu.gamesdk.config.HpPayItem

internal class PayWayItemDispatch: CommonDispatchBase<HpPayItem, PayWayItemDispatch.PayWayItemViewHolder>() {

    private var listener: OnItemSelectListener? = null
    override fun createHolder(parent: ViewGroup): PayWayItemViewHolder {
        return PayWayItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
            R.layout.hp_game_core_pay_item_layout,
            parent,
            false
        ))
    }

    override fun bindHolder(holder: PayWayItemViewHolder, data: HpPayItem, position: Int) {
       holder.tvPay.text = data.name
       Glide.with(holder.itemView.context).load(data.img).into(holder.ivPay)
        holder.ivSelect.setImageResource(if (data.select) R.mipmap.hp_game_core_pay_dialog_selected else R.mipmap.hp_game_core_pay_dialog_unselected)
        holder.itemView.setOnClickListener {
            listener?.onItemSelect(data)
        }
    }

    inner class PayWayItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var tvPay = itemView.findViewById<TextView>(R.id.tv_pay)
        var ivPay = itemView.findViewById<ImageView>(R.id.iv_pay)
        var ivSelect = itemView.findViewById<ImageView>(R.id.iv_pay_select)
    }

    fun registerOnItemSelectListener(listener: OnItemSelectListener) {
        this.listener = listener
    }

    interface OnItemSelectListener {
        fun onItemSelect(data: HpPayItem)
    }
}