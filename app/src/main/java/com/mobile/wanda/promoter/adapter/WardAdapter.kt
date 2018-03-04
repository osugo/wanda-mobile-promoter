package com.mobile.wanda.promoter.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.mobile.wanda.promoter.model.responses.Ward
import io.realm.RealmList

/**
 * Created by kombo on 04/03/2018.
 */
class WardAdapter(context: Context, private val wards: RealmList<Ward>): ArrayAdapter<Ward>(context, android.R.layout.select_dialog_item) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val ward = getItem(position)

        val holder: ViewHolder
        val retView: View

        if(convertView == null){
            holder = ViewHolder()
            retView = LayoutInflater.from(parent?.context).inflate(android.R.layout.select_dialog_item, parent, false)
            holder.text = retView.findViewById(android.R.id.text1) as TextView
            retView.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
            retView = convertView
        }

        holder.text?.text = ward.name

        return retView
    }

    override fun getCount(): Int = wards.size

    inner class ViewHolder {
        var text: TextView? = null
    }
}