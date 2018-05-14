package com.mobile.wanda.promoter.activity.orders

import android.os.Bundle
import com.mobile.wanda.promoter.R
import com.mobile.wanda.promoter.activity.BaseActivity
import com.mobile.wanda.promoter.fragment.OrderOptionsFragment
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor

/**
 * Created by kombo on 08/03/2018.
 */
class OrderManagement : BaseActivity(), OrderOptionsFragment.ClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        if (savedInstanceState == null && !isFinishing) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.contentFrame, OrderOptionsFragment())
                    .commitAllowingStateLoss()
        }
    }

    override fun onClickCreateOrder() {
        startActivity(intentFor<CreateOrders>().clearTop())
    }

    override fun onClickPendingOrders() {
        startActivity(intentFor<PendingOrders>().clearTop())
    }
}