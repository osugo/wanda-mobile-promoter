package com.mobile.wanda.promoter.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import com.mobile.wanda.promoter.R
import com.mobile.wanda.promoter.event.ErrorEvent
import com.mobile.wanda.promoter.rest.ErrorHandler
import com.mobile.wanda.promoter.rest.RestClient
import com.mobile.wanda.promoter.rest.RestInterface
import com.mobile.wanda.promoter.util.NetworkHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.indeterminateProgressDialog
import org.jetbrains.anko.yesButton

/**
 * Created by kombo on 08/03/2018.
 */
class PromoterVoucherFragment : Fragment(), View.OnClickListener {

    private val disposable = CompositeDisposable()
    private var clickListener: ClickListener? = null

    private var checkBalance: Button? = null
    private var topUp: Button? = null
    private var parentLayout: LinearLayout? = null

    private val restInterface by lazy {
        RestClient.client.create(RestInterface::class.java)
    }

    /**
     * Interface to communicate with parent activity to launch top up fragment
     */
    interface ClickListener {
        fun onRequestVoucherTopUp()
    }

    /**
     * Show error if need be
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onErrorEvent(errorEvent: ErrorEvent) {
        if (!activity!!.isFinishing)
            alert(errorEvent.message, null) {
                yesButton {
                    it.dismiss()
                }
            }.show()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.promoter_voucher_layout, container, false)

        checkBalance = view.findViewById(R.id.checkBalance) as Button
        topUp = view.findViewById(R.id.topUp) as Button
        parentLayout = view.findViewById(R.id.parentLayout) as LinearLayout

        checkBalance?.setOnClickListener(this)
        topUp?.setOnClickListener(this)

        return view
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.checkBalance -> {
                if (NetworkHelper.isOnline(activity!!))
                    checkBalance()
                else
                    snackbar(parentLayout!!, getString(R.string.network_unavailable))
            }
            R.id.topUp -> clickListener?.onRequestVoucherTopUp()
        }
    }

    /**
     * Request balance from server
     */
    private fun checkBalance() {
        if (!activity!!.isFinishing) {
            val dialog = indeterminateProgressDialog("Please wait...")

            disposable.add(
                    restInterface.voucherBalance()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                dialog.dismiss()

                                showBalance(it.balance)
                            }) {
                                dialog.dismiss()
                                ErrorHandler.showError(it)
                            }
            )
        }
    }

    /**
     * Displays the promoter's voucher balance in a dialog
     */
    private fun showBalance(balance: Long) {
        if (!activity!!.isFinishing)
            alert(String.format("Your voucher balance is %s KES", balance), "Balance") {
                yesButton {
                    it.dismiss()
                }
            }.show()
    }

    /**
     * Register event bus
     */
    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    /**
     * Unregister event bus
     */
    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    /**
     * Attach the interface to the activity
     */
    override fun onAttach(context: Context?) {
        super.onAttach(context)

        try {
            clickListener = context as ClickListener
        } catch (c: ClassCastException) {
            throw ClassCastException(String.format("%s must implement ClickListener", context.toString()))
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        disposable.dispose()
    }
}