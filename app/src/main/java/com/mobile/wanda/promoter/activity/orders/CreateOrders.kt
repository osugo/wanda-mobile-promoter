package com.mobile.wanda.promoter.activity.orders

import android.os.Bundle
import com.mobile.wanda.promoter.R
import com.mobile.wanda.promoter.Wanda
import com.mobile.wanda.promoter.activity.BaseActivity
import com.mobile.wanda.promoter.fragment.FarmersList
import com.mobile.wanda.promoter.model.Cart
import com.mobile.wanda.promoter.model.CartItem
import io.reactivex.Completable
import io.realm.Realm
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor

/**
 * Created by kombo on 08/03/2018.
 */
class CreateOrders : BaseActivity(), FarmersList.SelectionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        if (savedInstanceState == null && !isFinishing) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.contentFrame, FarmersList())
                    .commitAllowingStateLoss()
        }
    }

    /**
     * Start creating order for selected farmer
     */
    override fun onFarmerSelected(id: Long, name: String) {
        Completable.fromAction {
            clearCart()
        }.subscribe {
            startActivity(intentFor<ProductsList>("farmerId" to id, "farmerName" to name).clearTop())
        }
    }

    /**
     * Remove all items from cart before beginning each order
     */
    private fun clearCart() {
        Realm.getInstance(Wanda.INSTANCE.realmConfig()).use {
            val items = it.where(Cart::class.java).findAll()
            if (items.isNotEmpty())
                it.executeTransaction {
                    items.deleteAllFromRealm()
                }

            val cartItems = it.where(CartItem::class.java).findAll()
            if (cartItems.isNotEmpty())
                it.executeTransaction {
                    cartItems.deleteAllFromRealm()
                }
        }
    }
}