package com.mobile.wanda.promoter.model

import com.google.gson.annotations.SerializedName

data class Payment(

	@field:SerializedName("reference")
	val reference: String? = null,

	@field:SerializedName("amount")
	val amount: Int? = null,

	@field:SerializedName("currency")
	val currency: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("status")
	val status: String? = null
)