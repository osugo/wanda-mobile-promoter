package com.mobile.wanda.promoter.model

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject

open class County(

        @field:SerializedName("updated_at")
        var updatedAt: String? = null,

        @field:SerializedName("name")
        var name: String? = null,

        @field:SerializedName("created_at")
        var createdAt: String? = null,

        @field:SerializedName("id")
        var id: Int? = null
) : RealmObject()