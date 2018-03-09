package com.mobile.wanda.promoter.activity

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.mobile.wanda.promoter.R
import com.mobile.wanda.promoter.fragment.OrderPaymentFragment
import com.mobile.wanda.promoter.fragment.OrdersListFragment
import com.mobile.wanda.promoter.model.orders.PendingOrder
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

/**
 * Created by kombo on 08/03/2018.
 */

/**
 * Inflates fragment that shows the list of pending orders
 */
class PendingOrders : AppCompatActivity(), OrdersListFragment.ClickListener {

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
                    .replace(R.id.contentFrame, OrderPaymentFragment.newInstance(order.orderId!!))
                    .commitAllowingStateLoss()
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}