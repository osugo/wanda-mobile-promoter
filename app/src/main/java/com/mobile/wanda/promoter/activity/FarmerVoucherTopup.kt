package com.mobile.wanda.promoter.activity

import android.os.Bundle
import com.mobile.wanda.promoter.R
import com.mobile.wanda.promoter.fragment.FarmersList
import com.mobile.wanda.promoter.fragment.VoucherTopUpFragment

/**
 * Created by kombo on 07/03/2018.
 */

/**
 * Class to hold two fragments, farmers list and voucher topup fragment
 */
class FarmerVoucherTopup: BaseActivity(), FarmersList.SelectionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.contentFrame, FarmersList())
                    .commitAllowingStateLoss()
        }
    }

    /**
     * Use retrieved id from farmer to init voucher top up fragment
     */
    override fun onFarmerSelected(id: Long, name: String) {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.contentFrame, VoucherTopUpFragment.newInstance(id))
                .commitAllowingStateLoss()
    }
}