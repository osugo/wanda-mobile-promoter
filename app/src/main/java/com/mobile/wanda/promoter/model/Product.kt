package com.mobile.wanda.promoter.model

import com.google.gson.annotations.SerializedName

/**
 * Created by kombo on 27/11/2017.
 */
data class Product(
        @SerializedName("id")
        val id: Int,
        @SerializedName("name")
        val name: String
)