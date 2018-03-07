package com.mobile.wanda.promoter.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mobile.wanda.promoter.R
import com.mobile.wanda.promoter.activity.Home
import com.mobile.wanda.promoter.event.ErrorEvent
import com.mobile.wanda.promoter.model.errors.VoucherTopupErrors
import com.mobile.wanda.promoter.model.requests.VoucherTopUpRequest
import com.mobile.wanda.promoter.model.responses.VoucherTopupResponse
import com.mobile.wanda.promoter.rest.ErrorHandler
import com.mobile.wanda.promoter.rest.RestClient
import com.mobile.wanda.promoter.rest.RestInterface
import com.mobile.wanda.promoter.util.NetworkHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.voucher_top_up.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.singleTop
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.indeterminateProgressDialog
import org.jetbrains.anko.support.v4.intentFor
import org.jetbrains.anko.support.v4.selector
import org.jetbrains.anko.yesButton

/**
 * Created by kombo on 07/03/2018.
 */
class VoucherTopUpFragment : Fragment(), View.OnClickListener {

    private var userId: Int? = null
    private val disposable = CompositeDisposable()

    private val restInterface by lazy {
        RestClient.client.create(RestInterface::class.java)
    }

    companion object {
        private val USER_ID: String = "userId"
        private val CASH: String = "cash"
        private val MPESA_NOW: String = "mpesa-now"
        private val MPESA_LATER: String = "mpesa-later"

        fun newInstance(userId: Int): VoucherTopUpFragment {
            val bundle = Bundle().apply {
                putInt(USER_ID, userId)
            }

            val fragment = VoucherTopUpFragment()
            fragment.arguments = bundle

            return fragment
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onErrorEvent(errorEvent: ErrorEvent) {
        if (!activity.isFinishing)
            alert(errorEvent.message, null) {
                yesButton {
                    it.dismiss()
                }
            }.show()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.voucher_top_up, container, false)

        userId = arguments.getInt(USER_ID)

        topUp.setOnClickListener(this)

        return view
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.topUp -> {
                if (NetworkHelper.isOnline(activity)) {
                    if ((amount.text.toString().isNotEmpty() || amount.text.toString() != "0") && userId != null)
                        showOptions()
                    else
                        snackbar(parentLayout, getString(R.string.enter_valid_amount))
                } else
                    snackbar(parentLayout, getString(R.string.network_unavailable))
            }
        }
    }

    private fun showOptions() {
        val paymentOptions = listOf(getString(R.string.pay_cash), getString(R.string.mpesa_now), getString(R.string.mpesa_later))
        selector("Payment Options", paymentOptions, { _, i ->
            initPayment(paymentOptions[i])
        })
    }

    private fun initPayment(option: String) {
        val paymentMethod: String? = when (option) {
            getString(R.string.pay_cash) -> CASH
            getString(R.string.mpesa_later) -> MPESA_LATER
            getString(R.string.mpesa_now) -> MPESA_NOW
            else -> null
        }

        paymentMethod?.let {
            if (!activity.isFinishing) {
                val dialog = indeterminateProgressDialog("Please wait")
                disposable.add(
                        restInterface.voucherTopUp(VoucherTopUpRequest(userId!!, it, amount.text.toString()))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    dialog.dismiss()
                                    showMessage(it)
                                }) {
                                    ErrorHandler.showError(it)
                                }
                )
            }
        }
    }

    /**
     * Show appropriate message of transaction; whether success or failure
     */
    private fun showMessage(voucherTopupResponse: VoucherTopupResponse) {
        if (voucherTopupResponse.error != null) {
            if (!activity.isFinishing)
                alert(buildMessage(voucherTopupResponse.voucherTopupErrors!!).toString(), "Error") {
                    yesButton { it.dismiss() }
                }.show()
        } else {
            if (!activity.isFinishing)
                alert(getString(R.string.farm_audit_successful), null) {
                    yesButton {
                        it.dismiss()

                        startActivity(intentFor<Home>().singleTop())
                    }
                }.show()
        }
    }

    /**
     * Build display message based on errors returned
     */
    private fun buildMessage(voucherTopupErrors: VoucherTopupErrors): StringBuilder {
        val message = StringBuilder()

        if (voucherTopupErrors.userId != null)
            message.append(voucherTopupErrors.userId[0]).append("\n \n")

        if (voucherTopupErrors.paymentOption != null)
            message.append(voucherTopupErrors.paymentOption[0]).append("\n \n")

        if (voucherTopupErrors.amount != null)
            message.append(voucherTopupErrors.amount[0]).append("\n \n")

        return message
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