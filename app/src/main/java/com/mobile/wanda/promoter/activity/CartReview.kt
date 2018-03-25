package com.mobile.wanda.promoter.activity

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.mobile.wanda.promoter.R
import com.mobile.wanda.promoter.Wanda
import com.mobile.wanda.promoter.adapter.CartItemsAdapter
import com.mobile.wanda.promoter.model.CartItem
import com.mobile.wanda.promoter.view.DividerItemDecoration
import io.realm.Realm
import kotlinx.android.synthetic.main.pending_orders.*

/**
 * Created by kombo on 25/03/2018.
 */
class CartReview : BaseActivity() {

    private val realm by lazy {
        Realm.getInstance(Wanda.INSTANCE.realmConfig())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cart_review)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        initRecyclerView()

        loadCart()
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this))
    }

    private fun loadCart() {
        val cartItems = realm.where(CartItem::class.java).findAll()

        if(cartItems.isNotEmpty()){
            val adapter = CartItemsAdapter(cartItems, true, true)
            recyclerView.adapter = adapter
        }
    }
}