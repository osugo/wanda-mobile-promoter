package com.mobile.wanda.promoter.activity

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import com.github.florent37.rxgps.RxGps
import com.mobile.wanda.promoter.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.farm_audit.*
import org.jetbrains.anko.design.snackbar

/**
 * Created by kombo on 03/01/2018.
 */
class FarmAudit : AppCompatActivity(), View.OnClickListener {

    private lateinit var location: Location

    companion object {
        val PERMISSION_REQUEST_CODE = 234
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.farm_audit)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        submit.setOnClickListener(this)

        //check if location permission is enabled and request if not
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE)
            } else {
                //retrieve locatoin
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
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    location = it
//                    toast("${location.latitude}")
                }, {
                    if (it is RxGps.PermissionException)
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE)
                    else if (it is RxGps.PlayServicesNotAvailableException)
                        snackbar(parentLayout, "Google Play Services is not available. Unable to retrieve location")
                })
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

    /**
     * Click listener for the submit button
     */
    override fun onClick(v: View?) {
        val name = farmerName.text.toString()
        val size = farmSize.text.toString()
        val desc = description.text.toString()

        //check if name field is empty
        if (isEmpty(name)) {
            showSnackbar("Please enter the farmer's name.")
            return
        }

        //check if phone number field is empty
        if (isEmpty(size)) {
            showSnackbar("Please enter farm size.")
            return
        }

        //check if ward details are provided
        if (isEmpty(desc)) {
            showSnackbar("Please enter a description of the farm")
            return
        }


    }

    /**
     * Validation check to ensure that fields are not blank
     */
    private fun isEmpty(string: String): Boolean {
        return string.isBlank() or string.isEmpty()
    }

    /**
     * Shows SnackBar with appropriate message at bottom of screen
     */
    private fun showSnackbar(message: String) {
        snackbar(parentLayout, message)
    }

    /**
     * Listener for hardware back button press
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> false
        }
    }
}