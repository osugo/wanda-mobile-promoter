package com.mobile.wanda.promoter.adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.mobile.wanda.promoter.R
import com.mobile.wanda.promoter.activity.CommissionManagement
import com.mobile.wanda.promoter.activity.FarmReport
import com.mobile.wanda.promoter.activity.FarmerRegistration
import com.mobile.wanda.promoter.activity.VoucherManagement
import com.mobile.wanda.promoter.model.MenuItem
import com.mobile.wanda.promoter.view.SquareImageView

/**
 * Created by kombo on 06/12/2017.
 */

/**
 * Initialise the adapter and pass the parameters on constructor instantiation
 */
class MenuAdapter(private val context: Context, private val menus: ArrayList<MenuItem>) : RecyclerView.Adapter<MenuAdapter.ViewHolder>() {

    /**
     * Bind items to viewholder
     */
    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bindItems(context, menus[holder.adapterPosition])
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

        fun bindItems(context: Context, menu: MenuItem) {
            val textView = itemView.findViewById(R.id.menuItem) as TextView
            val icon = itemView.findViewById(R.id.icon) as SquareImageView
            val parent = itemView.findViewById(R.id.parentLayout) as LinearLayout

            textView.text = menu.title
            icon.setImageResource(menu.icon!!)

            parent.setOnClickListener {
                when (menu.title) {
                    context.getString(R.string.commission_mgmt) -> context.startActivity(Intent(context, CommissionManagement::class.java))
                    context.getString(R.string.farmer_reg) -> context.startActivity(Intent(context, FarmerRegistration::class.java))
                    context.getString(R.string.farm_audit) -> context.startActivity(Intent(context, FarmReport::class.java))
                    context.getString(R.string.voucher_mgmt) -> context.startActivity(Intent(context, VoucherManagement::class.java))
                }
            }
        }
    }
}