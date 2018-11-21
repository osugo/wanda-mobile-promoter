package com.mobile.wanda.promoter.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.mobile.wanda.promoter.R
import com.mobile.wanda.promoter.adapter.PendingPaymentsAdapter
import com.mobile.wanda.promoter.event.ErrorEvent
import com.mobile.wanda.promoter.model.PendingPayment
import com.mobile.wanda.promoter.model.PendingPaymentsList
import com.mobile.wanda.promoter.network.ErrorHandler
import com.mobile.wanda.promoter.network.RestClient
import com.mobile.wanda.promoter.network.RestInterface
import com.mobile.wanda.promoter.util.NetworkHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.yesButton

/**
 * Created by kombo on 08/03/2018.
 */

/**
 * Shows list of pending orders and allows for payment
 */
class PendingPaymentsFragment : Fragment() {

    @BindView(R.id.loadingIndicator)
    lateinit var loadingIndicator: ProgressBar
    @BindView(R.id.recyclerView)
    lateinit var recyclerView: RecyclerView
    @BindView(R.id.empty)
    lateinit var empty: TextView
    @BindView(R.id.parentLayout)
    lateinit var parentLayout: RecyclerView

    private var pendingPaymentsAdapter: PendingPaymentsAdapter? = null
    private var callback: ClickListener? = null

    private val compositeDisposable = CompositeDisposable()

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onErrorEvent(errorEvent: ErrorEvent) {
        hideLoadingIndicator()

        alert(errorEvent.message) {
            yesButton {
                it.dismiss()
            }
        }.show()
    }

    interface ClickListener {
        fun onItemSelected(pendingPayment: PendingPayment)
    }

    private val restInterface by lazy {
        RestClient.client.create(RestInterface::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.pending_orders, container, false)

        ButterKnife.bind(this, view)

        recyclerView.layoutManager = LinearLayoutManager(activity as? AppCompatActivity)

        showPendingOrders()

        return view
    }

    /**
     * Load pending orders from network
     */
    private fun showPendingOrders() {
        if (!activity!!.isFinishing && NetworkHelper.isOnline(activity as AppCompatActivity)) {
            showLoadingIndicator()

            compositeDisposable.add(
                    restInterface.pendingPayments
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                hideLoadingIndicator()

                                showPendingPayments(it)
                            }) {
                                ErrorHandler.showError(it)
                            }
            )
        } else {
            snackbar(parentLayout, getString(R.string.network_unavailable))
        }
    }

    private fun showPendingPayments(pendingPaymentsList: PendingPaymentsList){
        if(pendingPaymentsList.pendingPayments!!.isNotEmpty()){
            pendingPaymentsAdapter = PendingPaymentsAdapter(pendingPaymentsList.pendingPayments!!, object: PendingPaymentsAdapter.ClickListener {
                override fun onClickPendingOrder(pendingPayment: PendingPayment) {
                    callback?.onItemSelected(pendingPayment)
                }
            })
            recyclerView.adapter = pendingPaymentsAdapter
        } else {
            alert(getString(R.string.no_pending_payments)){
                yesButton {
                    activity?.finish()
                }
            }
        }
    }

    private fun showLoadingIndicator() {
        empty.visibility = View.GONE
        loadingIndicator.visibility = View.VISIBLE
    }

    private fun hideLoadingIndicator() {
        loadingIndicator.visibility = View.GONE
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    /**
     * Attach interface to fragment
     */
    override fun onAttach(context: Context?) {
        super.onAttach(context)

        try {
            callback = context as ClickListener
        } catch (c: ClassCastException) {
            throw ClassCastException(String.format("%s must implement ClickListener", context.toString()))
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        compositeDisposable.clear()
    }
}