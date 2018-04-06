package com.mobile.wanda.promoter.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.mobile.wanda.promoter.R
import com.mobile.wanda.promoter.model.orders.Product
import com.mobile.wanda.promoter.model.orders.ProductResults

/**
 * Created by kombo on 09/03/2018.
 */
class ProductsAdapter(private val products: ProductResults, private val clickListener: ClickListener) : RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.order_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bindItems(products.items[holder.adapterPosition], clickListener)
    }

    override fun getItemCount(): Int = products.items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var clickListener: ClickListener? = null

        fun bindItems(product: Product, clickListener: ClickListener) {
            this.clickListener = clickListener

            val name = itemView.findViewById<TextView>(R.id.order)
            val parentLayout = itemView.findViewById<LinearLayout>(R.id.parentLayout)

            name.text = product.name
            parentLayout.setOnClickListener {
                clickListener.onProductSelected(product)
            }
        }
    }

    interface ClickListener {
        fun onProductSelected(product: Product)
    }
}