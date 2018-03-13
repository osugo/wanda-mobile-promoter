package com.mobile.wanda.promoter.activity

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.mobile.wanda.promoter.R
import com.mobile.wanda.promoter.fragment.FarmCreationFragment
import com.mobile.wanda.promoter.fragment.FarmersList
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

/**
 * Created by kombo on 05/03/2018.
 */
class AddFarm : AppCompatActivity(), FarmersList.SelectionListener {

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
     * Show farm report fragment after selecting farmer
     */
    override fun onFarmerSelected(id: Long, name: String) {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.contentFrame, FarmCreationFragment.newInstance(id, name))
                .commitAllowingStateLoss()
    }

    /**
     * Listener for hardware back button press
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