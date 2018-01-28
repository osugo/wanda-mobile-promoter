package com.mobile.wanda.promoter.model.requests

import com.google.gson.annotations.SerializedName
import com.mobile.wanda.promoter.model.responses.Ward
import io.realm.RealmList

/**
 * Created by kombo on 28/01/2018.
 */
data class WardList (@SerializedName("data") val wards: RealmList<Ward>)