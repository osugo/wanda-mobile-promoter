package com.mobile.wanda.promoter.activity

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.mobile.wanda.promoter.R
import com.mobile.wanda.promoter.adapter.OptionsAdapter
import com.mobile.wanda.promoter.event.MenuSelectionEvent
import com.mobile.wanda.promoter.rest.RestClient
import com.mobile.wanda.promoter.rest.RestInterface
import com.mobile.wanda.promoter.util.NetworkHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.commission.*
import kotlinx.android.synthetic.main.loading_indicator.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.design.snackbar
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

/**
 * Created by kombo on 06/12/2017.
 */
class CommissionManagement : AppCompatActivity() {

    private val disposable = CompositeDisposable()

    private val restInterface by lazy {
        RestClient.client.create(RestInterface::class.java)
    }

    companion object {
        val TAG: String = CommissionManagement::class.java.simpleName
    }

    /**
     * EventBus listener that detect what button has been clicked and performs appropriate action
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMenuEvent(menuEvent: MenuSelectionEvent) {
        toggleViews(true)

        when (menuEvent.selectedMenu) {
            getString(R.string.check_commission) -> {
                if (NetworkHelper.isOnline(this))
                    checkCommission()
                else
                    snackbar(parentLayout, getString(R.string.network_unavailable))
            }
            getString(R.string.request_commission) -> {
                if (NetworkHelper.isOnline(this))
                    requestCommission()
                else
                    snackbar(parentLayout, getString(R.string.network_unavailable))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.commission)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        recycler.layoutManager = LinearLayoutManager(this)
        val adapter = OptionsAdapter(getMenuOptions())
        recycler.adapter = adapter
    }

    /**
     * Retrieve menu options
     */
    private fun getMenuOptions(): ArrayList<String> {
        val menus = ArrayList<String>()
        menus.add(getString(R.string.check_commission))
        menus.add(getString(R.string.request_commission))

        return menus
    }

    /**
     * Network call to check for commission
     */
    private fun checkCommission() {
        disposable.add(
                restInterface.checkCommission()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            showCommissionLayout()
                            moneyIcon.setImageResource(R.drawable.ic_get_money)
                            amountResponse.text = String.format("Your commission is\n %s KES", it.commission)
                        }, {
                            toggleViews(false)
                            showSnackBar(it.localizedMessage)
                            Log.e(TAG, it.localizedMessage, it)
                        })
        )
    }

    /**
     * Network call to request for commission
     */
    private fun requestCommission() {
        disposable.add(
                restInterface.requestCommission()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            showCommissionLayout()
                            moneyIcon.setImageResource(R.drawable.ic_checked)
                            amountResponse.text = getString(R.string.commission_request_processed)
                        }, {
                            showSnackBar(it.localizedMessage)
                            Log.e(TAG, it.localizedMessage, it)
                        })
        )
    }

    /**
     * Show commission layout once result is received from server
     */
    private fun showCommissionLayout() {
        loadingIndicator.visibility = View.GONE
        valueLayout.visibility = View.VISIBLE
    }

    /**
     * Hide or show menu depending on loading state
     */
    private fun toggleViews(hideMenu: Boolean) {
        if (hideMenu) {
            recycler.visibility = View.GONE
            loadingIndicator.visibility = View.VISIBLE
            loadingIndicator.smoothToShow()
        } else {
            loadingIndicator.visibility = View.GONE
            loadingIndicator.smoothToHide()
            valueLayout.visibility = View.GONE
            recycler.visibility = View.VISIBLE
        }
    }

    /**
     * Display message in a SnackBar on bottom of screen
     */
    private fun showSnackBar(message: String) {
        snackbar(parentLayout, message)
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
            else -> false
        }
    }

    /**
     * Register the EventBus when activity starts
     */
    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    /**
     * Unregister the EventBus when activity is stopped
     */
    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()

        disposable.dispose()
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }
}