package com.mobile.wanda.promoter.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mobile.wanda.promoter.R
import com.mobile.wanda.promoter.Wanda
import com.mobile.wanda.promoter.adapter.FarmersAdapter
import com.mobile.wanda.promoter.event.ErrorEvent
import com.mobile.wanda.promoter.model.requests.FarmerList
import com.mobile.wanda.promoter.rest.ErrorHandler
import com.mobile.wanda.promoter.rest.RestClient
import com.mobile.wanda.promoter.rest.RestInterface
import com.mobile.wanda.promoter.util.NetworkHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.exceptions.RealmException
import kotlinx.android.synthetic.main.error_layout.*
import kotlinx.android.synthetic.main.farmers.*
import kotlinx.android.synthetic.main.loading_indicator.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by kombo on 04/03/2018.
 */
class FarmersList : Fragment() {

    private val disposable = CompositeDisposable()
    private var farmersAdapter: FarmersAdapter? = null

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

        errorText.text = errorEvent.message
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.farmers, container, false)

        getFarmers()

        retry?.setOnClickListener {
            farmerSearchView.visibility = View.GONE
            errorLayout.visibility = View.GONE
            loadingIndicator.visibility = View.VISIBLE

            getFarmers()
        }

        return view
    }

    /**
     * Load farmers from network
     */
    private fun getFarmers() {
        if (NetworkHelper.isOnline(activity))
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
        else {
            toggleViews()

            errorText.text = getString(R.string.network_unavailable)
        }
    }

    /**
     * load retrieved farmers into view
     */
    private fun showFarmers() {
        if (!realm.isClosed) {
            loadingIndicator.visibility = View.GONE
            farmerSearchView.visibility = View.VISIBLE

            val farmersList = realm.where(FarmerList::class.java).findFirst()

            if (farmersList?.farmers!!.isNotEmpty()) {
                farmersAdapter = FarmersAdapter(activity, realm, "name")
                farmerSearchView.setAdapter(farmersAdapter)
            }
        }
    }

    /**
     * Hide views and show error state
     */
    private fun toggleViews() {
        loadingIndicator.visibility = View.GONE
        farmerSearchView.visibility = View.GONE

        errorLayout.visibility = View.VISIBLE
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
        realm?.close()
    }
}