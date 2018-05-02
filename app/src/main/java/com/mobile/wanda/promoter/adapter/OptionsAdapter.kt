package com.mobile.wanda.promoter.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.mobile.wanda.promoter.R
import com.mobile.wanda.promoter.event.MenuSelectionEvent
import org.greenrobot.eventbus.EventBus

/**
 * Created by kombo on 07/12/2017.
 */
class OptionsAdapter(private val menus: ArrayList<String>) : RecyclerView.Adapter<OptionsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.menu_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.bindItems(menus[holder.adapterPosition])
    }

    override fun getItemCount(): Int = menus.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(menu: String) {
            val menuItem = itemView.findViewById(R.id.menuItem) as TextView
            val parent = itemView.findViewById(R.id.parent) as RelativeLayout

            menuItem.text = menu
            parent.setOnClickListener { EventBus.getDefault().post(MenuSelectionEvent(menu)) }
        }
    }
}