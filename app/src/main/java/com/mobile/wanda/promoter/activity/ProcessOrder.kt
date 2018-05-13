package com.mobile.wanda.promoter.activity

import android.os.Bundle
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mobile.wanda.promoter.R
import com.mobile.wanda.promoter.fragment.OrderDetailsFragment
import com.mobile.wanda.promoter.fragment.OrderPaymentFragment
import com.mobile.wanda.promoter.model.PendingOrder

/**
 * Created by kombo on 10/05/2018.
 */
class ProcessOrder : BaseActivity(), OrderDetailsFragment.OnClickPayment {

    private var pendingOrderString: String? = null

    companion object {
        const val ORDER = "pendingOrder"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        pendingOrderString = intent.getStringExtra(ORDER)

        if (savedInstanceState == null && !isFinishing) {
            pendingOrderString?.let {
                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.contentFrame, OrderDetailsFragment.newInstance(it))
                        .commitAllowingStateLoss()
            }
        }
    }

    override fun initPayment() {
        pendingOrderString?.let {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.contentFrame, OrderPaymentFragment.newInstance(getOrderId(it)!!.toLong()))
                    .commitAllowingStateLoss()
        }
    }

    private fun getOrderId(string: String): Int? {
        val type = object : TypeToken<PendingOrder>() {}.type

        return Gson().fromJson<PendingOrder>(string, type).details?.id
    }

}