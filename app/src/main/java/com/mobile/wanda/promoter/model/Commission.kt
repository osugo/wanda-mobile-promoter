package com.mobile.wanda.promoter.model

import com.google.gson.annotations.SerializedName

/**
 * Created by kombo on 29/11/2017.
 */
data class Commission(
        @SerializedName("commission")
        val commission: Long
)