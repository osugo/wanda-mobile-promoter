package com.mobile.wanda.promoter.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.mobile.wanda.promoter.R
import com.mobile.wanda.promoter.model.MenuItem
import com.mobile.wanda.promoter.view.SquareImageView

/**
 * Created by kombo on 06/12/2017.
 */

/**
 * Initialise the adapter and pass the parameters on constructor instantiation
 */
class MenuAdapter(private val menus: ArrayList<MenuItem>, private val clickListener: ClickListener) : RecyclerView.Adapter<MenuAdapter.ViewHolder>() {

    /**
     * Bind items to viewholder
     */
    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bindItems(menus[holder.adapterPosition], clickListener, holder.adapterPosition)
    }

    /**
     * return the size of items passed so that the adapter can know how many items to expect to draw
     */
    override fun getItemCount(): Int = menus.size

    /**
     * Inflate the view and create the viewholder
     */
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.main_menu_item_layout, parent, false)
        return ViewHolder(view)
    }

    /**
     * ViewHolder class that handles finding the views from the passed layout and setting the respective values and click listeners
     */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var clickListener: ClickListener? = null

        fun bindItems(menu: MenuItem, clickListener: ClickListener, position: Int) {
            val textView = itemView.findViewById(R.id.menuItem) as TextView
            val icon = itemView.findViewById(R.id.icon) as SquareImageView
            val parent = itemView.findViewById(R.id.parentLayout) as LinearLayout

            this.clickListener = clickListener

            textView.text = menu.title
            icon.setImageResource(menu.icon!!)

            parent.setOnClickListener {
                clickListener.onMenuItemClicked(position)
            }
        }
    }

    interface ClickListener {
        fun onMenuItemClicked(position: Int)
    }
}