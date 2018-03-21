package com.mobile.wanda.promoter.activity

import android.content.Context
import android.support.v7.app.AppCompatActivity
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

/**
 * Created by kombo on 21/03/2018.
 */

/**
 * All functions shared across all activities will be put here
 */
open class BaseActivity: AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}