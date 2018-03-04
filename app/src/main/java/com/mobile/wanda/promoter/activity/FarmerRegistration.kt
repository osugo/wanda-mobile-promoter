package com.mobile.wanda.promoter.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import com.mobile.wanda.promoter.R
import com.mobile.wanda.promoter.Wanda
import com.mobile.wanda.promoter.model.errors.FarmerRegistrationErrors
import com.mobile.wanda.promoter.model.requests.FarmerRegistrationDetails
import com.mobile.wanda.promoter.model.requests.WardList
import com.mobile.wanda.promoter.model.responses.FarmerRegistrationResponse
import com.mobile.wanda.promoter.model.responses.Ward
import com.mobile.wanda.promoter.rest.ErrorHandler
import com.mobile.wanda.promoter.rest.RestClient
import com.mobile.wanda.promoter.rest.RestInterface
import com.mobile.wanda.promoter.rest.RetrofitException
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.realm.Case
import io.realm.Realm
import kotlinx.android.synthetic.main.farmer_registration_layout.*
import org.apache.commons.lang3.text.WordUtils
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.alert
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.yesButton


/**
 * Created by kombo on 03/01/2018.
 */
class FarmerRegistration : AppCompatActivity(), View.OnClickListener, AnkoLogger {

    private val disposable = CompositeDisposable()

    private val restInterface by lazy {
        RestClient.client.create(RestInterface::class.java)
    }

    companion object {
        val TAG: String = FarmerRegistration::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.farmer_registration_layout)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        submit.setOnClickListener(this)

        val adapter = ArrayAdapter<String>(this, android.R.layout.select_dialog_item, getWards())
        ward.threshold = 1
        ward.setAdapter(adapter)
    }

    private fun getWards(): ArrayList<String> {
        val items = ArrayList<String>()

        Realm.getInstance(Wanda.INSTANCE.realmConfig()).use {
            val wardList = it.where(WardList::class.java).findFirst()

            wardList?.let {
                if(it.wards.isNotEmpty()){
                    it.wards.forEach({
                        items.add(WordUtils.capitalizeFully(it.name!!))
                    })
                }
            }
        }

        return items
    }

    /**
     * Easier to work backwards from name to obtain ward
     */
    private fun getWard(name: String): Ward? {
        Log.e(TAG, "Searching for $name")

        var ward: Ward? = null

        Realm.getInstance(Wanda.INSTANCE.realmConfig()).use {
            val result = it.where(Ward::class.java).equalTo("name", name, Case.INSENSITIVE).findFirst()

            if(result != null){
                ward = it.copyFromRealm(result)
            }
        }

        return ward
    }

    /**
     * Click listener for @Submit button
     */
    override fun onClick(v: View?) {
        Log.e(TAG,"Ward is ${getWard(ward.text.toString())?.name} with ID: ${getWard(ward.text.toString())?.id}")

        val name = farmerName.text
        val phone = phoneNumber.text
        val farmerWard = ward.text
        val farmerCollectionCenter = collectionCenter.text

        //check if name field is empty
        if (isEmpty(name)) {
            showSnackbar("Please enter the farmer's name.")
            return
        }

        //check if phone number field is empty
        if (isEmpty(phone)) {
            showSnackbar("Please enter a valid phone number.")
            return
        }

        //check if ward details are provided
        if (isEmpty(farmerWard)) {
            showSnackbar("Please provide the farmer's ward")
            return
        }

        //check if collection center details are provided
        if (isEmpty(farmerCollectionCenter)) {
            showSnackbar("Please enter a valid collection center")
            return
        }

        Log.e(TAG,"Ward is ${getWard(farmerWard.toString())?.name} with ID: ${getWard(farmerWard.toString())?.id}")

        //send details to server for processing
        if (!isEmpty(name) && !isEmpty(phone) && !isEmpty(farmerWard) && !isEmpty(farmerCollectionCenter)) {
            val dialog = indeterminateProgressDialog("Please wait")

            disposable.add(
                    restInterface.registerFarmer(FarmerRegistrationDetails(name.toString(), phone.toString(), farmerCollectionCenter.toString(), farmerWard.toString()))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                dialog.dismiss()
                                showMessage(it)
                            }, {
                                dialog.dismiss()
                                Log.e(TAG, it.localizedMessage, it)
                                showSnackbar(ErrorHandler.getExceptionMessage(it as RetrofitException))
                            })
            )
        }
    }

    /**
     * Show appropriate message of transaction; whether success or failure
     */
    private fun showMessage(farmerRegistrationResponse: FarmerRegistrationResponse) {
        if (farmerRegistrationResponse.error)
            alert(buildMessage(farmerRegistrationResponse.registrationErrors!!), "Error") {
                yesButton { it.dismiss() }
            }.show()
        else
            alert(getString(R.string.farmer_reg_successful), null) {
                yesButton {
                    it.dismiss()
                    finish()
                }
            }.show()
    }

    /**
     * Build display message based on errors returned
     */
    private fun buildMessage(registrationErrors: FarmerRegistrationErrors): StringBuilder {
        val message = StringBuilder()

        if (registrationErrors.name != null)
            message.append(registrationErrors.name[0]).append("\n \n")

        if (registrationErrors.phone != null)
            message.append(registrationErrors.phone[0]).append("\n \n")

        if (registrationErrors.ward != null)
            message.append(registrationErrors.ward[0]).append("\n \n")

        if (registrationErrors.collectionCenter != null)
            message.append(registrationErrors.collectionCenter[0].replace("-", " "))

        return message
    }

    /**
     * Validation check to ensure that fields are not blank
     */
    private fun isEmpty(charSequence: CharSequence): Boolean {
        return charSequence.isBlank() or charSequence.isEmpty()
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

    override fun onDestroy() {
        super.onDestroy()

        disposable.dispose()
    }
}