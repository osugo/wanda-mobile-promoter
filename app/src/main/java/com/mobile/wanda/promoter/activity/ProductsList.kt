package com.mobile.wanda.promoter.activity

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.InputType
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.mobile.wanda.promoter.R
import com.mobile.wanda.promoter.Wanda
import com.mobile.wanda.promoter.adapter.ProductsAdapter
import com.mobile.wanda.promoter.event.ErrorEvent
import com.mobile.wanda.promoter.model.Cart
import com.mobile.wanda.promoter.model.orders.Product
import com.mobile.wanda.promoter.model.orders.ProductResults
import com.mobile.wanda.promoter.model.responses.Farmer
import com.mobile.wanda.promoter.rest.ErrorHandler
import com.mobile.wanda.promoter.rest.RestClient
import com.mobile.wanda.promoter.rest.RestInterface
import com.mobile.wanda.promoter.util.CartUtil
import com.mobile.wanda.promoter.util.NetworkHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmList
import io.realm.exceptions.RealmException
import kotlinx.android.synthetic.main.loading_indicator.*
import kotlinx.android.synthetic.main.products_list.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.*
import org.jetbrains.anko.design.snackbar

/**
 * Created by kombo on 09/03/2018.
 */
class ProductsList : BaseActivity() {

    private val disposable = CompositeDisposable()
    private var productsAdapter: ProductsAdapter? = null
    private var options: List<String>? = null

    private lateinit var productList: ArrayList<Product>
    private lateinit var alertDialog: DialogInterface

    private var farmer: Farmer? = null

    private val restInterface by lazy {
        RestClient.client.create(RestInterface::class.java)
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
        setContentView(R.layout.products_list)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        farmer = initFarmer(intent)

        products.layoutManager = LinearLayoutManager(this)

        getProducts()

        clear.setOnClickListener {
            clearCart()
        }
    }

    /**
     * Create farmer from passed values
     */
    private fun initFarmer(intent: Intent): Farmer? {
        return Farmer(intent.getLongExtra("farmerId", 0), intent.getStringExtra("farmerName"))
    }

