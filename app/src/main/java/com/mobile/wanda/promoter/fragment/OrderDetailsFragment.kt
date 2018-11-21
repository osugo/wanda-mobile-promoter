package com.mobile.wanda.promoter.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mobile.wanda.promoter.R
import com.mobile.wanda.promoter.model.PendingOrder
import org.jetbrains.anko.bundleOf

/**
 * Created by kombo on 10/05/2018.
 */
class OrderDetailsFragment : Fragment() {

    private var pendingOrder: PendingOrder? = null
    private var onClickPayment: OnClickPayment? = null

    @BindView(R.id.total)
    lateinit var total: TextView
    @BindView(R.id.itemsCost)
    lateinit var itemsCost: TextView
    @BindView(R.id.reference)
    lateinit var reference: TextView
    @BindView(R.id.deliveryCost)
    lateinit var deliveryCost: TextView

    @OnClick(R.id.makePayment)
    fun onClickMakePayment() {
        onClickPayment?.initPayment()
    }

    companion object {

        const val ORDER = "order"
        const val CURRENCY = "KES"

        fun newInstance(orderString: String): OrderDetailsFragment {
            val fragment = OrderDetailsFragment()
            fragment.arguments = bundleOf(ORDER to orderString)

            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.process_order, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ButterKnife.bind(this, view)

        pendingOrder = getPendingOrder(arguments?.getString(ORDER))

        initViews()
    }

    private fun initViews(){
        pendingOrder?.let {
            reference.text = it.details?.reference
            itemsCost.text = String.format("$CURRENCY %s", it.details?.itemsCost)
            total.text = String.format("$CURRENCY %s", it.details?.totalCost)
            deliveryCost.text = String.format("$CURRENCY %s", it.details?.deliveryCost)
        }
    }

    private fun getPendingOrder(string: String?): PendingOrder {
        val type = object : TypeToken<PendingOrder>() {}.type

        return Gson().fromJson<PendingOrder>(string, type)
    }

    interface OnClickPayment {
        fun initPayment()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        try {
            onClickPayment = context as OnClickPayment
        } catch (c: ClassCastException){
            throw ClassCastException(String.format("%s must implement OnClickPayment", context.toString()))
        }
    }
}