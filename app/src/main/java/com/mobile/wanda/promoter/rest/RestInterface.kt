package com.mobile.wanda.promoter.rest

import com.mobile.wanda.promoter.model.*
import io.reactivex.Completable
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Created by kombo on 23/11/2017.
 */

interface RestInterface {

    @get:GET("vouchers/balance")
    val voucherBalance: Observable<VoucherBalance>

    @POST("http://35.176.127.140/wanda-mobile/oauth/token")
    fun login(@Body loginCredentials: LoginCredentials): Observable<AuthCredentials>

    @POST("/farmers/registration") //TODO need to know format of payload
    fun registerFarmer(@Body registrationDetails: FarmerRegistrationDetails): Completable

    //TODO need to know payload to add farm and return type and the likes

    @GET("/products/categories?search={searchTerm}")
    fun searchProductCategories(@Path("searchTerm") searchTerm: String): Observable<ProductResults>

    @GET("/products/products?search={searchTerm}&category_id={categoryId}") //TODO I need sample result data to create return type
    fun searchProducts(@Path("searchTerm") searchTerm: String, @Path("category_id") categoryId: Int): Observable<ProductResults>

    @GET("/products/variations?search={searchTerm}&category_id={categoryId}") //TODO I need sample result data to create return type
    fun getProductVariations(@Path("searchTerm") searchTerm: String, @Path("category_id") categoryId: Int): Observable<ProductResults>

    @POST("farmers/orders/create")  //TODO this returns a 404. Need to check on this
    fun placeOrder(@Body order: Order): Completable

    @POST("payments/pay-for-order")
    fun payOrder(@Body orderPayment: OrderPayment): Observable<PaymentResponse>

    @POST("payments/voucher-top-up")
    fun voucherTopUp(@Body voucherTopUpRequest: VoucherTopUpRequest): Observable<PaymentResponse>

    @GET("commissions/check")
    fun checkCommission(): Observable<Commission>

    @POST("commissions/request-payment")
    fun requestCommission(): Observable<CommissionRequestResponse>
}
