package com.mobile.wanda.promoter.model.requests

import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by kombo on 28/01/2018.
 */
open class WardList(
        @PrimaryKey var id: Int = 0,
        @SerializedName("data") var wards: RealmList<Ward> = RealmList()) : RealmObject()