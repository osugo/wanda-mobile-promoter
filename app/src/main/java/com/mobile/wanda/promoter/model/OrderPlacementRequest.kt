package com.mobile.wanda.promoter.model

import com.google.gson.annotations.SerializedName

/**
 * Created by kombo on 06/04/2018.
 */
class OrderPlacementRequest(@SerializedName("farmer_id") val farmerId: Long, @SerializedName("deliver") val deliver: Int)