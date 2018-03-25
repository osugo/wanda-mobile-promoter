package com.mobile.wanda.promoter.model.orders

import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey

/**
 * Created by kombo on 08/03/2018.
 */

data class ProductResults(
        @SerializedName("data") val items: ArrayList<Product>
)

data class Product(
        @SerializedName("id") val id: Long,
        @SerializedName("name") val name: String
)

open class Order(
        @SerializedName("farmer_id") var farmerId: Long? = 0,
        @SerializedName("items") var items: RealmList<OrderItem>? = RealmList()
) : RealmObject()

open class OrderItem(
        @SerializedName("variation_id") var variationId: Long? = 0,
        @SerializedName("quantity") var quantity: Int? = 0
) : RealmObject()

open class PendingOrder(
        @Ignore
        @SerializedName("error") var error: Boolean? = false,
        @SerializedName("message") var message: String? = null,
        @PrimaryKey
        @SerializedName("order_id") var orderId: Long? = 0,
        @SerializedName("status") var status: String? = null
) : RealmObject()