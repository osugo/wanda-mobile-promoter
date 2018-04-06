package com.mobile.wanda.promoter.model.orders

import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject

/**
 * Created by kombo on 08/03/2018.
 */

data class ProductResults(
        @SerializedName("data") val items: ArrayList<Product>
)

data class Product(

        @SerializedName("name")
        val name: String? = null,

        @SerializedName("description")
        val description: String? = null,

        @SerializedName("id")
        val id: Long? = null
)

open class Order(
        @SerializedName("farmer_id") var farmerId: Long? = 0,
        @SerializedName("items") var items: RealmList<OrderItem>? = RealmList()
) : RealmObject()

open class OrderItem(
        @SerializedName("product_id") var variationId: Long? = 0,
        @SerializedName("quantity") var quantity: Int? = 0
) : RealmObject()
