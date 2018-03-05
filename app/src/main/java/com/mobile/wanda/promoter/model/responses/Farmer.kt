package com.mobile.wanda.promoter.model.responses

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by kombo on 28/01/2018.
 */
open class Farmer(@PrimaryKey
                  @SerializedName("id") var id: Long? = 0,
                  @SerializedName("name") var name: String? = null) : RealmObject()