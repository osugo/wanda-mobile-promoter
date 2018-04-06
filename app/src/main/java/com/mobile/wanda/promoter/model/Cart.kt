package com.mobile.wanda.promoter.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by kombo on 25/03/2018.
 */
open class Cart (@PrimaryKey var id: Long? = 0, var items: RealmList<CartItem>? = RealmList()) : RealmObject()