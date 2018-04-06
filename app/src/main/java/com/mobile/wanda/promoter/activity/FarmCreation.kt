package com.mobile.wanda.promoter.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
import com.github.florent37.rxgps.RxGps
import com.mobile.wanda.promoter.R
import com.mobile.wanda.promoter.Wanda
import com.mobile.wanda.promoter.fragment.FarmCreationFragment
import com.mobile.wanda.promoter.fragment.FarmersList
import com.mobile.wanda.promoter.model.UserLocation
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import kotlinx.android.synthetic.main.main_layout.*
import org.jetbrains.anko.design.snackbar

/**
 * Created by kombo on 05/03/2018.
 */
class FarmCreation : BaseActivity(), FarmersList.SelectionListener {

    private val compositeDisposable = CompositeDisposable()

    companion object {
        val TAG: String = FarmCreation::class.java.simpleName
        const val PERMISSION_REQUEST_CODE = 12
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        tryRetrieveLocation()

        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.contentFrame, FarmersList())
                    .commitAllowingStateLoss()
        }
    }

    /**
     * Get the current location
     */
    private fun tryRetrieveLocation() {
        //check if location permission is enabled and request if not
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE)
                Log.e(TAG, "Location permission not permitted")
            } else {
                //retrieve location
                Log.e(TAG, "Location permission permitted")
                getLocation()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Show farm report fragment after selecting farmer
     */
    override fun onFarmerSelected(id: Long, name: String) {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.contentFrame, FarmCreationFragment.newInstance(id))
                .commitAllowingStateLoss()
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
                    Realm.getInstance(Wanda.INSTANCE.realmConfig()).use {
                        it.executeTransaction { it.copyToRealmOrUpdate(UserLocation(0, location.latitude.toString(), location.longitude.toString())) }
                    }
                }) {
                    when (it) {
                        is RxGps.PermissionException -> ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE)
                        is RxGps.PlayServicesNotAvailableException -> snackbar(parentLayout!!, "Google Play Services is not available. Unable to retrieve location")
                    }
                }
    }

    /**
     * Confirm status of location permission. Keep asking until user allows
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation()
            } else {
                snackbar(parentLayout, "Location permission denied.").setAction("Allow", {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE)
                })
            }
        }
    }

    private fun addDisposable(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }
}