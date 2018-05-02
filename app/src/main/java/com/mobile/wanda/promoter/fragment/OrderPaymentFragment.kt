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
import com.mobile.wanda.promoter.event.ErrorEvent
import com.mobile.wanda.promoter.model.requests.OrderPayment
import com.mobile.wanda.promoter.model.responses.PaymentResponse
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
import org.jetbrains.anko.support.v4.selector
import org.jetbrains.anko.yesButton

/**
 * Created by kombo on 08/03/2018.
 */

/**
 * Handles payment completion for pending orders
 */
class OrderPaymentFragment : Fragment(), View.OnClickListener {

    private val disposable = CompositeDisposable()

    private var amount: EditText? = null
    private var pay: Button? = null
    private var parentLayout: LinearLayout? = null

    private var orderId: Long? = 0

    /**
     * Lazily inflate the network client
     */
    private val restInterface by lazy {
        RestClient.client.create(RestInterface::class.java)
    }

    companion object {
        private val ORDER_ID: String = "order_id"
        private val CASH: String = "cash"
        private val MPESA_NOW: String = "mpesa-now"
        private val MPESA_LATER: String = "mpesa-later"

        fun newInstance(orderId: Long): OrderPaymentFragment {
            val bundle = Bundle().apply {
                putLong(ORDER_ID, orderId)
            }

            val fragment = OrderPaymentFragment()
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
        val view = inflater.inflate(R.layout.order_payment_layout, container, false)

        orderId = arguments?.getLong(ORDER_ID)

        amount = view.findViewById(R.id.amount) as EditText
        pay = view.findViewById(R.id.pay) as Button
        parentLayout = view.findViewById(R.id.parentLayout) as LinearLayout

        pay?.setOnClickListener(this)

        return view
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.pay -> {
                if (NetworkHelper.isOnline(activity!!)) {
                    showPaymentOptions()
                } else {
                    snackbar(parentLayout!!, getString(R.string.network_unavailable))
                }
            }
        }
    }

    /**
     * Show dialog with the various payment options
     */
    private fun showPaymentOptions() {
        val paymentOptions = listOf(getString(R.string.pay_cash), getString(R.string.mpesa_now), getString(R.string.mpesa_later))
        selector("Payment Options", paymentOptions, { _, i ->
            initPayment(paymentOptions[i])
        })
    }

    /**
     * Trigger the payment with the appropriate payment method
     */
    private fun initPayment(option: String) {
        val paymentMethod: String? = when (option) {
            getString(R.string.pay_cash) -> CASH
            getString(R.string.mpesa_later) -> MPESA_LATER
            getString(R.string.mpesa_now) -> MPESA_NOW
            else -> null
        }

        paymentMethod?.let {
            if (!activity!!.isFinishing && orderId != null) {
                val dialog = indeterminateProgressDialog("Please wait")

                disposable.add(
                        restInterface.payOrder(OrderPayment(orderId!!, it))
                                .subscribeOn(Schedulers.io())
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
     * Show payment response
     */
    private fun showMessage(paymentResponse: PaymentResponse) {
        if (paymentResponse.error != null) {
            if (paymentResponse.error && !activity!!.isFinishing) {
                alert(paymentResponse.message!!, null) {
                    yesButton {
                        it.dismiss()
                    }
                }.show()
            }
        } else {
            //show successful message here if server response isn't appropriate
            alert(paymentResponse.message!!, null) {
                yesButton {
                    it.dismiss()
                }
            }.show()
        }
    }

    /**
     * Register eventbus
     */
    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    /**
     * Uregister eventbus
     */
    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()

        disposable.dispose()
    }
}