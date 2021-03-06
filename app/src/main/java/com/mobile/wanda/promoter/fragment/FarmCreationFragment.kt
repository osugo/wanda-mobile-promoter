package com.mobile.wanda.promoter.fragment

import android.location.Location
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.mobile.wanda.promoter.R
import com.mobile.wanda.promoter.Wanda
import com.mobile.wanda.promoter.activity.FarmerRegistration
import com.mobile.wanda.promoter.activity.Home
import com.mobile.wanda.promoter.event.ErrorEvent
import com.mobile.wanda.promoter.model.Ward
import com.mobile.wanda.promoter.model.errors.FarmAuditErrors
import com.mobile.wanda.promoter.model.requests.FarmDetails
import com.mobile.wanda.promoter.model.requests.WardList
import com.mobile.wanda.promoter.model.responses.FarmCreationResponse
import com.mobile.wanda.promoter.rest.ErrorHandler
import com.mobile.wanda.promoter.rest.RestClient
import com.mobile.wanda.promoter.rest.RestInterface
import com.mobile.wanda.promoter.util.NetworkHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.realm.Case
import io.realm.Realm
import org.apache.commons.lang3.text.WordUtils
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.find
import org.jetbrains.anko.singleTop
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.indeterminateProgressDialog
import org.jetbrains.anko.support.v4.intentFor
import org.jetbrains.anko.yesButton

/**
 * Created by kombo on 05/03/2018.
 */

//TODO test with actual location received from device
class FarmCreationFragment : Fragment(), View.OnClickListener {

    private var location: Location? = null
    private var farmerId: Long? = null

    private var submit: Button? = null
    private var ward: AutoCompleteTextView? = null
    private var farmSize: EditText? = null
    private var description: EditText? = null
    private var parentLayout: RelativeLayout? = null

    private val compositeDisposable = CompositeDisposable()

    private val restInterface by lazy {
        RestClient.client.create(RestInterface::class.java)
    }

    companion object {
        private val TAG: String = FarmCreationFragment::class.java.simpleName
        private const val ID: String = "id"

        fun newInstance(id: Long): FarmCreationFragment {
            val bundle = Bundle().apply {
                putLong(ID, id)
            }

            val fragment = FarmCreationFragment()
            fragment.arguments = bundle

            return fragment
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onErrorEvent(errorEvent: ErrorEvent) {
        if (!activity!!.isFinishing)
            alert(errorEvent.message, null) {
                yesButton {
                    it.dismiss()
                }
            }.show()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.add_farm, container, false)

        submit = view.find(R.id.submit) as Button
        ward = view.find(R.id.ward) as AutoCompleteTextView
        description = view.find(R.id.description) as EditText
        farmSize = view.find(R.id.farmSize) as EditText
        parentLayout = view.find(R.id.parentLayout) as RelativeLayout

        arguments?.let {
            farmerId = it.getLong(ID, 0)
        }

        submit?.setOnClickListener(this)

        val adapter = ArrayAdapter<String>(activity, android.R.layout.select_dialog_item, getWards())
        ward?.threshold = 1
        ward?.setAdapter(adapter)

        return view
    }

    private fun getWards(): ArrayList<String> {
        val items = ArrayList<String>()

        Realm.getInstance(Wanda.INSTANCE.realmConfig()).use {
            val wardList = it.where(WardList::class.java).findFirst()

            wardList?.let {
                if (it.wards.isNotEmpty()) {
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
        Log.e(FarmerRegistration.TAG, "Searching for $name")

        var ward: Ward? = null

        Realm.getInstance(Wanda.INSTANCE.realmConfig()).use {
            val result = it.where(Ward::class.java).equalTo("name", name, Case.INSENSITIVE).findFirst()

            if (result != null) {
                ward = it.copyFromRealm(result)
            }
        }

        return ward
    }

    /**
     * Click listener for the submit button
     */
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.submit -> {
                val size = farmSize?.text.toString()
                val desc = description?.text.toString()
                val farmerWard = ward?.text.toString()

                //check if size field is empty
                if (isEmpty(size)) {
                    showSnackbar("Please enter farm size.")
                    return
                }

                //check if ward details are provided
                if (isEmpty(desc)) {
                    showSnackbar("Please enter a description of the farm")
                    return
                }

                if (isEmpty(farmerWard)) {
                    showSnackbar("Please choose a ward to proceed")
                }

//                Log.e("Size", size)
//                Log.e("Desc", desc)
//                Log.e("Ward", farmerWard)

//        if (location == null) {
//            Log.e(TAG, "Getting location")
//            getLocation()
//        }

                if (size.isNotEmpty() && desc.isNotEmpty() && getWard(farmerWard) != null) {
                    if (NetworkHelper.isOnline(activity!!)) {
//                val locale = "${location!!.latitude}, ${location!!.longitude}"
                        val locale = "1.235, 3.567"

                        Log.e(TAG, "Here")

                        if (!activity!!.isFinishing) {
                            val dialog = indeterminateProgressDialog("Please wait")
                            compositeDisposable.add(
                                    restInterface.addFarm(FarmDetails(farmerId!!.toInt(), getWard(farmerWard)!!.id!!.toInt(), size, desc, locale))
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe({
                                                dialog.dismiss()
                                                showMessage(it)
                                            }) {
                                                dialog.dismiss()
                                                Log.e(TAG, it.localizedMessage, it)

                                                ErrorHandler.showError(it)
                                            }
                            )
                        }
                    } else
                        snackbar(parentLayout!!, getString(R.string.network_unavailable))
                }
            }
        }
    }

    /**
     * Show appropriate message of transaction; whether success or failure
     */
    private fun showMessage(farmCreationResponse: FarmCreationResponse) {
        if (farmCreationResponse.error != null) {
            if (!activity!!.isFinishing)
                alert(buildMessage(farmCreationResponse.farmAuditErrors!!).toString(), "Error") {
                    yesButton { it.dismiss() }
                }.show()
        } else {
            if (!activity!!.isFinishing)
                alert(farmCreationResponse.message!!, null) {
                    yesButton {
                        it.dismiss()

                        startActivity(intentFor<Home>().singleTop())
                    }
                }.show()
        }
    }

    /**
     * Build display message based on errors returned
     */
    private fun buildMessage(farmAuditErrors: FarmAuditErrors): StringBuilder {
        val message = StringBuilder()

        if (farmAuditErrors.userId != null)
            message.append(farmAuditErrors.userId[0]).append("\n \n")

        if (farmAuditErrors.wardId != null)
            message.append(farmAuditErrors.wardId[0]).append("\n \n")

        if (farmAuditErrors.description != null)
            message.append(farmAuditErrors.description[0]).append("\n \n")

        if (farmAuditErrors.size != null)
            message.append(farmAuditErrors.size[0]).append("\n \n")

        if (farmAuditErrors.location != null)
            message.append(farmAuditErrors.location[0]).append("\n \n")

        return message
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
        snackbar(parentLayout!!, message)
    }

    override fun onDestroy() {
        super.onDestroy()

        compositeDisposable.dispose()
    }
}