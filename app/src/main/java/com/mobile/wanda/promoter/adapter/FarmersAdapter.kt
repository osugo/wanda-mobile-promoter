package com.mobile.wanda.promoter.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import co.moonmonkeylabs.realmsearchview.RealmSearchAdapter
import co.moonmonkeylabs.realmsearchview.RealmSearchViewHolder
import com.mobile.wanda.promoter.R
import com.mobile.wanda.promoter.model.responses.Farmer
import io.realm.Realm

/**
 * Created by kombo on 04/03/2018.
 */
class FarmersAdapter(context: Context, realm: Realm, filterColumnName: String) :
        RealmSearchAdapter<Farmer, FarmersAdapter.ViewHolder>(context, realm, filterColumnName) {

    override fun onBindRealmViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bindItem(realmResults[position]!!)
    }

    override fun onCreateRealmViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.farmer_layout_item, parent, false))
    }

    inner class ViewHolder(itemView: View) : RealmSearchViewHolder(itemView) {

        fun bindItem(farmer: Farmer) {
            val name = itemView.findViewById(R.id.name) as TextView

            name.text = farmer.name
        }
    }
}