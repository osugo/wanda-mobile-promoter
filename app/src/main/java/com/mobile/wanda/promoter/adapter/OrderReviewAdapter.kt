package com.mobile.wanda.promoter.adapter

import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.vipulasri.timelineview.TimelineView
import com.mobile.wanda.promoter.R
import com.mobile.wanda.promoter.Wanda
import com.mobile.wanda.promoter.model.ProductOrder
import org.jetbrains.anko.find

/**
 * Created by kombo on 25/03/2018.
 */
class OrderReviewAdapter(private val productOrders: List<ProductOrder>) : RecyclerView.Adapter<OrderReviewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(productOrders[holder.adapterPosition], getItemViewType(position))
    }

    override fun getItemCount(): Int = productOrders.size

    override fun getItemViewType(position: Int): Int {
        return TimelineView.getTimeLineViewType(position, itemCount)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(productOrder: ProductOrder, itemViewType: Int) {
            val name = itemView.find<TextView>(R.id.item)
            val amount = itemView.find<TextView>(R.id.amount)
            val marker = itemView.find<TimelineView>(R.id.marker)

            marker.initLine(itemViewType)

            name.text = productOrder.productName
            amount.text = buildString {
                append(productOrder.currency)
                append(" ")
                append(productOrder.amount)
            }

            amount.typeface = Typeface.createFromAsset(Wanda.INSTANCE.assets, "fonts/PT_Sans-Web-Bold.ttf")
        }
    }
}