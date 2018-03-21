package com.mobile.wanda.promoter.activity

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
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
import org.jetbrains.anko.alert
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.selector
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton

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

    private fun showResults(productResults: ProductResults) {
        productsAdapter = ProductsAdapter(productResults, object : ProductsAdapter.ClickListener {
            override fun onProductSelected(product: Product) {
                loadProducts(product.id)
            }
        })
        products.adapter = productsAdapter
    }

    private fun loadProducts(id: Int) {
        if (NetworkHelper.isOnline(this)) {
            showLoadingIndicator()

            disposable.add(
                    restInterface.searchProducts(id)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                hideLoadingIndicator()

                                showProducts(it)
                            }) {
                                hideLoadingIndicator()
                                ErrorHandler.showError(it)
                            }
            )
        } else
            snackbar(parentLayout, getString(R.string.network_unavailable))
    }

    private fun showProducts(products: ProductResults) {
        val options = products.items.map { it.name }

        selector("Payment Options", options, { _, i ->
            toast("Clicked ${options[i]}")
        })
    }

    private fun showLoadingIndicator() {
        loadingIndicator.visibility = View.VISIBLE
        loadingIndicator.smoothToShow()
    }

    private fun hideLoadingIndicator() {
        loadingIndicator.visibility = View.GONE
        loadingIndicator.smoothToHide()
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