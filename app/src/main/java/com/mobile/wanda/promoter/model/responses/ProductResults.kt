package com.mobile.wanda.promoter.model.responses

import com.google.gson.annotations.SerializedName
import com.mobile.wanda.promoter.model.Product

/**
 * Created by kombo on 27/11/2017.
 */
data class ProductResults(
        @SerializedName("data")
        val products: ArrayList<Product>
)