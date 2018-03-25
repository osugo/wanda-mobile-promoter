package com.mobile.wanda.promoter.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by kombo on 25/03/2018.
 */
open class CartItem(@PrimaryKey var id: Long? = 0, var name: String? = null, var quantity: Int? = 0) : RealmObject()