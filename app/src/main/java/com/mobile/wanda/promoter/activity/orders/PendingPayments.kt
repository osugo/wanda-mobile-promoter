package com.mobile.wanda.promoter.activity.orders

import android.os.Bundle
import com.mobile.wanda.promoter.R
import com.mobile.wanda.promoter.activity.BaseActivity
import com.mobile.wanda.promoter.fragment.PendingPaymentsFragment
import com.mobile.wanda.promoter.model.PendingPayment

/**
 * Created by kombo on 08/03/2018.
 */

/**
 * Inflates fragment that shows the list of pending payments
 */
class PendingPayments : BaseActivity(), PendingPaymentsFragment.ClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        if (savedInstanceState == null && !isFinishing) {
            supportActionBar?.title = getString(R.string.orders)
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.contentFrame, PendingPaymentsFragment())
                    .commitAllowingStateLoss()
        }
    }

    //TODO load a fragment that show the order summary and allow the promter to complete the payment
    override fun onItemSelected(pendingPayment: PendingPayment) {
        if (!isFinishing) {
            supportActionBar?.title = getString(R.string.pending_payment)

        }
    }
}