package com.mobile.wanda.promoter.model

import com.google.gson.annotations.SerializedName

data class CreatedAt(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("timezone")
	val timezone: String? = null,

	@field:SerializedName("timezone_type")
	val timezoneType: Int? = null
)