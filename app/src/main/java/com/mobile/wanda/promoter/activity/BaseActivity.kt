package com.mobile.wanda.promoter.activity

import android.app.ProgressDialog
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.mobile.wanda.promoter.R
import org.jetbrains.anko.indeterminateProgressDialog
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

/**
 * Created by kombo on 21/03/2018.
 */

/**
 * All functions shared across all activities will be put here
 */
open class BaseActivity : AppCompatActivity() {

    private var dialog: ProgressDialog? = null

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

    protected fun showLoadingDialog() {
        if (!isFinishing)
            dialog = indeterminateProgressDialog(getString(R.string.please_wait))
    }

    protected fun hideLoadingDialog() {
        dialog?.dismiss()
    }
}