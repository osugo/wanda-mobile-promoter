package com.mobile.wanda.promoter.model.requests

import com.google.gson.annotations.SerializedName
import com.mobile.wanda.promoter.model.responses.Farmer
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by kombo on 28/01/2018.
 */
open class FarmerList(
        @PrimaryKey
        var id: Long = 0,
        @SerializedName("orderDetails") var farmers: RealmList<Farmer> = RealmList()) : RealmObject()