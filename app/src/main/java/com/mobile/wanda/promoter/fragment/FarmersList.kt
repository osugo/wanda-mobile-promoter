package com.mobile.wanda.promoter.fragment

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.mobile.wanda.promoter.R
import com.mobile.wanda.promoter.Wanda
import com.mobile.wanda.promoter.adapter.FarmerAdapter
import com.mobile.wanda.promoter.event.ErrorEvent
import com.mobile.wanda.promoter.model.requests.FarmerList
import com.mobile.wanda.promoter.model.responses.Farmer
import com.mobile.wanda.promoter.rest.ErrorHandler
import com.mobile.wanda.promoter.rest.RestClient
import com.mobile.wanda.promoter.rest.RestInterface
import com.mobile.wanda.promoter.util.NetworkHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.find

/**
 * Created by kombo on 04/03/2018.
 */
class FarmersList : Fragment(), SearchView.OnQueryTextListener {

    private val disposable = CompositeDisposable()
    private var farmerAdapter: FarmerAdapter? = null
    private var callback: SelectionListener? = null

    private var loadingIndicator: ProgressBar? = null
    private var retry: Button? = null
    private var errorLayout: LinearLayout? = null
    private var errorText: TextView? = null
    private var recyclerView: RecyclerView? = null
    private var searchView: SearchView? = null

    private val restInterface by lazy {
        RestClient.client.create(RestInterface::class.java)
    }

    private val realm by lazy {
        Realm.getInstance(Wanda.INSTANCE.realmConfig())
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onErrorEvent(errorEvent: ErrorEvent) {
        toggleViews()

        errorText?.text = errorEvent.message
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.farmer_list, container, false)

        initViews(view)

        getFarmers()

        retry?.setOnClickListener {
            recyclerView?.visibility = View.GONE
            errorLayout?.visibility = View.GONE
            loadingIndicator?.visibility = View.VISIBLE

            getFarmers()
        }

        return view
    }

    /**
     * Initialize views
     */
    private fun initViews(v: View) {
        retry = v.find(R.id.retry) as Button
        errorText = v.find(R.id.errorText) as TextView
        searchView = v.find(R.id.searchView) as SearchView
        errorLayout = v.find(R.id.errorLayout) as LinearLayout
        recyclerView = v.find(R.id.recyclerView) as RecyclerView
        loadingIndicator = v.find(R.id.loadingIndicator) as ProgressBar

        recyclerView?.layoutManager = LinearLayoutManager(activity)

        initSearchView()
    }

    private fun initSearchView() {
        val searchManager = activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView?.setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
        searchView?.setOnQueryTextListener(this)
    }

    override fun onQueryTextSubmit(query: String?): Boolean = false

    override fun onQueryTextChange(newText: String?): Boolean {
        farmerAdapter?.filter?.filter(newText)
        return true
    }

    /**
     * Load farmers from network
     */
    private fun getFarmers() {
        if (NetworkHelper.isOnline(activity as AppCompatActivity)) {
            loadingIndicator?.visibility = View.VISIBLE

            disposable.add(
                    restInterface.getFarmers()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                showFarmers(it)
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
    private fun showFarmers(farmerList: FarmerList) {
        if (!realm.isClosed) {
            loadingIndicator?.visibility = View.GONE

            recyclerView?.visibility = View.VISIBLE

            farmerAdapter = FarmerAdapter(farmerList.farmers!!, object : FarmerAdapter.ClickListener {
                override fun onItemClicked(farmer: Farmer) {
                    callback?.onFarmerSelected(farmer.id!!, farmer.name!!)
                }
            })
            recyclerView?.adapter = farmerAdapter
        }
    }

    /**
     * Hide views and show error state
     */
    private fun toggleViews() {
        loadingIndicator?.visibility = View.GONE
        recyclerView?.visibility = View.GONE

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