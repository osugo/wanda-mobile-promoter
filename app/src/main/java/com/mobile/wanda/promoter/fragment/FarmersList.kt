package com.mobile.wanda.promoter.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import co.moonmonkeylabs.realmsearchview.RealmSearchView
import com.mobile.wanda.promoter.R
import com.mobile.wanda.promoter.Wanda
import com.mobile.wanda.promoter.adapter.FarmersAdapter
import com.mobile.wanda.promoter.event.ErrorEvent
import com.mobile.wanda.promoter.model.Cart
import com.mobile.wanda.promoter.model.requests.FarmerList
import com.mobile.wanda.promoter.model.responses.Farmer
import com.mobile.wanda.promoter.rest.ErrorHandler
import com.mobile.wanda.promoter.rest.RestClient
import com.mobile.wanda.promoter.rest.RestInterface
import com.mobile.wanda.promoter.util.NetworkHelper
import com.wang.avi.AVLoadingIndicatorView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.exceptions.RealmException
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by kombo on 04/03/2018.
 */
class FarmersList : Fragment() {

    private val disposable = CompositeDisposable()
    private var farmersAdapter: FarmersAdapter? = null
    private var callback: SelectionListener? = null

    private var loadingIndicator: AVLoadingIndicatorView? = null
    private var retry: Button? = null
    private var farmerSearchView: RealmSearchView? = null
    private var errorLayout: LinearLayout? = null
    private var errorText: TextView? = null

    private val restInterface by lazy {
        RestClient.client.create(RestInterface::class.java)
    }

    private val realm by lazy {
        Realm.getInstance(Wanda.INSTANCE.realmConfig())
    }

    companion object {
        val TAG: String = FarmersList::class.java.simpleName
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onErrorEvent(errorEvent: ErrorEvent) {
        toggleViews()

        errorText?.text = errorEvent.message
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.farmers, container, false)

        initViews(view)

        clearCart()

        getFarmers()

        retry?.setOnClickListener {
            farmerSearchView?.visibility = View.GONE
            errorLayout?.visibility = View.GONE
            loadingIndicator?.visibility = View.VISIBLE

            getFarmers()
        }

        return view
    }

    /**
     * Remove all items from cart and begin afresh
     */
    private fun clearCart() {
        Realm.getInstance(Wanda.INSTANCE.realmConfig()).use {
            val items = it.where(Cart::class.java).findAll()

            if (items.isNotEmpty())
                it.executeTransaction {
                    items.deleteAllFromRealm()
                }
        }
    }

    /**
     * Initialize views
     */
    private fun initViews(v: View) {
        retry = v.findViewById(R.id.retry) as Button
        errorText = v.findViewById(R.id.errorText) as TextView
        errorLayout = v.findViewById(R.id.errorLayout) as LinearLayout
        farmerSearchView = v.findViewById(R.id.farmerSearchView) as RealmSearchView
        loadingIndicator = v.findViewById(R.id.loadingIndicator) as AVLoadingIndicatorView
    }

    /**
     * Load farmers from network
     */
    private fun getFarmers() {
        if (NetworkHelper.isOnline(activity)) {
            loadingIndicator?.visibility = View.VISIBLE
            loadingIndicator?.smoothToShow()

            disposable.add(
                    restInterface.getFarmers()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ farmers ->
                                try {
                                    Realm.getInstance(Wanda.INSTANCE.realmConfig()).use {
                                        it.executeTransaction {
                                            it.copyToRealmOrUpdate(farmers)
                                        }
                                    }
                                } catch (e: RealmException) {
                                    Log.e(TAG, e.localizedMessage, e)
                                } finally {
                                    showFarmers()
                                }
                            }) {
                                ErrorHandler.showError(it)
                            }
            )
        } else {
            toggleViews()

            errorText?.text = getString(R.string.network_unavailable)
        }
    }

    /**
     * load retrieved farmers into view
     */
    private fun showFarmers() {
        if (!realm.isClosed) {
            loadingIndicator?.visibility = View.GONE
            loadingIndicator?.smoothToHide()

            farmerSearchView?.visibility = View.VISIBLE

            val farmersList = realm.where(FarmerList::class.java).findFirst()

            if (farmersList?.farmers!!.isNotEmpty()) {
                farmersAdapter = FarmersAdapter(activity, realm, "name", object : FarmersAdapter.ClickListener {
                    override fun onItemClicked(farmer: Farmer) {
                        callback?.onFarmerSelected(farmer.id!!, farmer.name!!)
                    }
                })
                farmerSearchView?.setAdapter(farmersAdapter)
            }
        }
    }

    /**
     * Hide views and show error state
     */
    private fun toggleViews() {
        loadingIndicator?.visibility = View.GONE
        farmerSearchView?.visibility = View.GONE

        errorLayout?.visibility = View.VISIBLE
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        try {
            callback = context as SelectionListener
        } catch (c: ClassCastException) {
            throw ClassCastException(String.format("%s must implement SelectionListener", context.toString()))
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        disposable.dispose()
        realm?.close()
    }

    interface SelectionListener {
        fun onFarmerSelected(id: Long, name: String)
    }
}