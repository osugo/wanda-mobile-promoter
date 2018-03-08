package com.mobile.wanda.promoter.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.mobile.wanda.promoter.R
import com.mobile.wanda.promoter.model.orders.PendingOrder
import io.realm.RealmResults

/**
 * Created by kombo on 08/03/2018.
 */
class OrdersAdapter(private val pendingOrders: RealmResults<PendingOrder>, private val clickListener: ClickListener) : RecyclerView.Adapter<OrdersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.order_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bindItems(pendingOrders[holder.adapterPosition]!!, clickListener)
    }

    override fun getItemCount(): Int = pendingOrders.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var clickListener: ClickListener? = null
        private var position: Int? = 0

        fun bindItems(order: PendingOrder, clickListener: ClickListener) {
            this.clickListener = clickListener
            this.position = position

            val parentLayout = itemView.findViewById<LinearLayout>(R.id.parentLayout)
            val text = itemView.findViewById<TextView>(R.id.order)

            text.text = String.format("Order %s", position)

            parentLayout.setOnClickListener {
                clickListener.onOrderClicked(order)
            }
        }
    }

    interface ClickListener {
        fun onOrderClicked(order: PendingOrder)
    }
}