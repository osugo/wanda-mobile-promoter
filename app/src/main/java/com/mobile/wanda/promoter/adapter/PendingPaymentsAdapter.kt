package com.mobile.wanda.promoter.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.mobile.wanda.promoter.R
import com.mobile.wanda.promoter.model.PendingPayment
import org.jetbrains.anko.find

/**
 * Created by kombo on 14/05/2018.
 */
class PendingPaymentsAdapter(private val pendingPayments: ArrayList<PendingPayment>, private val clickListener: ClickListener) : RecyclerView.Adapter<PendingPaymentsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pending_payment_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = pendingPayments.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(pendingPayments[holder.adapterPosition], clickListener)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var clickListener: ClickListener? = null

        fun bindItem(pendingPayment: PendingPayment, clickListener: ClickListener) {
            val farmer = itemView.find<TextView>(R.id.farmerName)
            val amount = itemView.find<TextView>(R.id.amount)
            val layout = itemView.find<LinearLayout>(R.id.layout)

            this.clickListener = clickListener

            farmer.text = pendingPayment.farmer?.name
            amount.text = buildString {
                append(pendingPayment.payment?.currency)
                append(" ")
                append(pendingPayment.payment?.amount)
            }

            layout.setOnClickListener {
                clickListener.onClickPendingOrder(pendingPayment)
            }
        }


    }

    interface ClickListener {
        fun onClickPendingOrder(pendingPayment: PendingPayment)
    }
}