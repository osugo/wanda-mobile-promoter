package com.mobile.wanda.promoter.activity.orders

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mobile.wanda.promoter.R
import com.mobile.wanda.promoter.Wanda
import com.mobile.wanda.promoter.activity.BaseActivity
import com.mobile.wanda.promoter.adapter.OrderReviewAdapter
import com.mobile.wanda.promoter.event.ErrorEvent
import com.mobile.wanda.promoter.model.OrderPlacementRequest
import com.mobile.wanda.promoter.model.PendingOrder
import com.mobile.wanda.promoter.rest.ErrorHandler
import com.mobile.wanda.promoter.rest.RestClient
import com.mobile.wanda.promoter.rest.RestInterface
import com.mobile.wanda.promoter.util.NetworkHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import kotlinx.android.synthetic.main.cart_review.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.*

/**
 * Created by kombo on 25/03/2018.
 */
class CartReview : BaseActivity(), View.OnClickListener {

    private var orderId: Int? = null
    private var pendingOrder: PendingOrder? = null
    private val disposable = CompositeDisposable()
    private lateinit var alertDialog: DialogInterface

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
    }

    /**
     * Load items from cart onto recycler
     */
    private fun loadCart(orderString: String) {
        pendingOrder = getPendingOrder(orderString)
        orderId = pendingOrder?.details?.id

        if (pendingOrder != null) {
            val adapter = OrderReviewAdapter(pendingOrder!!.details!!.items!!)
            recyclerView.adapter = adapter

            itemsCost.text = "${pendingOrder!!.details!!.itemsCost}"
            deliveryCost.text = "${pendingOrder!!.details!!.estimateDeliveryCost}"
            total.text = "${pendingOrder!!.details!!.totalCost}"
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
        alertDialog = alert {
            customView {
                verticalLayout {
                    textView("Would you like the delivery to be made to the farm?") {
                        textSize = 18f
                        textColor = Color.BLACK
                        padding = dip(17)
                    }

                    linearLayout {
                        button("No") {
                            textColor = Color.WHITE
                            background = ContextCompat.getDrawable(this@CartReview, R.color.colorPrimary)
                            typeface = Typeface.createFromAsset(this@CartReview.assets, "fonts/PT_Sans-Web-Bold.ttf")
                        }.lparams(width = dip(0), height = wrapContent) {
                            weight = 1f
                            rightMargin = dip(1)
                        }.setOnClickListener {
                            placeOrder(0)
                            alertDialog.dismiss()
                        }

                        button("Yes") {
                            textColor = Color.WHITE
                            background = ContextCompat.getDrawable(this@CartReview, R.color.colorPrimary)
                            typeface = Typeface.createFromAsset(this@CartReview.assets, "fonts/PT_Sans-Web-Bold.ttf")
                        }.lparams(width = dip(0), height = wrapContent) {
                            weight = 1f
                        }.setOnClickListener {
                            placeOrder(1)
                            alertDialog.dismiss()
                        }
                    }.lparams(width = matchParent, height = wrapContent) {
                        weightSum = 2f
                    }
                }
            }
        }.show()
    }

    private fun placeOrder(shouldDeliver: Int) {
        if (NetworkHelper.isOnline(this)) {
            if (!isFinishing) {
                showLoadingDialog()

                disposable.add(
                        restInterface.placeOrder(OrderPlacementRequest(pendingOrder!!.details!!.farmer!!.id!!, shouldDeliver))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    hideLoadingDialog()
                                    showMessage(it)
                                }) {
                                    hideLoadingDialog()
                                    ErrorHandler.showError(it)
                                }
                )
            }
        }
    }

    private fun showMessage(pendingOrder: PendingOrder) {
        if (pendingOrder.error == null) {
            alert(pendingOrder.message!!) {
                yesButton {
                    startActivity(intentFor<ProcessOrder>(ProcessOrder.ORDER to Gson().toJson(pendingOrder)))
                }
            }.show()
        } else {
            alert(pendingOrder.errorData!!.message!!) {
                yesButton {
                    it.dismiss()
                }
            }.show()
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
    }
}