package com.mobile.wanda.promoter.activity

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

/**
 * Created by kombo on 21/03/2018.
 */

/**
 * All functions shared across all activities will be put here
 */
open class BaseActivity: AppCompatActivity() {

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