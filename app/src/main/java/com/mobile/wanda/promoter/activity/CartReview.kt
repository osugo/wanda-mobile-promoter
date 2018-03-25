package com.mobile.wanda.promoter.activity

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.mobile.wanda.promoter.R
import com.mobile.wanda.promoter.Wanda
import com.mobile.wanda.promoter.adapter.CartItemsAdapter
import com.mobile.wanda.promoter.event.ErrorEvent
import com.mobile.wanda.promoter.model.Cart
import com.mobile.wanda.promoter.model.CartItem
import com.mobile.wanda.promoter.model.orders.Order
import com.mobile.wanda.promoter.model.orders.OrderItem
import com.mobile.wanda.promoter.rest.ErrorHandler
import com.mobile.wanda.promoter.rest.RestClient
import com.mobile.wanda.promoter.rest.RestInterface
import com.mobile.wanda.promoter.view.DividerItemDecoration
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmList
import kotlinx.android.synthetic.main.cart_review.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.alert
import org.jetbrains.anko.yesButton

/**
 * Created by kombo on 25/03/2018.
 */
class CartReview : BaseActivity(), View.OnClickListener {

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

        loadCart()

        checkout.setOnClickListener(this)
    }

    /**
     * Initialize the recyclerview
     */
    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this))
    }

    /**
     * Load items from cart onto recycler
     */
    private fun loadCart() {
        val cartItems = realm.where(CartItem::class.java).findAll()

        if (cartItems.isNotEmpty()) {
            val adapter = CartItemsAdapter(cartItems, true, true)
            recyclerView.adapter = adapter
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.checkout -> {
                createOrder()
            }
        }
    }

    private fun createOrder() {
        val cart = realm.where(Cart::class.java).findFirst()

        val orderItems = RealmList<OrderItem>()
        cart?.items?.forEach {
            orderItems.add(OrderItem(it.id, it.quantity))
        }

        val order = Order()
        order.apply {
            farmerId = cart!!.id
            items = orderItems
        }

        showLoadingDialog()
        restInterface.placeOrder(order)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    hideLoadingDialog()
                }) {
                    hideLoadingDialog()
                    ErrorHandler.showError(it)
                }
    }

    private fun showMessage(){

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