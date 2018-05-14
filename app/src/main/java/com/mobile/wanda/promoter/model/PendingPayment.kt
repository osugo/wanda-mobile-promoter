package com.mobile.wanda.promoter.model

import com.google.gson.annotations.SerializedName
import com.mobile.wanda.promoter.model.responses.Farmer

data class PendingPayment(

	@field:SerializedName("promoter_id")
	var promoterId: Int? = null,

	@field:SerializedName("farmer_id")
	var farmerId: Int? = null,

	@field:SerializedName("payment_id")
	var paymentId: Int? = null,

	@field:SerializedName("farmer")
	var farmer: Farmer? = null,

	@field:SerializedName("payment")
	var payment: Payment? = null,

	@field:SerializedName("id")
	var id: Int? = null
)