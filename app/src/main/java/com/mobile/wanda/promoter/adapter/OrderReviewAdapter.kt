package com.mobile.wanda.promoter.adapter

import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.mobile.wanda.promoter.R
import com.mobile.wanda.promoter.Wanda
import com.mobile.wanda.promoter.model.ProductOrder
import org.jetbrains.anko.find

/**
 * Created by kombo on 25/03/2018.
 */
class OrderReviewAdapter(private val productOrders: List<ProductOrder>): RecyclerView.Adapter<OrderReviewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(productOrders[holder.adapterPosition], holder.adapterPosition)
    }

    override fun getItemCount(): Int = productOrders.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(productOrder: ProductOrder, position: Int) {
            val name = itemView.find<TextView>(R.id.item)
            val amount = itemView.find<TextView>(R.id.amount)
            val clear = itemView.find<ImageView>(R.id.clear)
            val pos = itemView.find<TextView>(R.id.position)

            name.text = productOrder.productName
            amount.text = "${productOrder.currency} ${productOrder.amount}"
            pos.text = (position + 1).toString()

            amount.typeface = Typeface.createFromAsset(Wanda.INSTANCE.assets, "fonts/PT_Sans-Web-Bold.ttf")

//            clear.setOnClickListener {
//                try {
//                    Realm.getInstance(Wanda.INSTANCE.realmConfig()).use {
//                        it.executeTransaction {
//                            cartItem.deleteFromRealm()
//                        }
//                    }
//                } catch (e: RealmException) {
//                    Log.e(OrderReviewAdapter::class.java.simpleName, e.localizedMessage, e)
//                }
//            }
        }
    }
}