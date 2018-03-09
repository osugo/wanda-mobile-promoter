package com.mobile.wanda.promoter.activity

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.mobile.wanda.promoter.R
import com.mobile.wanda.promoter.fragment.OrderOptionsFragment
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

/**
 * Created by kombo on 08/03/2018.
 */
class OrderManagement : AppCompatActivity(), OrderOptionsFragment.ClickListener {

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

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed() //TODO handle fragment changes depending on currently inflated fragment
                true
            }
            else -> false
        }
    }

    override fun onClickCreateOrder() {
        startActivity(intentFor<CreateOrders>().clearTop())
    }

    override fun onClickPendingOrders() {
        startActivity(intentFor<PendingOrders>().clearTop())
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}