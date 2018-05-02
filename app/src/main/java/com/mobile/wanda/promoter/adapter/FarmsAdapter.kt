package com.mobile.wanda.promoter.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.LinearLayout
import android.widget.TextView
import com.mobile.wanda.promoter.R
import com.mobile.wanda.promoter.model.UserFarm

/**
 * Created by kombo on 02/05/2018.
 */
class FarmsAdapter(private val farms: ArrayList<UserFarm>, private val clickListener: ClickListener) : RecyclerView.Adapter<FarmsAdapter.ViewHolder>(), Filterable {

    private var filteredList: ArrayList<UserFarm>? = null
    private var farmFilter: Filter? = null

    init {
        filteredList = farms
        filter
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.farmer_layout_item, parent, false))
    }

    override fun getItemCount() = filteredList?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(farms[position], clickListener)
    }

    override fun getFilter(): Filter {
        if (farmFilter == null)
            farmFilter = FarmerFilter()

        return farmFilter!!
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        override fun onClick(v: View?) {
            clickListener?.onItemClicked(farms[adapterPosition])
        }

        private var clickListener: FarmsAdapter.ClickListener? = null

        fun bindItem(farm: UserFarm, clickListener: FarmsAdapter.ClickListener) {
            this.clickListener = clickListener

            val name = itemView.findViewById(R.id.name) as TextView
            val layout = itemView.findViewById<LinearLayout>(R.id.layout)

            name.text = farm.name
            layout.setOnClickListener(this)
        }

    }

    interface ClickListener {
        fun onItemClicked(farm: UserFarm)
    }

    inner class FarmerFilter : Filter() {

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filterResults = Filter.FilterResults()
            if (constraint != null && constraint.isNotEmpty()) {
                val tempList = ArrayList<UserFarm>()

                // search content in friend list
                for (farm in farms) {
                    if (farm.name!!.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        tempList.add(farm)
                    }
                }

                filterResults.count = tempList.size
                filterResults.values = tempList
            } else {
                filterResults.count = farms.size
                filterResults.values = farms
            }

            return filterResults
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            filteredList = results?.values as ArrayList<UserFarm>
            notifyDataSetChanged()
        }
    }
}