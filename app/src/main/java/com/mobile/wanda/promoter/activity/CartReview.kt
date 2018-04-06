package com.mobile.wanda.promoter.activity

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mobile.wanda.promoter.R
import com.mobile.wanda.promoter.Wanda
import com.mobile.wanda.promoter.adapter.OrderReviewAdapter
import com.mobile.wanda.promoter.event.ErrorEvent
import com.mobile.wanda.promoter.model.PendingOrder
import com.mobile.wanda.promoter.rest.RestClient
import com.mobile.wanda.promoter.rest.RestInterface
import com.mobile.wanda.promoter.view.DividerItemDecoration
import io.reactivex.disposables.CompositeDisposable
import io.realm.Realm
import kotlinx.android.synthetic.main.cart_review.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton

/**
 * Created by kombo on 25/03/2018.
 */
class CartReview : BaseActivity(), View.OnClickListener {

    private var orderId: Int? = null
    private val disposable = CompositeDisposable()

    private val restInterface by lazy {
        RestClient.client.create(RestInterface::class.java)
    }

    private val realm by lazy {
        Realm.getInstance(Wanda.INSTANCE.realmConfig())
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
        setContentView(R.layout.cart_review)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        initRecyclerView()

        loadCart(intent.getStringExtra(PENDING_ORDER))

        completeOrder.setOnClickListener(this)
    }

    /**
     * Initialize the recyclerView
     */
    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this))
    }

    /**
     * Load items from cart onto recycler
     */
    private fun loadCart(orderString: String) {
        val pendingOrder = getPendingOrder(orderString)
        orderId = pendingOrder?.details?.id

        if (pendingOrder != null) {
            val adapter = OrderReviewAdapter(pendingOrder.details!!.items!!)
            recyclerView.adapter = adapter
        }
    }

    private fun getPendingOrder(orderString: String): PendingOrder? {
        val gson = Gson()
        val type = object : TypeToken<PendingOrder>() {}.type

        return gson.fromJson<PendingOrder>(orderString, type)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.completeOrder -> {
                showPickUpPrompt()
            }
        }
    }

    /**
     * Prompts the user to either choose whether products are to be delivered or picked up
     */
    private fun showPickUpPrompt() {
       alert("Would you like the delivery to be made to the farm?"){
           yesButton {
//               placeOrder(1)
           }
           noButton {
//               placeOrder(0)
           }
       }.show()
    }

    private fun showMessage(pendingOrder: PendingOrder) {
        if (pendingOrder.error == null) {
            alert(pendingOrder.message!!) {
                yesButton {
                    //TODO proceed to payment
                }
            }.show()
        } else {
            //TODO show error message here
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

    override fun onDestroy() {
        super.onDestroy()

        disposable.dispose()
    }

    companion object {
        const val PENDING_ORDER = "pendingOrder"
        private const val DELIVERY = "Delivery"
        private const val PICK_UP = "Pick up"
    }
}