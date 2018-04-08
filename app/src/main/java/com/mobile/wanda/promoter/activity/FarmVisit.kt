package com.mobile.wanda.promoter.activity

import android.os.Bundle
import android.view.View
import com.mobile.wanda.promoter.R
import com.mobile.wanda.promoter.event.ErrorEvent
import com.mobile.wanda.promoter.model.errors.FarmReviewError
import com.mobile.wanda.promoter.model.requests.FarmReview
import com.mobile.wanda.promoter.model.responses.FarmReviewResponse
import com.mobile.wanda.promoter.rest.ErrorHandler
import com.mobile.wanda.promoter.rest.RestClient
import com.mobile.wanda.promoter.rest.RestInterface
import com.mobile.wanda.promoter.util.NetworkHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
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

    private var farmId: Int? = null
    private var location: String? = null

    private val disposable = CompositeDisposable()

    private val restInterface by lazy {
        RestClient.client.create(RestInterface::class.java)
    }

    companion object {
        const val FARM_ID = "farm_id"
        const val LOCATION = "location"
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
        location = intent.getStringExtra(LOCATION)

        submit.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (NetworkHelper.isOnline(this)) {
            val review = comments.text

            if (review.isEmpty()) {
                snackbar(parentLayout, "Please enter a review to proceed")
            }

            if (review.isNotEmpty()) {
                if (!isFinishing) {
                    showLoadingDialog()
                    disposable.add(
                            restInterface.addFarmVisit(FarmReview(farmId, location))
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