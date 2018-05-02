package com.mobile.wanda.promoter.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.mobile.wanda.promoter.R
import com.mobile.wanda.promoter.Wanda
import com.mobile.wanda.promoter.adapter.OrdersAdapter
import com.mobile.wanda.promoter.model.PendingOrder
import io.realm.Realm

/**
 * Created by kombo on 08/03/2018.
 */

/**
 * Shows list of pending orders and allows for payment
 */
class OrdersListFragment : Fragment() {

    private var loadingIndicator: ProgressBar? = null
    private var recyclerView: RecyclerView? = null
    private var empty: TextView? = null

    private var ordersAdapter: OrdersAdapter? = null
    private var callback: ClickListener? = null

    interface ClickListener {
        fun onOrderSelected(order: PendingOrder)
    }

    /**
     * Lazily init Realm db
     */
    private val realm by lazy {
        Realm.getInstance(Wanda.INSTANCE.realmConfig())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.pending_orders, container, false)

        loadingIndicator = view.findViewById(R.id.loadingIndicator) as ProgressBar
        recyclerView = view.findViewById(R.id.recyclerView) as RecyclerView
        empty = view.findViewById(R.id.empty) as TextView

        showPendingOrders()

        return view
    }

    /**
     * Load pending orders from network
     */

    //TODO laod pending orders from network call
    private fun showPendingOrders() {
//        showLoadingIndicator()
//
//        val pendingOrders = realm.where(PendingOrder::class.java).findAll()
//        if (pendingOrders.isNotEmpty()) {
//            hideLoadingIndicator()
//
//            ordersAdapter = OrdersAdapter(pendingOrders, object : OrdersAdapter.ClickListener {
//
//                override fun onOrderClicked(order: PendingOrder) {
//                    callback?.onOrderSelected(order)
//                }
//
//            })
//
//            recyclerView?.visibility = View.VISIBLE
//            recyclerView?.adapter = ordersAdapter
//        } else {
//            //show empty view if there are no pending orders
//            hideLoadingIndicator()
//            recyclerView?.visibility = View.GONE
//
//            empty?.visibility = View.VISIBLE
//        }
    }

    private fun showLoadingIndicator() {
        empty?.visibility = View.GONE
        loadingIndicator?.visibility = View.VISIBLE
    }

    private fun hideLoadingIndicator() {
        loadingIndicator?.visibility = View.GONE
    }

    /**
     * Attach interface to fragment
     */
    override fun onAttach(context: Context?) {
        super.onAttach(context)

        try {
            callback = context as ClickListener
        } catch (c: ClassCastException) {
            throw ClassCastException(String.format("%s must implement ClickListener", context.toString()))
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        realm?.close()
    }
}