package com.mobile.wanda.promoter.activity

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.mobile.wanda.promoter.R
import com.mobile.wanda.promoter.event.ErrorEvent
import com.mobile.wanda.promoter.rest.ErrorHandler
import com.mobile.wanda.promoter.rest.RestClient
import com.mobile.wanda.promoter.rest.RestInterface
import com.mobile.wanda.promoter.util.NetworkHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.commissions.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.*
import org.jetbrains.anko.design.snackbar

/**
 * Created by kombo on 09/03/2018.
 */
class Commissions : BaseActivity(), View.OnClickListener {

    private val disposable = CompositeDisposable()

    private val restInterface by lazy {
        RestClient.client.create(RestInterface::class.java)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onErrorEvent(errorEvent: ErrorEvent) {
        if (!isFinishing)
            alert(errorEvent.message, null) {
                yesButton {
                    it.dismiss()
                }
            }.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.commissions)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        checkCommission?.setOnClickListener(this)
        requestCommission?.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.checkCommission -> {
                if (NetworkHelper.isOnline(this)) {
                    checkBalance()
                } else
                    snackbar(parentLayout!!, getString(R.string.network_unavailable))
            }
            R.id.requestCommission -> {
                if (NetworkHelper.isOnline(this)) {
                    requestCommission()
                } else
                    snackbar(parentLayout!!, getString(R.string.network_unavailable))
            }
        }
    }

    /**
     * Check for commission balance
     */
    private fun checkBalance() {
        if (!isFinishing) {
            val dialog = indeterminateProgressDialog("Please wait..")

            disposable.add(
                    restInterface.checkCommission()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                dialog.dismiss()

                                showBalance(it.commission)
                            }) {
                                dialog.dismiss()
                                ErrorHandler.showError(it)
                            }
            )
        }
    }

    /**
     * Request for commission
     */
    private fun requestCommission() {
        if (!isFinishing) {
            val dialog = indeterminateProgressDialog("Please wait...")

            disposable.add(
                    restInterface.requestCommission()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                dialog.dismiss()

                                showMessage(it.message!!)
                            }) {
                                dialog.dismiss()
                                ErrorHandler.showError(it)
                            }
            )
        }
    }

    /**
     * Displays the response
     */
    private fun showBalance(balance: Long) {
        if (!isFinishing)
            alert(String.format("Your commission is %s KES", balance), "Commission") {
                yesButton {
                    startActivity(intentFor<Home>().clearTop())
                }
            }.show()
    }

    private fun showMessage(message: String) {
        alert(message) {
            yesButton {
                startActivity(intentFor<Home>().clearTop())
            }
        }.show()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        disposable.dispose()
    }

}