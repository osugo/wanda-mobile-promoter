package com.mobile.wanda.promoter.activity

import android.os.Bundle
import com.mobile.wanda.promoter.R
import com.mobile.wanda.promoter.fragment.OrderPaymentFragment
import com.mobile.wanda.promoter.fragment.OrdersListFragment
import com.mobile.wanda.promoter.model.PendingOrder

/**
 * Created by kombo on 08/03/2018.
 */

/**
 * Inflates fragment that shows the list of pending orders
 */
class PendingOrders : BaseActivity(), OrdersListFragment.ClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        if (savedInstanceState == null && !isFinishing) {
            supportActionBar?.title = getString(R.string.orders)
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.contentFrame, OrdersListFragment())
                    .commitAllowingStateLoss()
        }
    }

    override fun onOrderSelected(order: PendingOrder) {
        if (!isFinishing) {
            supportActionBar?.title = getString(R.string.pay_order)
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.contentFrame, OrderPaymentFragment.newInstance(order.orderDetails!!.id!!))
                    .commitAllowingStateLoss()
        }
    }
}