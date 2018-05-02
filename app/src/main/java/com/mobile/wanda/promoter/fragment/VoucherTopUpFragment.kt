package com.mobile.wanda.promoter.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import com.mobile.wanda.promoter.R
import com.mobile.wanda.promoter.activity.Home
import com.mobile.wanda.promoter.event.ErrorEvent
import com.mobile.wanda.promoter.model.errors.VoucherTopupErrors
import com.mobile.wanda.promoter.model.requests.FarmerVoucherTopUpRequest
import com.mobile.wanda.promoter.model.requests.PromoterVoucherTopUpRequest
import com.mobile.wanda.promoter.model.responses.VoucherTopupResponse
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
import org.jetbrains.anko.singleTop
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.indeterminateProgressDialog
import org.jetbrains.anko.support.v4.intentFor
import org.jetbrains.anko.support.v4.selector
import org.jetbrains.anko.yesButton

/**
 * Created by kombo on 07/03/2018.
 */

/**
 * Base class for all voucher top up transactions
 */
class VoucherTopUpFragment : Fragment(), View.OnClickListener {

    private var userId: Long? = null
    private val disposable = CompositeDisposable()

    private var topUp: Button? = null
    private var amount: EditText? = null
    private var parentLayout: LinearLayout? = null

    private val restInterface by lazy {
        RestClient.client.create(RestInterface::class.java)
    }

    companion object {
        private const val USER_ID: String = "userId"
        private const val CASH: String = "cash"
        private const val MPESA_NOW: String = "mpesa-now"
        private const val MPESA_LATER: String = "mpesa-later"

        fun newInstance(userId: Long): VoucherTopUpFragment {
            val bundle = Bundle().apply {
                putLong(USER_ID, userId)
            }

            val fragment = VoucherTopUpFragment()
            fragment.arguments = bundle

            return fragment
        }
    }

    /**
     * Show a dialog with error other than the one returned from server response
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
        val view = inflater.inflate(R.layout.voucher_top_up, container, false)

        userId = arguments?.getLong(USER_ID)

        topUp = view.findViewById(R.id.topUp) as Button
        amount = view.findViewById(R.id.amount) as EditText
        parentLayout = view.findViewById(R.id.parentLayout) as LinearLayout

        topUp?.setOnClickListener(this)

        return view
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.topUp -> {
                when {
                    amount?.text.isNullOrBlank() || amount?.text.toString() == "0" -> snackbar(parentLayout!!, getString(R.string.enter_valid_amount))
                    else -> if (NetworkHelper.isOnline(activity!!))
                        showOptions()
                    else
                        snackbar(parentLayout!!, getString(R.string.network_unavailable))
                }
            }
        }
    }

    /**
     * Show dialog with the various payment options
     */
    private fun showOptions() {
        val paymentOptions = listOf(getString(R.string.pay_cash), getString(R.string.mpesa_now), getString(R.string.mpesa_later))
        selector("Payment Options", paymentOptions, { _, i ->
            initPayment(paymentOptions[i])
        })
    }

    /**
     * Trigger the payment witht he appropriate payment method
     */
    private fun initPayment(option: String) {
        val paymentMethod: String? = when (option) {
            getString(R.string.pay_cash) -> CASH
            getString(R.string.mpesa_later) -> MPESA_LATER
            getString(R.string.mpesa_now) -> MPESA_NOW
            else -> null
        }

        paymentMethod?.let {
            if (!activity!!.isFinishing) {
                val dialog = indeterminateProgressDialog("Please wait")

                val call = if (userId != null)
                    restInterface.farmerVoucherTopUp(FarmerVoucherTopUpRequest(userId!!, it, amount!!.text.toString()))
                else
                    restInterface.promoterVoucherTopUp(PromoterVoucherTopUpRequest(it, amount!!.text.toString()))

                disposable.add(
                        call.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    dialog.dismiss()
                                    showMessage(it)
                                }) {
                                    dialog.dismiss()
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
            if (!activity!!.isFinishing)
            //show dialog with error messages
                alert(buildMessage(voucherTopupResponse.voucherTopupErrors!!).toString(), "Error") {
                    yesButton { it.dismiss() }
                }.show()
        } else {
            if (!activity!!.isFinishing)
            //show dialog with success message
                alert("Please wait for instructions on how to pay for the voucher", null) {
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

    override fun onDestroy() {
        super.onDestroy()

        disposable.dispose()
    }
}