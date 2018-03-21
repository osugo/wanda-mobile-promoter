package com.mobile.wanda.promoter.activity

import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.InputType
import android.view.View
import com.mobile.wanda.promoter.R
import com.mobile.wanda.promoter.adapter.ProductsAdapter
import com.mobile.wanda.promoter.event.ErrorEvent
import com.mobile.wanda.promoter.model.orders.Product
import com.mobile.wanda.promoter.model.orders.ProductResults
import com.mobile.wanda.promoter.rest.ErrorHandler
import com.mobile.wanda.promoter.rest.RestClient
import com.mobile.wanda.promoter.rest.RestInterface
import com.mobile.wanda.promoter.util.NetworkHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
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

        products.layoutManager = LinearLayoutManager(this)

        getProducts()
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
    private fun loadProducts(id: Int) {
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
        val options = products.items.map { it.name }

        if (options.isNotEmpty())
            selector("Products", options, { _, i ->
                toast("Clicked ${options[i]}")
                getQuantity(options[i])
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
     * Creates the quantity dialog using Anko DSL
     */
    private fun getQuantity(product: String) {
        alert {
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

                    button(getString(R.string.confirm)) {
                        background = ContextCompat.getDrawable(this@ProductsList, R.color.colorPrimary)
                        textColor = Color.WHITE
                    }.lparams(width = matchParent, height = matchParent) {
                        topMargin = dip(10)
                    }.setOnClickListener {
                        if (quantity.text.isNullOrBlank())
                            snackbar(parentLayout!!, getString(R.string.enter_valid_quantity))
                        else
                            addToCart(product, quantity.text.toString().toInt())
                    }
                }
            }
        }.show()
    }

    private fun addToCart(item: String, quantity: Int) {
        snackbar(parentLayout!!, "Entered $quantity")
    }

    private fun showLoadingIndicator() {
        loadingIndicator.visibility = View.VISIBLE
        loadingIndicator.show()
    }

    private fun hideLoadingIndicator() {
        loadingIndicator.visibility = View.GONE
        loadingIndicator.hide()
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