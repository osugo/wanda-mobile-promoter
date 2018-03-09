package com.mobile.wanda.promoter.activity

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.mobile.wanda.promoter.R
import com.mobile.wanda.promoter.fragment.PromoterVoucherFragment
import com.mobile.wanda.promoter.fragment.VoucherTopUpFragment
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

/**
 * Created by kombo on 07/03/2018.
 */
class PromoterVoucher : AppCompatActivity(), PromoterVoucherFragment.ClickListener {

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
                    .replace(R.id.contentFrame, VoucherTopUpFragment.newInstance(1))
                    .commitAllowingStateLoss()
    }

    /**
     * Handle hardware back button press
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}