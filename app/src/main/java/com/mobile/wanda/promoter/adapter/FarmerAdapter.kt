package com.mobile.wanda.promoter.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.LinearLayout
import android.widget.TextView
import co.moonmonkeylabs.realmsearchview.RealmSearchViewHolder
import com.mobile.wanda.promoter.R
import com.mobile.wanda.promoter.model.responses.Farmer

/**
 * Created by kombo on 06/04/2018.
 */
class FarmerAdapter(private val farmers: ArrayList<Farmer>, private val clickListener: ClickListener) : RecyclerView.Adapter<FarmerAdapter.ViewHolder>(), Filterable {

    private var filteredList: ArrayList<Farmer>? = null
    private var farmerFilter: Filter? = null

    init {
        filteredList = farmers
        filter
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.farmer_layout_item, parent, false))
    }

    override fun getItemCount() = if (filteredList != null) filteredList!!.size else 0

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bindItem(farmers[position], clickListener)
    }

    override fun getFilter(): Filter {
        if(farmerFilter == null)
            farmerFilter = FarmerFilter()

        return farmerFilter!!
    }

    inner class ViewHolder(itemView: View) : RealmSearchViewHolder(itemView), View.OnClickListener {

        override fun onClick(v: View?) {
            clickListener?.onItemClicked(farmers[adapterPosition])
        }

        private var clickListener: FarmerAdapter.ClickListener? = null

        fun bindItem(farmer: Farmer, clickListener: FarmerAdapter.ClickListener) {
            this.clickListener = clickListener

            val name = itemView.findViewById(R.id.name) as TextView
            val layout = itemView.findViewById<LinearLayout>(R.id.layout)

            name.text = farmer.name
            layout.setOnClickListener(this)
        }

    }

    interface ClickListener {
        fun onItemClicked(farmer: Farmer)
    }

    inner class FarmerFilter: Filter() {

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filterResults = Filter.FilterResults()
            if (constraint != null && constraint.isNotEmpty()) {
                val tempList = ArrayList<Farmer>()

                // search content in friend list
                for (farmer in farmers) {
                    if (farmer.name!!.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        tempList.add(farmer)
                    }
                }

                filterResults.count = tempList.size
                filterResults.values = tempList
            } else {
                filterResults.count = farmers.size
                filterResults.values = farmers
            }

            return filterResults
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            filteredList = results?.values as ArrayList<Farmer>
            notifyDataSetChanged()
        }
    }
}