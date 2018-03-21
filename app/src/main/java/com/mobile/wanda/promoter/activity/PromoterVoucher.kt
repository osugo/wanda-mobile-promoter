package com.mobile.wanda.promoter.activity

import android.os.Bundle
import com.mobile.wanda.promoter.R
import com.mobile.wanda.promoter.fragment.PromoterVoucherFragment
import com.mobile.wanda.promoter.fragment.VoucherTopUpFragment

/**
 * Created by kombo on 07/03/2018.
 */
class PromoterVoucher : BaseActivity(), PromoterVoucherFragment.ClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        if (savedInstanceState == null && !isFinishing) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.contentFrame, PromoterVoucherFragment())
                    .commitAllowingStateLoss()
        }
    }

    /**
     * Find the promoter's id and send it as the parameter
     */
    override fun onRequestVoucherTopUp() {
        if (!isFinishing)
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.contentFrame, VoucherTopUpFragment())
                    .commitAllowingStateLoss()
    }
}