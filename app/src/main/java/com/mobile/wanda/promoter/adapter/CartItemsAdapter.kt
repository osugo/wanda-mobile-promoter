package com.mobile.wanda.promoter.adapter

import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.mobile.wanda.promoter.R
import com.mobile.wanda.promoter.Wanda
import com.mobile.wanda.promoter.model.CartItem
import io.realm.OrderedRealmCollection
import io.realm.Realm
import io.realm.RealmRecyclerViewAdapter
import io.realm.exceptions.RealmException
import org.jetbrains.anko.find

/**
 * Created by kombo on 25/03/2018.
 */
class CartItemsAdapter(data: OrderedRealmCollection<CartItem>, autoUpdate: Boolean, updateOnModification: Boolean) :
        RealmRecyclerViewAdapter<CartItem, CartItemsAdapter.ViewHolder>(data, autoUpdate, updateOnModification) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(data!![holder.adapterPosition], holder.adapterPosition)
    }

    override fun getItemCount(): Int = data!!.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(cartItem: CartItem, position: Int) {
            val name = itemView.find<TextView>(R.id.item)
            val quantity = itemView.find<TextView>(R.id.quantity)
            val clear = itemView.find<ImageView>(R.id.clear)
            val pos = itemView.find<TextView>(R.id.position)

            name.text = cartItem.name
            quantity.text = cartItem.quantity.toString()
            pos.text = (position + 1).toString()

            quantity.typeface = Typeface.createFromAsset(Wanda.INSTANCE.assets, "fonts/PT_Sans-Web-Bold.ttf")

            clear.setOnClickListener {
                try {
                    Realm.getInstance(Wanda.INSTANCE.realmConfig()).use {
                        it.executeTransaction {
                            cartItem.deleteFromRealm()
                        }
                    }
                } catch (e: RealmException) {
                    Log.e(CartItemsAdapter::class.java.simpleName, e.localizedMessage, e)
                }
            }
        }
    }
}