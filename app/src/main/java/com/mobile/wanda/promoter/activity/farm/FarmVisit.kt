package com.mobile.wanda.promoter.activity.farm

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.View
import com.github.florent37.rxgps.RxGps
import com.google.gson.Gson
import com.mobile.wanda.promoter.R
import com.mobile.wanda.promoter.Wanda
import com.mobile.wanda.promoter.activity.BaseActivity
import com.mobile.wanda.promoter.activity.Home
import com.mobile.wanda.promoter.event.ErrorEvent
import com.mobile.wanda.promoter.model.UserLocation
import com.mobile.wanda.promoter.model.errors.FarmReviewError
import com.mobile.wanda.promoter.model.requests.FarmReview
import com.mobile.wanda.promoter.model.responses.FarmReviewResponse
import com.mobile.wanda.promoter.rest.ErrorHandler
import com.mobile.wanda.promoter.rest.RestClient
import com.mobile.wanda.promoter.rest.RestInterface
import com.mobile.wanda.promoter.util.NetworkHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.exceptions.RealmException
import kotlinx.android.synthetic.main.farm_visit.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.alert
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.yesButton

/**
 * Created by kombo on 25/03/2018.
 */
class FarmVisit : BaseActivity(), View.OnClickListener {

    private val compositeDisposable = CompositeDisposable()

    private var farmId: Int? = null

    private val disposable = CompositeDisposable()

    private val restInterface by lazy {
        RestClient.client.create(RestInterface::class.java)
    }

    companion object {
        val TAG: String = FarmVisit::class.java.simpleName
        const val FARM_ID = "farm_id"
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
        setContentView(R.layout.farm_visit)

        farmId = intent.getIntExtra(FARM_ID, 0)

        tryRetrieveLocation()

        submit.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (NetworkHelper.isOnline(this)) {
            val review = comments.text.toString()

            if (review.isEmpty()) {
                snackbar(parentLayout, "Please enter a review to proceed")
            }

            var location: String? = null

            try {
                Realm.getInstance(Wanda.INSTANCE.realmConfig()).use {
                    val loc = it.where(UserLocation::class.java).findFirst()

                    loc?.let {
                        location = "${loc.latitude},${loc.longitude}"
                    }
                }
            }catch (e: RealmException){
                Log.e(TAG, e.localizedMessage, e)
            }

            if(location != null){
                Log.e(TAG, "Location is not null")
            }

            if (review.isNotEmpty()) {
                if (!isFinishing) {
                    showLoadingDialog()
                    disposable.add(
                            restInterface.addFarmVisit(FarmReview(farmId, location ?: "1.2345, 3.5667", review))
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({
                                        hideLoadingDialog()

                                        if (!isFinishing)
                                            showMessage(it)
                                    }) {
                                        hideLoadingDialog()
                                        ErrorHandler.showError(it)
                                    }
                    )
                }
            }
        } else {
            snackbar(parentLayout, getString(R.string.network_unavailable))
        }
    }

    /**
     * Build and display error message
     */
    private fun showMessage(farmReviewResponse: FarmReviewResponse) {
        if (farmReviewResponse.error == null) {
            alert(farmReviewResponse.message!!) {
                yesButton {
                    startActivity(intentFor<Home>().clearTop())
                    it.dismiss()
                }
            }.show()
        } else {
            alert(buildMessage(farmReviewResponse.farmReviewError!!)){
                yesButton {
                    it.dismiss()
                }
            }.show()
        }
    }

    /**
     * Build display message based on errors returned
     */
    private fun buildMessage(farmReviewError: FarmReviewError): StringBuilder {
        val message = StringBuilder()

        if (farmReviewError.farmId != null)
            message.append(farmReviewError.farmId!![0]).append("\n \n")

        if (farmReviewError.location != null)
            message.append(farmReviewError.location!![0]).append("\n \n")

        if (farmReviewError.comments != null)
            message.append(farmReviewError.comments!![0]).append("\n \n")

        return message
    }

    /**
     * Get the current location
     */
    private fun tryRetrieveLocation() {
        //check if location permission is enabled and request if not
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), FarmCreation.PERMISSION_REQUEST_CODE)
                Log.e(FarmCreation.TAG, "Location permission not permitted")
            } else {
                //retrieve location
                Log.e(FarmCreation.TAG, "Location permission permitted")
                getLocation()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Retrieve user location in background
     */
    private fun getLocation() {
        RxGps(this).locationLowPower()
                .doOnSubscribe(this::addDisposable)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val location = it
                    Log.e("Location", Gson().toJson(location))

                    Realm.getInstance(Wanda.INSTANCE.realmConfig()).use {
                        it.executeTransaction { it.copyToRealmOrUpdate(UserLocation(0, location.latitude.toString(), location.longitude.toString())) }
                    }
                }) {
                    when (it) {
                        is RxGps.PermissionException -> ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), FarmCreation.PERMISSION_REQUEST_CODE)
                        is RxGps.PlayServicesNotAvailableException -> snackbar(parentLayout!!, "Google Play Services is not available. Unable to retrieve location")
                    }
                }
    }

    private fun addDisposable(disposable: Disposable) {
        compositeDisposable.add(disposable)
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

        hideLoadingDialog()
        disposable.clear()
    }
}