    /**
     * Retrieve and show product categories
     */
    private fun getProducts() {
        if (NetworkHelper.isOnline(this)) {
            showLoadingIndicator()
            disposable.add(
                    restInterface.getProductCategories()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                hideLoadingIndicator()

                                showResults(it)
                            }) {
                                hideLoadingIndicator()
                                ErrorHandler.showError(it)
                            }
            )
        } else
            snackbar(parentLayout, getString(R.string.network_unavailable))
    }

    /**
     * Load products from the selected categories
     */
    private fun showResults(productResults: ProductResults) {
        productsAdapter = ProductsAdapter(productResults, object : ProductsAdapter.ClickListener {
            override fun onProductSelected(product: Product) {
                loadProducts(product.id)
            }
        })
        products.adapter = productsAdapter
    }

    /**
     * Retrieve products from a given category
     */
    private fun loadProducts(id: Long) {
        if (NetworkHelper.isOnline(this)) {
            showLoadingDialog()

            disposable.add(
                    restInterface.searchProducts(id)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                hideLoadingDialog()

                                showProducts(it)
                            }) {
                                hideLoadingIndicator()
                                ErrorHandler.showError(it)
                            }
            )
        } else
            snackbar(parentLayout, getString(R.string.network_unavailable))
    }

    /**
     * Show category specific products or error dialog if empty
     */
    private fun showProducts(products: ProductResults) {
        productList = products.items
        options = productList.map { it.name }

        if (options!!.isNotEmpty())
            selector("Products", options!!, { _, i ->
                getProductVariation(options!![i])
            })
        else {
            if (!isFinishing)
                alert(getString(R.string.empty_products)) {
                    yesButton {
                        it.dismiss()
                    }
                }.show()
        }
    }

    /**
     * get product variations
     */
    private fun getProductVariation(productName: String?) {
        val product = productList.first { it.name == productName }

        if (NetworkHelper.isOnline(this)) {
            showLoadingDialog()

            disposable.add(
                    restInterface.getProductVariations(product.id)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                hideLoadingDialog()
                                showProductVariations(it)
                            }) {
                                hideLoadingDialog()
                                ErrorHandler.showError(it)
                            }
            )
        }
    }

    private fun showProductVariations(products: ProductResults) {
        productList = products.items
        options = productList.map { it.name }

        if (options!!.isNotEmpty()) {
            selector("Products", options!!, { _, i ->
                getQuantity(options!![i])
            })
        }
    }

    /**
     * Creates the quantity dialog using Anko DSL
     */
    private fun getQuantity(product: String) {
        alertDialog = alert {
            customView {
                verticalLayout {
                    textView {
                        text = getString(R.string.enter_quantity)
                        textSize = 18f
                        textColor = Color.BLACK
                    }.lparams {
                        topMargin = dip(17)
                        horizontalMargin = dip(17)
                        bottomMargin = dip(10)
                    }

                    val quantity = editText {
                        inputType = InputType.TYPE_CLASS_NUMBER
                        background = ContextCompat.getDrawable(this@ProductsList, R.drawable.textbox_bg)
                    }.lparams(width = matchParent, height = wrapContent) {
                        bottomMargin = dip(10)
                        horizontalMargin = dip(17)
                    }

                    button("ADD TO CART") {
                        background = ContextCompat.getDrawable(this@ProductsList, R.color.colorPrimary)
                        textColor = Color.WHITE
                    }.lparams(width = matchParent, height = matchParent) {
                        topMargin = dip(10)
                    }.setOnClickListener {
                        if (quantity.text.isNullOrBlank())
                            snackbar(parentLayout!!, getString(R.string.enter_valid_quantity))
                        else
                            addToCart(product, quantity.text.toString().toInt())

                        alertDialog.dismiss()
                    }
                }
            }
        }.show()
    }

    /**
     * Add item to cart after selecting quantity
     */
    private fun addToCart(item: String, quantity: Int) {
        val product = productList.first { it.name == item }

        try {
            Realm.getInstance(Wanda.INSTANCE.realmConfig()).use {
                it.executeTransaction {
                    val results = it.where(Cart::class.java).findAll()
                    val cart: Cart

                    if (results.isEmpty()) {
                        cart = Cart()
                        cart.apply {
                            id = farmer?.id
                            items = RealmList()
                            items!!.add(CartUtil.getCartItem(product, quantity))
                        }
                    } else {
                        cart = results.first()!!
                        cart.apply {
                            items!!.add(CartUtil.getCartItem(product, quantity))
                        }
                    }

                    it.copyToRealmOrUpdate(cart)

                    cartItems.text = resources.getQuantityString(R.plurals.no_of_items, cart.items!!.size, cart.items!!.size)

                    toast("Added to cart")
                }
            }
        } catch (e: RealmException) {
            Log.e(TAG, e.localizedMessage, e)
        }
    }

    /**
     * Clear cart
     */
    private fun clearCart() {
        try {
            Realm.getInstance(Wanda.INSTANCE.realmConfig()).use {
                it.executeTransaction {
                    val cart = it.where(Cart::class.java).equalTo("id", farmer?.id).findFirst()

                    if (cart != null) {
                        cart.items?.clear()
                        it.copyToRealmOrUpdate(cart)

                        cartItems.text = resources.getQuantityString(R.plurals.no_of_items, 0, 0)

                        toast("Cart cleared")
                    }
                }
            }
        } catch (e: RealmException) {
            Log.e(TAG, e.localizedMessage, e)
        }
    }

    private fun showLoadingIndicator() {
        loadingIndicator.visibility = View.VISIBLE
        loadingIndicator.show()
    }

    private fun hideLoadingIndicator() {
        loadingIndicator.visibility = View.GONE
        loadingIndicator.hide()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_product_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.done -> {
                Realm.getInstance(Wanda.INSTANCE.realmConfig()).use {
                    val items = it.where(Cart::class.java).findFirst()?.items

                    if (items != null && items.isNotEmpty())
                        startActivity<CartReview>()
                    else
                        snackbar(parentLayout, "Your cart is empty")
                }

                true
            }
            else -> super.onOptionsItemSelected(item)
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
        val TAG: String = ProductsList::class.java.simpleName
    }
}