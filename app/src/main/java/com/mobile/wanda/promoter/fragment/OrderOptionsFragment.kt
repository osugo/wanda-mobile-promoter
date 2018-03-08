package com.mobile.wanda.promoter.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.mobile.wanda.promoter.R

/**
 * Created by kombo on 08/03/2018.
 */

/**
 * Gives user option to create an order or review pending orders
 */
class OrderOptionsFragment : Fragment(), View.OnClickListener {

    private var createOrder: Button? = null
    private var pendingOrders: Button? = null

    private var callback: ClickListener? = null

    /**
     * Interface to communicate with the parent activity and launch the appropriate activity
     */
    interface ClickListener {
        fun onClickCreateOrder()
        fun onClickPendingOrders()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.order_options, container, false)

        createOrder = view.findViewById(R.id.createOrder) as Button
        pendingOrders = view.findViewById(R.id.pendingOrders) as Button

        createOrder?.setOnClickListener(this)
        pendingOrders?.setOnClickListener(this)

        return view
    }

    /**
     * Button click listeners
     */
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.createOrder -> callback?.onClickCreateOrder()
            R.id.pendingOrders -> callback?.onClickPendingOrders()
        }
    }

    /**
     * Init the clicklistener interface and attach it to the activity
     */
    override fun onAttach(context: Context?) {
        super.onAttach(context)

        try {
            callback = context as ClickListener
        } catch (c: ClassCastException) {
            throw ClassCastException(String.format("%s must implement ClickListener", context.toString()))
        }
    }
}