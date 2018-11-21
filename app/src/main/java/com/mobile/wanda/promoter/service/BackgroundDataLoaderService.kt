package com.mobile.wanda.promoter.service

import android.app.IntentService
import android.content.Intent
import android.util.Log
import com.mobile.wanda.promoter.Wanda
import com.mobile.wanda.promoter.network.RestClient
import com.mobile.wanda.promoter.network.RestInterface
import com.mobile.wanda.promoter.util.NetworkHelper
import io.reactivex.schedulers.Schedulers
import io.realm.Realm

/**
 * Created by kombo on 28/01/2018.
 */
class BackgroundDataLoaderService: IntentService(TAG) {

    companion object {
        val GET_WARDS: String = "com.mobile.wanda.promoter.LOAD_WARDS"
        val TAG: String = BackgroundDataLoaderService::class.java.simpleName
    }

    override fun onHandleIntent(intent: Intent?) {
        if(!NetworkHelper.isOnline(this)) return

        if(intent == null) return

        (intent.action == GET_WARDS).let {
            RestClient.client.create(RestInterface::class.java)
                    .getWards
                    .subscribeOn(Schedulers.io())
                    .subscribe({ wardList ->
                        Realm.getInstance(Wanda.INSTANCE.realmConfig()).use {
                            it.executeTransaction {
                                it.copyToRealmOrUpdate(wardList)
                            }
                        }
                    }, {
                        Log.e(TAG, it.localizedMessage, it)
                    })
        }
    }
}