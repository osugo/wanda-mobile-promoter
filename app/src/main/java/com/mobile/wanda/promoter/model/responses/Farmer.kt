package com.mobile.wanda.promoter.model.responses

import com.google.gson.annotations.SerializedName

/**
 * Created by kombo on 28/01/2018.
 */
data class Farmer(@SerializedName("id") val id: Long, @SerializedName("name") val name: String)