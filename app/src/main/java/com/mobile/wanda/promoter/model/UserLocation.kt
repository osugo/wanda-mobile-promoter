package com.mobile.wanda.promoter.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by kombo on 20/03/2018.
 */
open class UserLocation(
        @PrimaryKey var id: Int? = 0,
        var latitude: String? = null,
        var longitude: String? = null
) : RealmObject()