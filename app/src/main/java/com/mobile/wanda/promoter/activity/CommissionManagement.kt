package com.mobile.wanda.promoter.activity

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.mobile.wanda.promoter.R
import com.mobile.wanda.promoter.adapter.CommissionMenuAdapter
import com.mobile.wanda.promoter.event.MenuSelectionEvent
import com.mobile.wanda.promoter.rest.RestClient
import com.mobile.wanda.promoter.rest.RestInterface
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.commission.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by kombo on 06/12/2017.
 */
class CommissionManagement : AppCompatActivity() {

    companion object {
        val TAG: String = CommissionManagement::class.java.simpleName
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMenuEvent(menuEvent: MenuSelectionEvent) {
        toggleViews(true)

        when (menuEvent.selectedMenu) {
            getString(R.string.check_commission) -> {
                checkCommission()
            }
            getString(R.string.request_commission) -> {
                requestCommission()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.commission)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        recycler.layoutManager = LinearLayoutManager(this)
        val adapter = CommissionMenuAdapter(getMenuOptions())
        recycler.adapter = adapter
    }

    private fun getMenuOptions(): ArrayList<String> {
        val menus = ArrayList<String>()
        menus.add(getString(R.string.check_commission))
        menus.add(getString(R.string.request_commission))

        return menus
    }

    private fun checkCommission() {
        RestClient.client.create(RestInterface::class.java).checkCommission()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    showCommissionLayout()
                    commissionIcon.setImageResource(R.drawable.ic_get_money)
                    commissionResponse.text = "Your commission is\n ${it.commission} KES"
                }, {
                    showSnackBar(it.localizedMessage)
                    Log.e(TAG, it.localizedMessage, it)
                })
    }

    private fun requestCommission() {
        RestClient.client.create(RestInterface::class.java).requestCommission()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    showCommissionLayout()
                    commissionIcon.setImageResource(R.drawable.ic_checked)
                    commissionResponse.text = getString(R.string.commission_request_processed)
                }, {
                    showSnackBar(it.localizedMessage)
                    Log.e(TAG, it.localizedMessage, it)
                })
    }

    private fun showCommissionLayout() {
        loadingIndicator.visibility = View.GONE
        commissionLayout.visibility = View.VISIBLE
    }

    private fun toggleViews(hideMenu: Boolean) {
        if (hideMenu) {
            recycler.visibility = View.GONE
            loadingIndicator.visibility = View.VISIBLE
            loadingIndicator.show()
        } else {
            loadingIndicator.visibility = View.GONE
            loadingIndicator.hide()
            commissionLayout.visibility = View.GONE
            recycler.visibility = View.VISIBLE
        }
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> {
                false
            }
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }
}