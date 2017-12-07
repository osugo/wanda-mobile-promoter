package com.mobile.wanda.promoter.model

import com.google.gson.annotations.SerializedName

/**
 * Created by kombo on 27/11/2017.
 */
data class ProductResults(
        @SerializedName("data")
        val products: ArrayList<Product>
)