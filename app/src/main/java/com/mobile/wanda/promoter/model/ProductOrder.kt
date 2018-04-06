package com.mobile.wanda.promoter.model

import com.google.gson.annotations.SerializedName
import com.mobile.wanda.promoter.model.orders.Product

data class ProductOrder(

	@field:SerializedName("amount")
	val amount: Int? = null,

	@field:SerializedName("product")
	val product: Product? = null,

	@field:SerializedName("quantity")
	val quantity: Int? = null,

	@field:SerializedName("product_id")
	val productId: Int? = null,

	@field:SerializedName("currency")
	val currency: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("tier_id")
	val tierId: Int? = null,

	@field:SerializedName("product_name")
	val productName: String? = null,

	@field:SerializedName("order_id")
	val orderId: Int? = null
)